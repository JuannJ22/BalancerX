package com.balancerx.domain.model;

import java.time.Instant;
import java.util.UUID;

public class TransferenciaHistorialEntry {
    private final UUID id;
    private final UUID transferenciaId;
    private final String accion;
    private final UUID usuarioId;
    private final Instant timestamp;
    private final String metadataJson;

    public TransferenciaHistorialEntry(UUID id, UUID transferenciaId, String accion, UUID usuarioId,
                                       Instant timestamp, String metadataJson) {
        this.id = id;
        this.transferenciaId = transferenciaId;
        this.accion = accion;
        this.usuarioId = usuarioId;
        this.timestamp = timestamp;
        this.metadataJson = metadataJson;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTransferenciaId() {
        return transferenciaId;
    }

    public String getAccion() {
        return accion;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getMetadataJson() {
        return metadataJson;
    }
}
