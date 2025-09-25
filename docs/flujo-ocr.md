1. El usuario sube el PDF del cuadre.
2. El `FileStoragePort` guarda el archivo y calcula checksum SHA-256.
3. `Tess4JOcrPipeline` lee el PDF (PDFBox) y analiza el orden de secciones.
4. Se generan observaciones con severidades INFO/WARNING/ERROR.
5. El caso de uso `EnviarCuadre` bloquea el envío si existen observaciones ERROR.
6. Las observaciones quedan disponibles para el auditor en la bandeja JavaFX.
