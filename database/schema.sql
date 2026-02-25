CREATE SCHEMA bx;
GO

CREATE TABLE bx.roles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre NVARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE bx.users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,
    admin_pin_hash NVARCHAR(255) NULL,
    activo BIT NOT NULL DEFAULT 1
);

CREATE TABLE bx.user_roles (
    usuario_id INT NOT NULL,
    rol_id INT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES bx.users(id),
    FOREIGN KEY (rol_id) REFERENCES bx.roles(id)
);

CREATE TABLE bx.puntos_venta (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre NVARCHAR(150) NOT NULL
);

CREATE TABLE bx.vendedores (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nombre NVARCHAR(150) NOT NULL
);

CREATE TABLE bx.transferencias (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    monto DECIMAL(18,2) NOT NULL,
    punto_venta_id INT NOT NULL,
    vendedor_id INT NOT NULL,
    observacion NVARCHAR(500) NULL,
    estado NVARCHAR(30) NOT NULL DEFAULT 'CREADA',
    printed_at DATETIME2 NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    created_by INT NOT NULL,
    FOREIGN KEY (punto_venta_id) REFERENCES bx.puntos_venta(id),
    FOREIGN KEY (vendedor_id) REFERENCES bx.vendedores(id),
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

INSERT INTO bx.roles (nombre) VALUES ('ADMIN'), ('TESORERIA'), ('AUXILIAR');
