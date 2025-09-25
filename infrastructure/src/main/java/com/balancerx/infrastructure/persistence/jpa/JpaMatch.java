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
}
