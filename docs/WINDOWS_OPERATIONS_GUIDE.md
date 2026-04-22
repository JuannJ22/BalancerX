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

También puedes ejecutar el diagnóstico integral incluido en el repositorio:

```powershell
powershell -ExecutionPolicy Bypass -File .\deploy\windows\diagnose-connectivity.ps1 `
  -ServiceName "BalancerX.Api" `
  -Urls "http://0.0.0.0:5000"
```

Este script revisa estado del servicio, variables de entorno efectivas, puerto en escucha, sonda local (`/login.html`) y regla de firewall esperada.

## 10.1 Diagnóstico cuando aparece "Error 5: Acceso denegado" al iniciar el servicio

Si Windows Services muestra **"Windows no pudo iniciar ... Error 5: Acceso denegado"**, normalmente el problema está en permisos/cuenta del servicio, no en la red.

Checklist recomendado (en este orden):

1. Revisar cuenta efectiva del servicio:
   ```powershell
   Get-CimInstance Win32_Service -Filter "Name='BalancerX.Api'" | Select-Object Name, StartName, State, ExitCode
   ```
2. Si `StartName` es una cuenta de dominio/local (no `LocalSystem`), validar privilegio **Iniciar sesión como servicio**.
3. Validar ACL de ejecución y lectura sobre el binario publicado:
   ```powershell
   Get-Acl "C:\apps\balancerx\current\BalancerX.Api.exe" | Format-List
   ```
4. Validar permisos de la carpeta raíz de releases (`C:\apps\balancerx\releases`) y del release activo.
5. Revisar eventos de `Service Control Manager` para causa exacta:
   ```powershell
   Get-WinEvent -LogName System -MaxEvents 80 |
     Where-Object { $_.ProviderName -eq 'Service Control Manager' } |
     Select-Object TimeCreated, Id, LevelDisplayName, Message -First 20
   ```

Nota: `install-service.ps1` ya imprime este diagnóstico de forma automática cuando `Start-Service` falla, para acelerar el análisis en primer despliegue.

## 10.2 Diagnóstico cuando Windows muestra "No se puede acceder al dispositivo/ruta/archivo especificado" al abrir `BalancerX.Api.exe`

Si al hacer doble clic sobre `C:\apps\balancerx\current\BalancerX.Api.exe` aparece el cuadro:

- **"Windows no puede acceder al dispositivo, la ruta de acceso o el archivo especificados"**

entonces el problema suele estar en **ACL/bloqueo del ejecutable**, no en ASP.NET ni en la API.

Checklist recomendado (en este orden):

1. Verificar que el ejecutable realmente existe en el `current` activo:
   ```powershell
   Get-Item "C:\apps\balancerx\current\BalancerX.Api.exe" | Select-Object FullName, Length, LastWriteTime
   ```
2. Confirmar que `current` apunta a un release válido (junction/symlink):
   ```powershell
   Get-Item "C:\apps\balancerx\current" | Select-Object FullName, LinkType, Target
   ```
3. Revisar ACL y confirmar permisos de **Read & Execute** para la cuenta que ejecuta (usuario interactivo y/o cuenta del servicio):
   ```powershell
   icacls "C:\apps\balancerx\current\BalancerX.Api.exe"
   icacls "C:\apps\balancerx\current"
   ```

## 10.3 Diagnóstico cuando `/api/transferencias/{id}/print` devuelve error por PDF en Windows

Si el endpoint de impresión responde 400 con detalle similar a:

- **"No hay una aplicación PDF asociada para impresión en Windows..."**

la causa no es el flujo de negocio ni el login, sino la infraestructura de impresión del servidor.

Checklist recomendado:

1. Verificar visor PDF instalado en el servidor (Adobe Reader / SumatraPDF / Edge).
2. Confirmar asociación predeterminada para `.pdf` en la sesión de Windows del servidor.
3. Preferir siempre impresión por comando explícito (sin depender de UI):
   ```json
   "Printing": {
     "CommandTemplate": "SumatraPDF.exe -print-to-default \"{file}\""
   }
   ```
4. Si se requiere impresora específica:
   ```json
   "Printing": {
     "PrinterName": "NOMBRE_IMPRESORA",
     "CommandTemplate": "AcroRd32.exe /t \"{file}\" \"{printer}\""
   }
   ```

Buenas prácticas de arquitectura:

