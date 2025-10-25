package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.EstrategiaMatch;
import com.balancerx.domain.valueobject.EstadoMatch;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class Match {
    private final UUID id;
    private final UUID movimientoBancarioId;
    private final UUID documentoId;
    private final EstrategiaMatch estrategia;
    private final BigDecimal score;
    private final EstadoMatch estado;
    private final Map<String, Object> razones;
    private final UUID decidedBy;
    private final Instant decidedAt;

    public Match(UUID id, UUID movimientoBancarioId, UUID documentoId, EstrategiaMatch estrategia,
                 BigDecimal score, EstadoMatch estado, Map<String, Object> razones,
                 UUID decidedBy, Instant decidedAt) {
        this.id = id;
        this.movimientoBancarioId = movimientoBancarioId;
        this.documentoId = documentoId;
        this.estrategia = estrategia;
        this.score = score;
        this.estado = estado;
        this.razones = razones;
        this.decidedBy = decidedBy;
        this.decidedAt = decidedAt;
    }

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

    public Map<String, Object> getRazones() {
        return razones;
    }

    public UUID getDecidedBy() {
        return decidedBy;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public Match withId(UUID id) {
        return new Match(id, this.movimientoBancarioId, this.documentoId, this.estrategia,
                         this.score, this.estado, this.razones, this.decidedBy, this.decidedAt);
    }

    public Match withMovimientoBancarioId(UUID movimientoBancarioId) {
        return new Match(this.id, movimientoBancarioId, this.documentoId, this.estrategia,
                         this.score, this.estado, this.razones, this.decidedBy, this.decidedAt);
    }

    public Match withDocumentoId(UUID documentoId) {
        return new Match(this.id, this.movimientoBancarioId, documentoId, this.estrategia,
                         this.score, this.estado, this.razones, this.decidedBy, this.decidedAt);
    }

    public Match withEstrategia(EstrategiaMatch estrategia) {
        return new Match(this.id, this.movimientoBancarioId, this.documentoId, estrategia,
                         this.score, this.estado, this.razones, this.decidedBy, this.decidedAt);
    }

    public Match withScore(BigDecimal score) {
        return new Match(this.id, this.movimientoBancarioId, this.documentoId, this.estrategia,
                         score, this.estado, this.razones, this.decidedBy, this.decidedAt);
    }

    public Match withEstado(EstadoMatch estado) {
        return new Match(this.id, this.movimientoBancarioId, this.documentoId, this.estrategia,
                         this.score, estado, this.razones, this.decidedBy, this.decidedAt);
    }

    public Match withRazones(Map<String, Object> razones) {
        return new Match(this.id, this.movimientoBancarioId, this.documentoId, this.estrategia,
                         this.score, this.estado, razones, this.decidedBy, this.decidedAt);
    }

    public Match withDecidedBy(UUID decidedBy) {
        return new Match(this.id, this.movimientoBancarioId, this.documentoId, this.estrategia,
                         this.score, this.estado, this.razones, decidedBy, this.decidedAt);
    }

    public Match withDecidedAt(Instant decidedAt) {
        return new Match(this.id, this.movimientoBancarioId, this.documentoId, this.estrategia,
                         this.score, this.estado, this.razones, this.decidedBy, decidedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return java.util.Objects.equals(id, match.id) &&
               java.util.Objects.equals(movimientoBancarioId, match.movimientoBancarioId) &&
               java.util.Objects.equals(documentoId, match.documentoId) &&
               estrategia == match.estrategia &&
               java.util.Objects.equals(score, match.score) &&
               estado == match.estado &&
               java.util.Objects.equals(razones, match.razones) &&
               java.util.Objects.equals(decidedBy, match.decidedBy) &&
               java.util.Objects.equals(decidedAt, match.decidedAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, movimientoBancarioId, documentoId, estrategia, score,
                                      estado, razones, decidedBy, decidedAt);
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", movimientoBancarioId=" + movimientoBancarioId +
                ", documentoId=" + documentoId +
                ", estrategia=" + estrategia +
                ", score=" + score +
                ", estado=" + estado +
                ", razones=" + razones +
                ", decidedBy=" + decidedBy +
                ", decidedAt=" + decidedAt +
                '}';
    }
}
