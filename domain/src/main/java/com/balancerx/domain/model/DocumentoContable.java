package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.TipoDocumentoContable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder(toBuilder = true)
@With
public class DocumentoContable {
    UUID id;
    TipoDocumentoContable tipo;
    String numero;
    LocalDate fecha;
    BigDecimal valor;
    String referencia;
    UUID cuadreId;
    String observacion;

    public Optional<UUID> getCuadreId() {
        return Optional.ofNullable(cuadreId);
    }

    public Optional<String> getReferencia() {
        return Optional.ofNullable(referencia);
    }

    public Optional<String> getObservacion() {
        return Optional.ofNullable(observacion);
    }
}
