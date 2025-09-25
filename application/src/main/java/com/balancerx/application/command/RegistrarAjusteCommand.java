package com.balancerx.application.command;

import com.balancerx.domain.valueobject.TipoAjuste;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegistrarAjusteCommand {
    UUID cuadreId;
    TipoAjuste tipo;
    BigDecimal monto;
    String motivo;
    UUID usuarioId;
}
