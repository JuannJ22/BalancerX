package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.TipoAjuste;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Ajuste {
    UUID id;
    UUID cuadreId;
    TipoAjuste tipo;
    BigDecimal monto;
    String motivo;
    UUID autorId;
    Instant createdAt;
}
