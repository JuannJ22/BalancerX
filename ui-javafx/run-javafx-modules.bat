@echo off
echo Ejecutando BalancerX con modulos JavaFX...
"C:\Users\Usuario\.jdks\temurin-24\bin\java.exe" ^
  --module-path "target/dependency" ^
  --add-modules javafx.controls,javafx.fxml ^
  --add-opens javafx.fxml/javafx.fxml=ALL-UNNAMED ^
  --add-opens javafx.controls/javafx.scene.control=ALL-UNNAMED ^
  --add-opens javafx.graphics/javafx.scene=ALL-UNNAMED ^
  -cp "target/classes" ^
  com.balancerx.BalancerXDesktopApp
if %ERRORLEVEL% NEQ 0 (
    echo Error al ejecutar la aplicacion. Codigo de salida: %ERRORLEVEL%
    pause
) else (
    echo Aplicacion ejecutada exitosamente.
)