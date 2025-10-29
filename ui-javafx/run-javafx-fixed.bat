@echo off
echo Ejecutando BalancerX con Java 24...
"C:\Users\Usuario\.jdks\temurin-24\bin\java.exe" -cp "target/classes;target/dependency/*" com.balancerx.BalancerXDesktopApp
if %ERRORLEVEL% NEQ 0 (
    echo Error al ejecutar la aplicacion. Codigo de salida: %ERRORLEVEL%
    pause
) else (
    echo Aplicacion ejecutada exitosamente.
)