package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.TipoAjuste;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Ajuste {
    private final UUID id;
    private final UUID cuadreId;
    private final TipoAjuste tipo;
    private final BigDecimal monto;
    private final String motivo;
    private final UUID autorId;
    private final Instant createdAt;

    public Ajuste(UUID id, UUID cuadreId, TipoAjuste tipo, BigDecimal monto, 
                  String motivo, UUID autorId, Instant createdAt) {
        this.id = id;
        this.cuadreId = cuadreId;
        this.tipo = tipo;
        this.monto = monto;
        this.motivo = motivo;
        this.autorId = autorId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public TipoAjuste getTipo() {
        return tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getMotivo() {
        return motivo;
    }

    public UUID getAutorId() {
        return autorId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ajuste ajuste = (Ajuste) o;
        return java.util.Objects.equals(id, ajuste.id) &&
               java.util.Objects.equals(cuadreId, ajuste.cuadreId) &&
               tipo == ajuste.tipo &&
               java.util.Objects.equals(monto, ajuste.monto) &&
               java.util.Objects.equals(motivo, ajuste.motivo) &&
               java.util.Objects.equals(autorId, ajuste.autorId) &&
               java.util.Objects.equals(createdAt, ajuste.createdAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, cuadreId, tipo, monto, motivo, autorId, createdAt);
    }

    @Override
    public String toString() {
        return "Ajuste{" +
                "id=" + id +
                ", cuadreId=" + cuadreId +
                ", tipo=" + tipo +
                ", monto=" + monto +
                ", motivo='" + motivo + '\'' +
                ", autorId=" + autorId +
                ", createdAt=" + createdAt +
                '}';
    }
}
