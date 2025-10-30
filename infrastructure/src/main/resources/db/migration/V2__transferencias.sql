CREATE TABLE transferencias (
    id UUID PRIMARY KEY,
    banco VARCHAR(64) NOT NULL,
    fecha DATE NOT NULL,
    valor NUMERIC(15,2) NOT NULL,
    comentario TEXT,
    archivo_id UUID REFERENCES archivos(id),
    cargado_por UUID NOT NULL REFERENCES usuarios(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    estado VARCHAR(32) NOT NULL,
    tipo_asignacion VARCHAR(32),
    destino_id UUID,
    asignado_por UUID REFERENCES usuarios(id),
    asignado_en TIMESTAMP WITH TIME ZONE,
    cuenta_contable VARCHAR(64),
    cuenta_bancaria VARCHAR(64),
    actualizado_por UUID REFERENCES usuarios(id),
    actualizado_en TIMESTAMP WITH TIME ZONE,
    version BIGINT NOT NULL
);

CREATE INDEX idx_transferencias_banco_fecha ON transferencias (banco, fecha);
CREATE INDEX idx_transferencias_destino ON transferencias (destino_id);

CREATE TABLE transferencias_historial (
    id UUID PRIMARY KEY,
    transferencia_id UUID NOT NULL REFERENCES transferencias(id) ON DELETE CASCADE,
    accion VARCHAR(64) NOT NULL,
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    metadata_json TEXT
);

CREATE INDEX idx_transferencias_historial_transferencia ON transferencias_historial (transferencia_id);
