package com.balancerx.infrastructure.persistence.jpa;

import com.balancerx.domain.valueobject.TipoAjuste;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ajustes")
@Getter
@Setter
public class JpaAjuste {
    @Id
    private UUID id;

    @Column(name = "cuadre_id", nullable = false)
    private UUID cuadreId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAjuste tipo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private String motivo;

    @Column(name = "autor_id", nullable = false)
    private UUID autorId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // Métodos setter manuales para resolver errores de compilación
    public void setId(UUID id) {
        this.id = id;
    }

    public void setCuadreId(UUID cuadreId) {
        this.cuadreId = cuadreId;
    }

    public void setTipo(TipoAjuste tipo) {
        this.tipo = tipo;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setAutorId(UUID autorId) {
        this.autorId = autorId;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // Métodos getter manuales para resolver errores de compilación
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
}