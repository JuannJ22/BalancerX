package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.TipoArchivo;
import java.time.Instant;
import java.util.UUID;

public class Archivo {
    private final UUID id;
    private final TipoArchivo tipo;
    private final String path;
    private final String checksum;
    private final String metadataJson;
    private final UUID subidoPor;
    private final UUID cuadreId;
    private final Instant createdAt;

    public Archivo(UUID id, TipoArchivo tipo, String path, String checksum, String metadataJson,
                   UUID subidoPor, UUID cuadreId, Instant createdAt) {
        this.id = id;
        this.tipo = tipo;
        this.path = path;
        this.checksum = checksum;
        this.metadataJson = metadataJson;
        this.subidoPor = subidoPor;
        this.cuadreId = cuadreId;
        this.createdAt = createdAt;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Archivo archivo = (Archivo) o;
        return java.util.Objects.equals(id, archivo.id) &&
               tipo == archivo.tipo &&
               java.util.Objects.equals(path, archivo.path) &&
               java.util.Objects.equals(checksum, archivo.checksum) &&
               java.util.Objects.equals(metadataJson, archivo.metadataJson) &&
               java.util.Objects.equals(subidoPor, archivo.subidoPor) &&
               java.util.Objects.equals(cuadreId, archivo.cuadreId) &&
               java.util.Objects.equals(createdAt, archivo.createdAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, tipo, path, checksum, metadataJson, subidoPor, cuadreId, createdAt);
    }

    @Override
    public String toString() {
        return "Archivo{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", path='" + path + '\'' +
                ", checksum='" + checksum + '\'' +
                ", metadataJson='" + metadataJson + '\'' +
                ", subidoPor=" + subidoPor +
                ", cuadreId=" + cuadreId +
                ", createdAt=" + createdAt +
                '}';
    }
}
