# BalancerX v2

Suite de conciliación y cuadre de caja con arquitectura hexagonal y distribución multi-módulo.

## Módulos Maven

| Módulo | Descripción |
| --- | --- |
| `domain` | Modelo de dominio, agregados, eventos y contratos (ports) |
| `application` | Casos de uso y orquestadores de flujo, reglas de negocio |
| `infrastructure` | Adaptadores secundarios (JPA, OCR, importadores, almacenamiento, reportes) y migraciones |
| `api` | API REST Spring Boot con seguridad básica, OpenAPI y wiring de componentes |
| `ui-javafx` | Cliente JavaFX (MVVM light) que consume la API central |

## Requisitos

* Java 21
* Maven 3.9+
* Docker / Docker Compose (para Postgres + servicios auxiliares)

## Configuración rápida

```bash
mvn clean install
```

Para ejecutar la API con un Postgres local vía Docker Compose:

```bash
docker compose up -d
mvn -pl api spring-boot:run
```

La UI JavaFX puede ejecutarse con:

```bash
mvn -pl ui-javafx javafx:run
```

## Migraciones y datos semilla

Flyway crea el esquema inicial y siembra cuatro Puntos de Venta. Fixtures de ejemplo (`infrastructure/src/main/resources/fixtures`) incluyen CSVs y un PDF dummy para demostrar el pipeline OCR.

## Casos de uso implementados

* Crear, enviar, aprobar, rechazar y firmar cuadres
* Subir PDF con validaciones OCR básicas
* Importar documentos contables y movimientos bancarios
* Pre-conciliar con motor de estrategias y explicación (`razonesJson`)
* Registrar ajustes y observaciones
* Generar reportes agregados

## Calidad

* Unit tests (JUnit5 + AssertJ + Mockito)
* Integraciones de estrategia (Conciliación)
* Configurado Jacoco y Spotless (pendiente activar reglas detalladas)

## Documentación adicional

* [ADR-001 Arquitectura Hexagonal](docs/ADR-001-arquitectura-hexagonal.md)
* [Diagrama de arquitectura](docs/diagrama-arquitectura.md)
* [Flujo OCR](docs/flujo-ocr.md)
* [Motor de conciliación](docs/motor-conciliacion.md)

## DevOps

* `docker-compose.yml` con Postgres y API (modo dev)
* GitHub Actions (`.github/workflows/ci.yml`) para build + tests + JaCoCo + empaquetado

## Variables relevantes

| Variable | Descripción |
| --- | --- |
| `SPRING_DATASOURCE_URL` | Cadena de conexión a Postgres |
| `BALANCERX_STORAGE_ROOT` | Carpeta de almacenamiento de PDFs y anexos |
| `JWT_SECRET` | Secreto para firma de tokens (por implementar) |

## Roadmap inmediato

1. Implementar verificación real de hash de contraseña + JWT
2. Añadir WebSocket de progreso OCR y conciliación
3. Completar pantallas JavaFX (bandejas multi-rol, reportes)
4. Automatizar generación de firmas y sellos en PDF usando PDFBox
