# Guía operativa segura (Windows) para BalancerX

> Objetivo: instalar, versionar y operar BalancerX en producción sin romper el sistema actual.

## 1) Modelo operativo recomendado

Separar en 3 ambientes:

- **DEV**: desarrollo local.
- **STAGING**: validación previa (misma arquitectura que producción).
- **PRODUCTION**: ambiente estable para usuarios.

Regla de oro: **todo cambio pasa por STAGING antes de PRODUCTION**.

---

## 2) Estructura de carpetas en servidor

Usa una estructura fija para despliegues por versión:

```txt
C:\apps\balancerx\
  releases\
    20260417-153000\
    20260420-221500\
  current -> junction al release activo
  logs\
```

Ventaja:
- rollback inmediato cambiando `current` al release anterior.
- cada release es inmutable y auditable.

---

## 3) Instalación inicial (solo una vez)

### 3.1 Prerrequisitos
- Windows Server actualizado.
- .NET Runtime/SDK instalado (según estrategia de publish).
- SQL Server accesible.
- Cuenta de servicio dedicada (recomendado), por ejemplo: `DOMINIO\\svc_balancerx`.
- Carpetas seguras para PDFs:
  - `D:\BalancerX_Prod_Secure\Transferencias`
  - `D:\BalancerX_Prod_Secure\Firmas`

### 3.2 Configuración de producción
Editar `appsettings.Production.json` con:
- `ConnectionStrings:SqlServer` apuntando a `BalancerX_Prod`.
- `Jwt:Key` fuerte y única.
- rutas de `Storage` de producción.

### 3.3 Instalar servicio

```powershell
powershell -ExecutionPolicy Bypass -File .\deploy\windows\install-service.ps1 `
  -ServiceName "BalancerX.Api" `
  -PublishedExePath "C:\apps\balancerx\current\BalancerX.Api.exe" `
  -Environment "Production" `
  -Urls "http://0.0.0.0:5000" `
  -ServiceUser "DOMINIO\svc_balancerx" `
  -ServicePassword "<SECRETO>"
```

Validar:

```powershell
sc.exe query BalancerX.Api
Invoke-WebRequest http://127.0.0.1:5000/swagger -UseBasicParsing
```

> Nota operativa: el script `install-service.ps1` ahora crea automáticamente una regla de firewall de entrada por cada puerto configurado en `-Urls` (por ejemplo, `BalancerX.Api TCP 5000`). Esto evita el caso típico donde el servicio inicia, pero la IP de red (`http://<IP_SERVIDOR>:5000`) queda inaccesible por timeout.

---

## 4) Cómo manejar versiones de producción sin dañar

## 4.1 Convención de versión
Usa versión semántica y etiqueta Git por release:
- `v1.4.0`, `v1.4.1`, `v1.5.0`

Reglas:
- **PATCH** (`x.x.1`): bugfix sin cambios de contrato.
- **MINOR** (`x.1.x`): nuevas funcionalidades compatibles.
- **MAJOR** (`1.x.x`): cambios incompatibles.

## 4.2 Flujo de promoción
1. Merge a rama de integración.
2. Deploy en STAGING.
3. Ejecutar pruebas de humo y checklist funcional.
4. Aprobación operativa.
5. Tag de release en Git.
6. Deploy en PRODUCTION con ventana controlada.

## 4.3 Deploy de nueva versión (producción)

```powershell
powershell -ExecutionPolicy Bypass -File .\deploy\windows\deploy-release.ps1 `
  -ServiceName "BalancerX.Api" `
  -ProjectPath ".\src\BalancerX.Api\BalancerX.Api.csproj" `
  -BasePath "C:\apps\balancerx" `
  -Environment "Production" `
  -Urls "http://0.0.0.0:5000"
```

El script realiza:
- publish a `releases/<timestamp>`.
- detiene servicio.
- actualiza `current`.
- actualiza `binPath` + variables de entorno.
- garantiza regla de firewall para los puertos definidos en `-Urls`.
- inicia servicio.

## 4.4 Checklist post-release (obligatorio)
- `sc.exe query BalancerX.Api` => `RUNNING`.
- Login funciona.
- Crear transferencia.
- Subir y descargar PDF.
- Imprimir/reimprimir.
- Revisar logs de aplicación y Windows Event Viewer.

---

## 5) Estrategia de base de datos segura

## 5.1 Nunca ejecutar SQL directo sin ensayo
Proceso:
1. aplicar script en STAGING;
2. validar consultas y tiempos;
3. respaldar PRODUCTION;
4. aplicar en PRODUCTION;
5. validar endpoints críticos.

## 5.2 Orden recomendado por release
1. Backup de `BalancerX_Prod`.
2. Deploy de aplicación (si no rompe contrato DB).
3. Migración SQL (o antes, según compatibilidad).
4. Smoke tests.
5. Monitoreo 30-60 min.

> Si hay cambios no backward-compatible, prepara modo mantenimiento corto y rollback documentado.

---

## 6) Rollback de emergencia (menos de 5 minutos)

1. `Stop-Service BalancerX.Api`
2. Reapuntar `current` al release previo.
3. `Start-Service BalancerX.Api`
4. Validar `/swagger` y login.
5. Si el incidente fue por SQL, restaurar backup según runbook de BD.

---

## 7) Controles para no dañar producción

- **Control de cambios**: ticket por release con alcance y riesgos.
- **Aprobación dual**: dev + responsable operativo.
- **Checklist firmado** antes y después de deploy.
- **Secretos fuera del repo** (variables de entorno o vault).
- **Monitoreo activo** con alertas de caídas y errores.

---

## 8) Rutina sugerida de operación semanal

- Revisar crecimiento de logs y limpieza controlada.
- Verificar backups restaurables (no solo que existan).
- Revisar salud del servicio (`RUNNING`, reinicios, consumo).
- Probar rollback en STAGING al menos 1 vez por mes.

---

## 9) Resumen ejecutivo

Para trabajar sin dañar todo:
1. ambientes separados,
2. releases inmutables con `current`,
3. despliegue automatizado,
4. checklist obligatorio,
5. rollback ensayado,
6. cambios SQL primero en STAGING.

---

## 10) Diagnóstico rápido cuando "no responde por IP"

Si en consola aparece `Now listening on http://0.0.0.0:5000` pero desde navegador remoto hay timeout:

1. Verificar localmente:
   ```powershell
   Invoke-WebRequest http://127.0.0.1:5000/login.html -UseBasicParsing
   ```
2. Verificar regla de firewall:
   ```powershell
   Get-NetFirewallRule -DisplayName "BalancerX.Api TCP 5000" | Format-Table DisplayName, Enabled, Direction, Action
   ```
3. Verificar puerto escuchando:
   ```powershell
   netstat -ano | findstr :5000
   ```
4. Si la regla no existe, re-ejecutar `install-service.ps1` o `deploy-release.ps1` con `-Urls` correcto.
