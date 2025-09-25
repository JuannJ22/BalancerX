package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.FuenteMovimiento;
import com.balancerx.domain.valueobject.TipoMovimientoBancario;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder(toBuilder = true)
@With
public class MovimientoBancario {
    UUID id;
    TipoMovimientoBancario tipo;
    String banco;
    LocalDate fecha;
    BigDecimal valor;
    String referenciaBanco;
    FuenteMovimiento fuente;
    UUID asignadoPor;
    UUID puntoVentaId;
    UUID cuadreId;
    Instant createdAt;
    long version;

    public Optional<UUID> getAsignadoPor() {
        return Optional.ofNullable(asignadoPor);
    }

    public Optional<UUID> getPuntoVentaId() {
        return Optional.ofNullable(puntoVentaId);
    }

    public Optional<UUID> getCuadreId() {
        return Optional.ofNullable(cuadreId);
    }
}
