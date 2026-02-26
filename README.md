# BALANCERX - Módulo de Transferencias (MVP)

Arquitectura limpia con proyectos:
- `src/BalancerX.Api`
- `src/BalancerX.Application`
- `src/BalancerX.Domain`
- `src/BalancerX.Infrastructure`

## Stack
- ASP.NET Core Web API
- EF Core + SQL Server Express
- JWT + RBAC (`ADMIN`, `TESORERIA`, `AUXILIAR`)
- Serilog
- Swagger/OpenAPI

## Arranque rápido (local)
1. Crear base de datos `BalancerX` en SQL Server.
2. Ejecutar `database/schema.sql`.
3. Ejecutar `database/seed.sql`.
4. Revisar `src/BalancerX.Api/appsettings.json` (connection string + JWT).
5. Crear carpeta segura para PDFs:
   - `D:\BalancerX_Secure\Transferencias\`
6. Iniciar API:

```bash
dotnet restore
dotnet run --project src/BalancerX.Api
```

7. Abrir Swagger en `http://localhost:5000/swagger` (o el puerto que muestre la API).

## Usuarios de desarrollo (seed.sql)
> Solo para entorno local de desarrollo.

- ADMIN: `admin` / `Admin123*` (PIN: `1234`)
- TESORERIA: `tesoreria` / `Tesoreria123*`
- AUXILIAR: `auxiliar` / `Auxiliar123*`

## Flujo principal
1. Login en `/api/auth/login` para obtener JWT.
2. Crear transferencia en `POST /api/transferencias`.
3. Subir PDF en `POST /api/transferencias/{id}/archivo`.
4. Descargar PDF por API en `GET /api/transferencias/{id}/archivo`.
5. Imprimir una sola vez en `POST /api/transferencias/{id}/print`.
6. Reimprimir solo ADMIN en `POST /api/transferencias/{id}/reprint` con PIN y razón.

## Seguridad de archivos
Los PDFs se guardan fuera de SQL en:
`D:\BalancerX_Secure\Transferencias\YYYY\MM\`

En base de datos se guardan metadatos:
- nombre original
- ruta interna
- sha256
- tamaño
- fecha y usuario de subida

## Ejemplos de requests

### Login
```bash
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usuario":"admin","password":"Admin123*"}'
```

### Crear transferencia
```bash
curl -X POST http://localhost:5000/api/transferencias \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"monto":15000.50,"puntoVentaId":1,"vendedorId":1,"observacion":"Transferencia semanal"}'
```

### Subir PDF
```bash
curl -X POST http://localhost:5000/api/transferencias/1/archivo \
  -H "Authorization: Bearer <TOKEN>" \
  -F "archivo=@/ruta/comprobante.pdf"
```

### Imprimir
```bash
curl -X POST http://localhost:5000/api/transferencias/1/print \
  -H "Authorization: Bearer <TOKEN>"
```

### Reimprimir (solo ADMIN)
```bash
curl -X POST http://localhost:5000/api/transferencias/1/reprint \
  -H "Authorization: Bearer <TOKEN_ADMIN>" \
  -H "Content-Type: application/json" \
  -d '{"pinAdmin":"1234","razon":"Comprobante ilegible"}'
```

## Base de datos
- Script de esquema: `database/schema.sql`
- Script de datos iniciales: `database/seed.sql`
