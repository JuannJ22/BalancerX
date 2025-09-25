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
}
