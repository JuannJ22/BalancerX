package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.SeveridadObservacion;
import java.time.Instant;
import java.util.UUID;

public class Observacion {
    private final UUID id;
    private final UUID cuadreId;
    private final UUID autorId;
    private final SeveridadObservacion severidad;
    private final String texto;
    private final Instant createdAt;

    public Observacion(UUID id, UUID cuadreId, UUID autorId, SeveridadObservacion severidad, 
                       String texto, Instant createdAt) {
        this.id = id;
        this.cuadreId = cuadreId;
        this.autorId = autorId;
        this.severidad = severidad;
        this.texto = texto;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public UUID getAutorId() {
        return autorId;
    }

    public SeveridadObservacion getSeveridad() {
        return severidad;
    }

    public String getTexto() {
        return texto;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Observacion that = (Observacion) o;
        return java.util.Objects.equals(id, that.id) &&
               java.util.Objects.equals(cuadreId, that.cuadreId) &&
               java.util.Objects.equals(autorId, that.autorId) &&
               severidad == that.severidad &&
               java.util.Objects.equals(texto, that.texto) &&
               java.util.Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, cuadreId, autorId, severidad, texto, createdAt);
    }

    @Override
    public String toString() {
        return "Observacion{" +
                "id=" + id +
                ", cuadreId=" + cuadreId +
                ", autorId=" + autorId +
                ", severidad=" + severidad +
                ", texto='" + texto + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
