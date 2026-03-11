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
2. Ejecutar `database/recreate.sql`.
3. Revisar `src/BalancerX.Api/appsettings.json` (connection string + JWT).
4. Crear carpeta segura para PDFs:
   - `D:\BalancerX_Secure\Transferencias\`
5. Iniciar API:

```bash
dotnet restore
dotnet run --project src/BalancerX.Api
```

6. Abrir Swagger en `http://localhost:5000/swagger` (o el puerto que muestre la API).

## Usuarios de desarrollo (incluidos en recreate.sql)
> Solo para entorno local de desarrollo.

- ADMIN: `admin` / `Admin123*` (PIN: `1234`)
- TESORERIA: `tesoreria` / `Tesoreria123*`
- AUXILIAR: `auxiliar` / `Auxiliar123*` (asignado al punto de venta `1`)

## Flujo principal
1. Login en `/api/auth/login` para obtener JWT.
2. Crear transferencia en `POST /api/transferencias` (solo `ADMIN` y `TESORERIA`).
3. Subir PDF en `POST /api/transferencias/{id}/archivo`.
4. Descargar PDF por API en `GET /api/transferencias/{id}/archivo`.
5. Imprimir una sola vez en `POST /api/transferencias/{id}/print` (`ADMIN`, `TESORERIA` y `AUXILIAR`).
6. Reimprimir solo ADMIN en `POST /api/transferencias/{id}/reprint` con PIN y razón.
7. Actualizar transferencia (solo ADMIN) en `PUT /api/transferencias/{id}`.
8. Administrar usuarios (solo ADMIN) en `/api/usuarios` (listar/crear/cambiar rol/eliminar).
9. Eliminar PDF o transferencia (solo ADMIN) en `DELETE /api/transferencias/{id}/archivo` y `DELETE /api/transferencias/{id}`.

## Regla operativa de AUXILIAR
- No puede crear transferencias.
- Solo puede consultar/operar transferencias de su `punto_venta_id` asignado en `bx.users`.
- Si no tiene punto de venta asignado, el acceso queda bloqueado para operación de transferencias.

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
- Script único para recrear toda la base: `database/recreate.sql`


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


### 4) Error CS0246 con `ICatalogosSyncServicio`
- Si tu copia local mezcla archivos de commits distintos, puede aparecer este error al compilar.
- Esta base ya incluye un servicio de compatibilidad `CatalogosSyncServicio` (no-op) para evitar ese fallo.
- Si persiste, elimina carpetas `bin/` y `obj/` de toda la solución y vuelve a ejecutar `dotnet clean && dotnet restore && dotnet build`.

### Actualizar transferencia (ADMIN/TESORERIA)
```bash
curl -X PUT http://localhost:5000/api/transferencias/1 \
  -H "Authorization: Bearer <TOKEN_ADMIN>" \
  -H "Content-Type: application/json" \
  -d '{"monto":18000.00,"puntoVentaId":1,"vendedorId":2,"bancoId":1,"cuentaContableId":1,"observacion":"Ajuste autorizado","estado":"SIN_IMPRIMIR"}'
```


### Crear usuario (solo ADMIN)
```bash
curl -X POST http://localhost:5000/api/usuarios \
  -H "Authorization: Bearer <TOKEN_ADMIN>" \
  -H "Content-Type: application/json" \
  -d '{"usuario":"operador1","password":"Operador123*","rolId":3,"pinAdmin":null,"firmaElectronica":"OPERADOR 1","puntoVentaId":2}'
```

### Cambiar rol de un usuario (solo ADMIN)
```bash
curl -X PUT http://localhost:5000/api/usuarios/3/rol \
  -H "Authorization: Bearer <TOKEN_ADMIN>" \
  -H "Content-Type: application/json" \
  -d '{"rolId":2}'
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

### Ver PDF en visor (inline)
```bash
curl -X GET http://localhost:5000/api/transferencias/1/archivo/visor \
  -H "Authorization: Bearer <TOKEN>"
```


### Cambiar password del usuario autenticado
```bash
curl -X PUT http://localhost:5000/api/perfil/password \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"passwordActual":"Admin123*","passwordNueva":"Admin1234*"}'
```

### Actualizar firma electrónica del usuario autenticado
```bash
curl -X PUT http://localhost:5000/api/perfil/firma \
  -H "Authorization: Bearer <TOKEN>" \
  -F "firma=@/ruta/firma.png"
```

Las firmas se guardan en: `D:\BalancerX_Secure\Firmas\` y al subir PDF se aplica automáticamente la firma solo si el usuario tiene una firma configurada (imagen o texto). Si no tiene firma, el PDF se guarda sin firmar.

### Tutorial: cambiar la posición de los estampados en el PDF

Si en algún momento quieres mover la firma (imagen/texto) o las etiquetas (`Punto de venta`, `Vendedor`) dentro del PDF, hazlo en este método:

- `src/BalancerX.Infrastructure/Servicios/ServiciosInfraestructura.cs`
- Método: `EstamparPdf(...)`

#### 1) Ubica las coordenadas actuales

Dentro del `for` por página están estas variables base:

- `posicionX = pageSize.GetWidth() * 0.58f;`
- `posicionY = pageSize.GetHeight() * 0.10f;`

Eso ubica la firma hacia la parte inferior derecha (58% ancho, 10% alto).

También están las etiquetas arriba a la derecha:

- `infoY = pageSize.GetHeight() - 28;`
- `infoX = pageSize.GetWidth() - 24;`

#### 2) Mover la firma

La firma usa `posicionX` y `posicionY` tanto para imagen como para texto.

Regla rápida:
- Subir firma: aumenta `posicionY` (ej. de `0.10f` a `0.18f`).
- Bajar firma: reduce `posicionY`.
- Mover a la derecha: aumenta `posicionX`.
- Mover a la izquierda: reduce `posicionX`.

Ejemplo:

```csharp
var posicionX = pageSize.GetWidth() * 0.65f;
var posicionY = pageSize.GetHeight() * 0.16f;
```

#### 3) Ajustar tamaño del estampado (firma imagen)

La imagen se escala aquí:

```csharp
var imagen = new Image(imageData).ScaleToFit(pageSize.GetWidth() * 0.22f, pageSize.GetHeight() * 0.09f);
```

- Más grande: sube `0.22f` / `0.09f`.
- Más pequeño: baja esos factores.

#### 4) Mover etiquetas de punto de venta / vendedor

Las etiquetas usan esquina superior derecha con:

```csharp
var infoY = pageSize.GetHeight() - 28;
var infoX = pageSize.GetWidth() - 24;
```

- Más abajo: aumenta `- 28` (ej. `- 40`).
- Más arriba: reduce ese valor (ej. `- 18`).
- Más a la izquierda: aumenta `- 24` (ej. `- 60`).
- Más a la derecha: reduce ese margen.

#### 5) Probar y validar

1. Compila y corre API.
2. Sube un PDF de prueba en una transferencia.
3. Descarga o abre visor (`/api/transferencias/{id}/archivo/visor`).
4. Ajusta factores hasta que quede en la posición exacta deseada.

Tip práctico: trabaja con factores proporcionales (como está hoy) para que el estampado se adapte mejor a distintos tamaños de página.
