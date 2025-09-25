package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.TipoArchivo;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Archivo {
    UUID id;
    TipoArchivo tipo;
    String path;
    String checksum;
    String metadataJson;
    UUID subidoPor;
    UUID cuadreId;
    Instant createdAt;
}
