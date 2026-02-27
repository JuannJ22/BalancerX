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
2. Crear transferencia en `POST /api/transferencias` (incluye `bancoId` y `cuentaContableId`).
3. Subir PDF en `POST /api/transferencias/{id}/archivo`.
4. Descargar PDF por API en `GET /api/transferencias/{id}/archivo`.
5. Imprimir una sola vez en `POST /api/transferencias/{id}/print`.
6. Reimprimir solo ADMIN en `POST /api/transferencias/{id}/reprint` con PIN y razón.
7. Actualizar transferencia (solo ADMIN) en `PUT /api/transferencias/{id}`.
8. Administrar usuarios (solo ADMIN) en `/api/usuarios` (listar/crear/eliminar).
9. Eliminar PDF o transferencia (solo ADMIN) en `DELETE /api/transferencias/{id}/archivo` y `DELETE /api/transferencias/{id}`.

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
  -d '{"monto":15000.50,"puntoVentaId":1,"vendedorId":1,"bancoId":1,"cuentaContableId":1,"observacion":"Transferencia semanal"}'
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


## Troubleshooting rápido

### 1) `/swagger` devuelve 404
- Verifica entorno local en Development:
  - `set ASPNETCORE_ENVIRONMENT=Development`
- Si el puerto 5000 está ocupado, usa otro:
  - `set ASPNETCORE_URLS=http://localhost:5080`

### 2) Login falla con error de columnas (`Invalid column name 'Activo'`, `Id`, `Nombre`)
- Ese error sucede cuando la base está en collation case-sensitive y el mapeo no coincide.
- Esta rama ya incluye mapeo explícito de columnas en `BalancerXDbContext`.

### 3) Login falla por conexión SQL remota
- Si usas servidor en red con usuario `sa`, **no** uses `Trusted_Connection=True`.
- Usa cadena con `User ID` y `Password`, por ejemplo:
  - `Server=tcp:192.168.5.10,14330;Database=BalancerX;User ID=sa;Password=***;TrustServerCertificate=True;Encrypt=False`


### Actualizar transferencia (solo ADMIN)
```bash
curl -X PUT http://localhost:5000/api/transferencias/1 \
  -H "Authorization: Bearer <TOKEN_ADMIN>" \
  -H "Content-Type: application/json" \
  -d '{"monto":18000.00,"puntoVentaId":1,"vendedorId":2,"bancoId":1,"cuentaContableId":1,"observacion":"Ajuste autorizado","estado":"CREADA"}'
```


### Crear usuario (solo ADMIN)
```bash
curl -X POST http://localhost:5000/api/usuarios \
  -H "Authorization: Bearer <TOKEN_ADMIN>" \
  -H "Content-Type: application/json" \
  -d '{"usuario":"operador1","password":"Operador123*","rol":"AUXILIAR","pinAdmin":null,"firmaElectronica":"OPERADOR 1"}'
```

### Eliminar PDF de una transferencia (solo ADMIN)
```bash
curl -X DELETE http://localhost:5000/api/transferencias/1/archivo \
  -H "Authorization: Bearer <TOKEN_ADMIN>"
```

### Eliminar transferencia (solo ADMIN)
```bash
curl -X DELETE http://localhost:5000/api/transferencias/1 \
  -H "Authorization: Bearer <TOKEN_ADMIN>"
```

## Script SQL incremental recomendado
Si ya tienes la BD creada, ejecuta: `database/alter_v2_admin_bancos_firma.sql`
