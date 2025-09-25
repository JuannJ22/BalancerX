package com.balancerx.infrastructure.persistence.jpa;

import com.balancerx.domain.valueobject.TipoDocumentoContable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documentos_contables")
@Getter
@Setter
public class JpaDocumentoContable {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDocumentoContable tipo;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    private String referencia;

    @Column(name = "cuadre_id")
    private UUID cuadreId;

    private String observacion;
}
