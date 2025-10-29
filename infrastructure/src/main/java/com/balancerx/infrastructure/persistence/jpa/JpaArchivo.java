package com.balancerx.infrastructure.persistence.jpa;

import com.balancerx.domain.valueobject.TipoArchivo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "archivos")
@Getter
@Setter
public class JpaArchivo {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoArchivo tipo;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String checksum;

    @Lob
    @Column(name = "metadata_json")
    private String metadataJson;

    @Column(name = "subido_por", nullable = false)
    private UUID subidoPor;

    @Column(name = "cuadre_id")
    private UUID cuadreId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // Métodos getter manuales para resolver errores de compilación
    public UUID getId() {
        return id;
    }

    public TipoArchivo getTipo() {
        return tipo;
    }

    public String getPath() {
        return path;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public UUID getSubidoPor() {
        return subidoPor;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    // Métodos setter manuales para resolver errores de compilación
    public void setId(UUID id) {
        this.id = id;
    }

    public void setTipo(TipoArchivo tipo) {
        this.tipo = tipo;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public void setSubidoPor(UUID subidoPor) {
        this.subidoPor = subidoPor;
    }

    public void setCuadreId(UUID cuadreId) {
        this.cuadreId = cuadreId;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
