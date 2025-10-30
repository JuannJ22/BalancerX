package com.balancerx.api.dto;

import com.balancerx.domain.model.TransferenciaHistorialEntry;
import java.time.Instant;
import java.util.UUID;

public record TransferenciaHistorialResponse(
        UUID id,
        String accion,
        UUID usuarioId,
        Instant timestamp,
        String metadataJson) {

    public static TransferenciaHistorialResponse fromDomain(TransferenciaHistorialEntry entry) {
        return new TransferenciaHistorialResponse(
                entry.getId(),
                entry.getAccion(),
                entry.getUsuarioId(),
                entry.getTimestamp(),
                entry.getMetadataJson()
        );
    }
}
