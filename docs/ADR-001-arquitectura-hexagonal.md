# ADR-001 Arquitectura Hexagonal

## Contexto

BalancerX necesita soportar múltiples adaptadores (OCR, archivos, importadores) y clientes (REST, JavaFX), manteniendo reglas de negocio independientes del framework.

## Decisión

Adoptar arquitectura hexagonal con módulos Maven dedicados:

* `domain`: entidades, agregados, servicios de dominio y puertos
* `application`: casos de uso y orquestación
* `infrastructure`: adaptadores secundarios, implementación de puertos, base de datos, OCR, almacenamiento
* `api`: adaptador primario REST
* `ui-javafx`: adaptador primario desktop

## Consecuencias

* Las reglas de negocio no dependen de Spring ni JavaFX.
* Permite pruebas unitarias aisladas de infraestructura.
* El coste inicial aumenta por la cantidad de clases y mapeos, pero se gana flexibilidad para futuras integraciones (MinIO, colas, etc.).
