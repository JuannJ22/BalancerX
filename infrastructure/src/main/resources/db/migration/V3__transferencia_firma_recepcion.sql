ALTER TABLE usuarios
    ADD COLUMN firma_path TEXT,
    ADD COLUMN firma_checksum TEXT;

ALTER TABLE transferencias
    ADD COLUMN punto_venta_texto VARCHAR(255),
    ADD COLUMN valor_texto VARCHAR(64),
    ADD COLUMN fecha_texto VARCHAR(64),
    ADD COLUMN receptor_id UUID REFERENCES usuarios(id),
    ADD COLUMN firmada_por UUID REFERENCES usuarios(id),
    ADD COLUMN firmada_en TIMESTAMP WITH TIME ZONE,
    ADD COLUMN recibida_por UUID REFERENCES usuarios(id),
    ADD COLUMN recibida_en TIMESTAMP WITH TIME ZONE,
    ADD COLUMN impresa_por UUID REFERENCES usuarios(id),
    ADD COLUMN impresa_en TIMESTAMP WITH TIME ZONE,
    ADD COLUMN comentario_recepcion TEXT;