- Mantener la lógica de impresión encapsulada por contrato (`IPrintService`) y reportar errores con motivo técnico explícito (archivo inexistente, comando fallido, asociación PDF faltante).
- Evitar que el backend dependa de sesiones interactivas de escritorio.
- En escenarios de alta criticidad, migrar a cola de impresión (job queue + reintentos + auditoría).
4. Si el archivo fue copiado desde internet/ZIP, quitar bloqueo de seguridad (Mark-of-the-Web):
   ```powershell
   Get-Item "C:\apps\balancerx\current\BalancerX.Api.exe" -Stream Zone.Identifier -ErrorAction SilentlyContinue
   Unblock-File "C:\apps\balancerx\current\BalancerX.Api.exe"
   ```
5. Verificar que no esté siendo bloqueado por antivirus/EDR (Microsoft Defender u otro):
   ```powershell
   Get-MpThreatDetection | Select-Object InitialDetectionTime, Resources, ThreatName -First 10
   ```
6. Probar ejecución por consola para obtener error más explícito:
   ```powershell
   cd C:\apps\balancerx\current
   .\BalancerX.Api.exe
   ```
7. Si falla igual, reconstruir release limpio y reapuntar `current`:
   - publicar en una carpeta nueva bajo `releases\`;
   - validar ejecución manual del `.exe`;
   - luego actualizar `current`.

> Práctica recomendada: no ejecutar la API con doble clic en producción. Ejecutar y diagnosticar desde PowerShell (o como servicio) para tener salida de error completa y trazable.

---

## 11) Flujo exacto para evitar errores (qué correr y cuándo)

Esta es la secuencia recomendada para un pase profesional a servidor principal, sin mezclar responsabilidades:

### Opción recomendada: flujo one-shot con un solo script

Si quieres ejecutar todo con un único comando (inicialización o actualización, según corresponda):

```powershell
powershell -ExecutionPolicy Bypass -File .\deploy\windows\bootstrap-and-run.ps1 `
  -ServiceName "BalancerX.Api" `
  -ProjectPath ".\src\BalancerX.Api\BalancerX.Api.csproj" `
  -BasePath "C:\apps\balancerx" `
  -Environment "Production" `
  -Urls "http://0.0.0.0:5000"
```

Comportamiento:
- Si el servicio no existe: publish inicial + enlace `current` + instalación + arranque + diagnóstico.
- Si el servicio existe: deploy-release + reinicio controlado + diagnóstico.

### Escenario A: primera instalación en el servidor

1. **Compilar/publicar en equipo de build o en el mismo servidor**:
   ```powershell
   dotnet publish .\src\BalancerX.Api\BalancerX.Api.csproj -c Release -r win-x64 --self-contained true -o C:\apps\balancerx\releases\bootstrap
   ```
2. Crear/actualizar junction `current` al release publicado.
3. Ejecutar **una sola vez** `install-service.ps1`:
   ```powershell
   powershell -ExecutionPolicy Bypass -File .\deploy\windows\install-service.ps1 `
     -ServiceName "BalancerX.Api" `
     -PublishedExePath "C:\apps\balancerx\current\BalancerX.Api.exe" `
     -Environment "Production" `
     -Urls "http://0.0.0.0:5000"
   ```

> En este escenario **no** se usa `dotnet run` para producción. El servicio Windows debe ejecutar el binario publicado (`BalancerX.Api.exe`).

### Escenario B: actualización de versión en servidor ya instalado

Con el servicio ya creado, para nuevas versiones ejecuta **solo**:

```powershell
powershell -ExecutionPolicy Bypass -File .\deploy\windows\deploy-release.ps1 `
  -ServiceName "BalancerX.Api" `
  -ProjectPath ".\src\BalancerX.Api\BalancerX.Api.csproj" `
  -BasePath "C:\apps\balancerx" `
  -Environment "Production" `
  -Urls "http://0.0.0.0:5000"
```

Este script ya hace publish, stop/start del servicio, actualización de `current`, variables y firewall.

### ¿Cuándo usar `dotnet run`?

- Solo para **desarrollo local** o pruebas rápidas.
- No usar `dotnet run` como estrategia de ejecución permanente en producción.

### Checklist mínimo post-ejecución

```powershell
sc.exe query BalancerX.Api
Invoke-WebRequest http://127.0.0.1:5000/swagger -UseBasicParsing
Get-NetFirewallRule -DisplayName "BalancerX.Api TCP 5000" | Format-Table DisplayName, Enabled, Direction, Action
```
