package com.balancerx.infrastructure.persistence.jpa;

import com.balancerx.domain.valueobject.FuenteMovimiento;
import com.balancerx.domain.valueobject.TipoMovimientoBancario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "movimientos_bancarios")
@Getter
@Setter
public class JpaMovimientoBancario {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimientoBancario tipo;

    private String banco;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "referencia_banco")
    private String referenciaBanco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuenteMovimiento fuente;

    @Column(name = "asignado_por")
    private UUID asignadoPor;

    @Column(name = "punto_venta_id")
    private UUID puntoVentaId;

    @Column(name = "cuadre_id")
    private UUID cuadreId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Version
    private long version;
}
