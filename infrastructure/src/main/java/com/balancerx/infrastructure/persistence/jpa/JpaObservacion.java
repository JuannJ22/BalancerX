package com.balancerx.infrastructure.persistence.jpa;

import com.balancerx.domain.valueobject.SeveridadObservacion;
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
@Table(name = "observaciones")
@Getter
@Setter
public class JpaObservacion {
    @Id
    private UUID id;

    @Column(name = "cuadre_id", nullable = false)
    private UUID cuadreId;

    @Column(name = "autor_id", nullable = false)
    private UUID autorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeveridadObservacion severidad;

    @Lob
    @Column(nullable = false)
    private String texto;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
