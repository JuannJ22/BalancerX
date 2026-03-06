/*
  BALANCERX - RECREAR BASE DE DATOS (ENTORNO LOCAL)
  Ejecuta este script sobre la base BalancerX para dejarla limpia y lista.
*/

SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

IF SCHEMA_ID('bx') IS NULL
    EXEC('CREATE SCHEMA bx');
GO

/* Limpieza total del esquema */
DROP TABLE IF EXISTS bx.audit_events;
DROP TABLE IF EXISTS bx.print_events;
DROP TABLE IF EXISTS bx.transferencia_archivos;
DROP TABLE IF EXISTS bx.transferencias;
DROP TABLE IF EXISTS bx.user_roles;
DROP TABLE IF EXISTS bx.users;
DROP TABLE IF EXISTS bx.roles;
DROP TABLE IF EXISTS bx.puntos_venta;
GO

/* Estructura */
CREATE TABLE bx.roles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre NVARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE bx.puntos_venta (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre NVARCHAR(150) NOT NULL
);

CREATE TABLE bx.users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,
    admin_pin_hash NVARCHAR(255) NULL,
    activo BIT NOT NULL DEFAULT 1,
    firma_electronica NVARCHAR(255) NULL,
    punto_venta_id INT NULL,
    CONSTRAINT FK_users_punto_venta FOREIGN KEY (punto_venta_id) REFERENCES bx.puntos_venta(id)
);

CREATE TABLE bx.user_roles (
    usuario_id INT NOT NULL,
    rol_id INT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES bx.users(id),
    FOREIGN KEY (rol_id) REFERENCES bx.roles(id)
);

CREATE TABLE bx.transferencias (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    monto DECIMAL(18,2) NOT NULL,
    punto_venta_id INT NOT NULL,
    vendedor_id INT NOT NULL,
    banco_id INT NOT NULL,
    cuenta_contable_id INT NOT NULL,
    observacion NVARCHAR(500) NULL,
    estado NVARCHAR(30) NOT NULL DEFAULT 'CREADA',
    printed_at DATETIME2 NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    created_by INT NOT NULL,
    FOREIGN KEY (punto_venta_id) REFERENCES bx.puntos_venta(id),
    FOREIGN KEY (created_by) REFERENCES bx.users(id)
);

CREATE TABLE bx.transferencia_archivos (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    transferencia_id BIGINT NOT NULL,
    nombre_original NVARCHAR(255) NOT NULL,
    internal_path NVARCHAR(500) NOT NULL,
    sha256 CHAR(64) NOT NULL,
    size_bytes BIGINT NOT NULL,
    uploaded_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    uploaded_by INT NOT NULL,
    FOREIGN KEY (transferencia_id) REFERENCES bx.transferencias(id),
    FOREIGN KEY (uploaded_by) REFERENCES bx.users(id)
);

CREATE TABLE bx.print_events (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    transferencia_id BIGINT NOT NULL,
    event_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    is_reprint BIT NOT NULL DEFAULT 0,
    executed_by INT NOT NULL,
    authorized_by INT NULL,
    razon NVARCHAR(500) NULL,
    FOREIGN KEY (transferencia_id) REFERENCES bx.transferencias(id),
    FOREIGN KEY (executed_by) REFERENCES bx.users(id),
    FOREIGN KEY (authorized_by) REFERENCES bx.users(id)
);

CREATE TABLE bx.audit_events (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    accion NVARCHAR(50) NOT NULL,
    entidad NVARCHAR(80) NOT NULL,
    entity_id NVARCHAR(80) NOT NULL,
    detalle NVARCHAR(MAX) NULL,
    event_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    executed_by INT NOT NULL,
    FOREIGN KEY (executed_by) REFERENCES bx.users(id)
);
GO

/* Datos base */
INSERT INTO bx.roles (nombre) VALUES ('ADMIN'), ('TESORERIA'), ('AUXILIAR');
GO

SET IDENTITY_INSERT bx.puntos_venta ON;
INSERT INTO bx.puntos_venta (id, nombre)
VALUES (1, N'Principal'), (2, N'Sucursal'), (3, N'Calarcá'), (4, N'Tienda Pintuco'), (5, N'Cartera'), (10, N'No identificada');
SET IDENTITY_INSERT bx.puntos_venta OFF;
GO

INSERT INTO bx.users (username, password_hash, admin_pin_hash, activo, firma_electronica, punto_venta_id)
VALUES
    ('admin', '{PLAIN}Admin123*', '{PLAIN}1234', 1, N'FIRMA ADMIN', NULL),
    ('tesoreria', '{PLAIN}Tesoreria123*', NULL, 1, N'FIRMA TESORERIA', NULL),
    ('auxiliar', '{PLAIN}Auxiliar123*', NULL, 1, N'FIRMA AUXILIAR', 1);
GO

INSERT INTO bx.user_roles (usuario_id, rol_id)
VALUES
    (1, 1), -- admin -> ADMIN
    (2, 2), -- tesoreria -> TESORERIA
    (3, 3); -- auxiliar -> AUXILIAR
GO
