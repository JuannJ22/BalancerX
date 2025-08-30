# Balancer (JavaFX)

Sistema de Cuadre Diario multi-sucursal (4+ puntos) con arquitectura MVC (Model, Service/Controller, ViewController, View),
persistencia cifrada en `C:\\Balancer` (AES-GCM), keystore JCEKS, control de acceso por roles/permisos y tests.

## Ejecutar
- **Desde línea de comandos**: ejecutar `./run.sh` (Linux/macOS) o `run.bat` (Windows) para iniciar la aplicación con los módulos de JavaFX necesarios.
- IntelliJ → Maven → Plugins → javafx → **javafx:run**
- Usuario inicial: **superadmin** / **admin123**
- Se crean carpetas y keystore en `C:\\Balancer` al iniciar.
