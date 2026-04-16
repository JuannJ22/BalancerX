# Runbook de despliegue y escalamiento (Producción + Pruebas paralelas)

## Objetivo
Desplegar BalancerX en servidor con:
1. **Nueva URL de producción**.
2. **La misma estructura de base de datos** en una instancia de **pruebas paralela**.
3. Cero interrupciones al ambiente actual mientras se escala.

---

## Principios de arquitectura y operación
- **Separación de ambientes**: cada ambiente tiene su propia base de datos, rutas de archivos y configuración.
- **Promoción controlada**: primero pruebas en `STAGING`, luego promoción a `PRODUCTION`.
- **Cambios reversibles**: cada despliegue debe poder volver atrás (rollback).
- **Trazabilidad**: versionar scripts SQL y configuraciones por ambiente.

---

## Topología recomendada

| Ambiente | URL | Base de datos | Objetivo |
|---|---|---|---|
| Producción actual | `https://balancerx.tu-dominio.com` | `BalancerX` | Operación estable actual |
| Producción nueva (cutover) | `https://api-nueva.tu-dominio.com` | `BalancerX_Prod` | Operación principal futura |
| Pruebas paralelas | `https://staging-api-nueva.tu-dominio.com` | `BalancerX_Staging` | Pruebas de cambios y escalamiento |

> Puedes conservar `BalancerX` como legado y operar la nueva URL sobre `BalancerX_Prod` para no arriesgar el ambiente que hoy funciona.

---

## Paso 1: Preparar bases de datos aisladas

### 1.1 Crear DB de pruebas paralelas
1. Crear base `BalancerX_Staging`.
2. Aplicar el script completo `database/recreate.sql`.
3. (Opcional) Cargar data anonimizada para pruebas reales.

### 1.2 Crear DB de producción nueva
1. Crear base `BalancerX_Prod`.
2. Aplicar el script completo `database/recreate.sql`.
3. Si necesitas continuidad histórica, migrar datos desde la base actual en ventana controlada.

> Regla: **nunca** apuntar dos ambientes a la misma base en paralelo.

---

## Paso 2: Configurar la API por ambiente

BalancerX soporta configuración por ambiente con `appsettings.{Environment}.json`.

- `src/BalancerX.Api/appsettings.Staging.json`
- `src/BalancerX.Api/appsettings.Production.json`

Ajusta al menos:
- `ConnectionStrings:SqlServer`
- `Jwt:Key` (secreta y distinta por ambiente)
- `Storage:TransferenciasPath`
- `Storage:FirmasPath`

**Aislamiento obligatorio de almacenamiento:**
- Producción: `D:\BalancerX_Prod_Secure\...`
- Staging: `D:\BalancerX_Staging_Secure\...`

---

## Paso 3: Publicar en servidor sin interrumpir el sistema actual

1. Publicar build en una carpeta independiente del despliegue actual.
2. Levantar la API nueva en puerto distinto (`ASPNETCORE_URLS`).
3. Configurar reverse proxy (IIS/Nginx) para la nueva URL.
4. Ejecutar pruebas de humo sobre staging.
5. Promover a producción nueva.

### Checklist mínimo previo a go-live
- Login válido con usuario de prueba.
- Crear transferencia.
- Subir y descargar PDF.
- Impresión y reimpresión.
- Confirmar que la instancia antigua sigue operativa.

---

## Paso 4: Estrategia de cutover segura

1. Congelar cambios funcionales brevemente.
2. Respaldar base de datos actual.
3. Sincronizar última data requerida hacia `BalancerX_Prod`.
4. Cambiar DNS/Proxy a nueva URL.
5. Monitorear errores y rendimiento 30-60 min.
6. Mantener rollback listo hacia URL/instancia anterior.

---

## Paso 5: Escalamiento posterior (sin romper SOLID)

### Aplicación
- Mantener capas actuales (Api / Application / Domain / Infrastructure).
- Extraer integraciones externas detrás de interfaces (`Application/Contratos`).
- Evitar lógica de negocio en controladores.

### Datos
- Versionar migraciones SQL por archivo incremental (`database/alter_v*.sql`).
- Aplicar primero en `Staging`, luego en `Production`.
- Agregar índices por patrón de consulta real.

### Operación
- Centralizar logs (Serilog sink a archivo/servidor).
- Agregar health checks y alertas.
- Definir RPO/RTO y política de backups.

---

## Variables operativas sugeridas

- `ASPNETCORE_ENVIRONMENT=Staging` (o `Production`)
- `ASPNETCORE_URLS=http://0.0.0.0:5080` (staging) / `:5000` (prod)

No guardar credenciales reales en repositorio; usar variables de entorno o secreto del servidor.

---

## Riesgos comunes y mitigación

1. **Compartir rutas de PDFs entre ambientes**  
   Mitigación: rutas físicas separadas por ambiente.

2. **Usar misma JWT key en todos los ambientes**  
   Mitigación: claves distintas y rotación controlada.

3. **Desplegar sin rollback**  
   Mitigación: snapshot/backup + versión anterior disponible.

4. **Aplicar SQL directo en producción**  
   Mitigación: ensayo previo y scripts idempotentes.

---

## Resultado esperado
Con este flujo tendrás:
- una URL nueva operando en infraestructura controlada,
- un ambiente de pruebas paralelo para escalar sin interrupciones,
- base sólida para seguir evolucionando el sistema de forma profesional.
