package com.balancerx.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transferencias_historial")
public class JpaTransferenciaHistorial {
    @Id
    private UUID id;

    @Column(name = "transferencia_id", nullable = false)
    private UUID transferenciaId;

    @Column(nullable = false)
    private String accion;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTransferenciaId() {
        return transferenciaId;
    }

    public void setTransferenciaId(UUID transferenciaId) {
        this.transferenciaId = transferenciaId;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }
}
