CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE usuarios (
    id UUID PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    rol VARCHAR(64) NOT NULL,
    hash_password VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE puntos_venta (
    id UUID PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE cuadres (
    id UUID PRIMARY KEY,
    fecha DATE NOT NULL,
    punto_venta_id UUID NOT NULL REFERENCES puntos_venta(id),
    estado VARCHAR(32) NOT NULL,
    total_tirilla NUMERIC(15,2),
    total_bancos NUMERIC(15,2),
    total_contable NUMERIC(15,2),
    pdf_path TEXT,
    checksum_pdf TEXT,
    creado_por UUID,
    actualizado_por UUID,
    firmado_elabora BOOLEAN DEFAULT FALSE,
    firmado_autoriza BOOLEAN DEFAULT FALSE,
    firmado_audita BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT uq_cuadre_aprobado UNIQUE (punto_venta_id, fecha, estado)
);

CREATE TABLE documentos_contables (
    id UUID PRIMARY KEY,
    tipo VARCHAR(64) NOT NULL,
    numero VARCHAR(128) NOT NULL,
    fecha DATE NOT NULL,
    valor NUMERIC(15,2) NOT NULL,
    referencia VARCHAR(255),
    cuadre_id UUID REFERENCES cuadres(id),
    observacion TEXT
);

CREATE TABLE movimientos_bancarios (
    id UUID PRIMARY KEY,
    tipo VARCHAR(64) NOT NULL,
    banco VARCHAR(128),
    fecha DATE NOT NULL,
    valor NUMERIC(15,2) NOT NULL,
    referencia_banco VARCHAR(255),
    fuente VARCHAR(64) NOT NULL,
    asignado_por UUID,
    punto_venta_id UUID REFERENCES puntos_venta(id),
    cuadre_id UUID REFERENCES cuadres(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL
);

CREATE INDEX idx_movimientos_referencia ON movimientos_bancarios (referencia_banco);

CREATE TABLE matches (
    id UUID PRIMARY KEY,
    movimiento_bancario_id UUID REFERENCES movimientos_bancarios(id),
    documento_id UUID REFERENCES documentos_contables(id),
    estrategia VARCHAR(64) NOT NULL,
    score NUMERIC(4,2) NOT NULL,
    estado VARCHAR(32) NOT NULL,
    razones_json TEXT,
    decided_by UUID,
    decided_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE ajustes (
    id UUID PRIMARY KEY,
    cuadre_id UUID REFERENCES cuadres(id),
    tipo VARCHAR(64) NOT NULL,
    monto NUMERIC(15,2) NOT NULL,
    motivo TEXT NOT NULL,
    autor_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE observaciones (
    id UUID PRIMARY KEY,
    cuadre_id UUID REFERENCES cuadres(id),
    autor_id UUID NOT NULL,
    severidad VARCHAR(32) NOT NULL,
    texto TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE firmas (
    id UUID PRIMARY KEY,
    cuadre_id UUID REFERENCES cuadres(id),
    rol VARCHAR(64) NOT NULL,
    firmante_id UUID NOT NULL,
    metodo VARCHAR(32) NOT NULL,
    hash TEXT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE archivos (
    id UUID PRIMARY KEY,
    tipo VARCHAR(16) NOT NULL,
    path TEXT NOT NULL,
    checksum TEXT NOT NULL,
    metadata_json TEXT,
    subido_por UUID NOT NULL,
    cuadre_id UUID REFERENCES cuadres(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

INSERT INTO puntos_venta (id, nombre, activo, created_at) VALUES
    (uuid_generate_v4(), 'Principal', true, NOW()),
    (uuid_generate_v4(), 'Sucursal', true, NOW()),
    (uuid_generate_v4(), 'Calarcá', true, NOW()),
    (uuid_generate_v4(), 'Tienda Pintuco', true, NOW());
