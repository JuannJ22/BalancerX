package com.balancerx.infrastructure.persistence.jpa;

import com.balancerx.domain.valueobject.EstrategiaMatch;
import com.balancerx.domain.valueobject.EstadoMatch;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "matches")
@Getter
@Setter
public class JpaMatch {
    @Id
    private UUID id;

    @Column(name = "movimiento_bancario_id")
    private UUID movimientoBancarioId;

    @Column(name = "documento_id")
    private UUID documentoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstrategiaMatch estrategia;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMatch estado;

    @Lob
    @Column(name = "razones_json")
    private String razonesJson;

    @Column(name = "decided_by")
    private UUID decidedBy;

    @Column(name = "decided_at")
    private Instant decidedAt;

    // Métodos setter manuales para resolver errores de compilación
    public void setId(UUID id) {
        this.id = id;
    }

    public void setMovimientoBancarioId(UUID movimientoBancarioId) {
        this.movimientoBancarioId = movimientoBancarioId;
    }

    public void setDocumentoId(UUID documentoId) {
        this.documentoId = documentoId;
    }

    public void setEstrategia(EstrategiaMatch estrategia) {
        this.estrategia = estrategia;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public void setEstado(EstadoMatch estado) {
        this.estado = estado;
    }

    public void setRazonesJson(String razonesJson) {
        this.razonesJson = razonesJson;
    }

    public void setDecidedBy(UUID decidedBy) {
        this.decidedBy = decidedBy;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    // Métodos getter manuales para resolver errores de compilación
    public UUID getId() {
        return id;
    }

    public UUID getMovimientoBancarioId() {
        return movimientoBancarioId;
    }

    public UUID getDocumentoId() {
        return documentoId;
    }

    public EstrategiaMatch getEstrategia() {
        return estrategia;
    }

    public BigDecimal getScore() {
        return score;
    }

    public EstadoMatch getEstado() {
        return estado;
    }

    public String getRazonesJson() {
        return razonesJson;
    }

    public UUID getDecidedBy() {
        return decidedBy;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }
}