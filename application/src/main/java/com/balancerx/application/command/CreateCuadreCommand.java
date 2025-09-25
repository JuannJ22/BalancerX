package com.balancerx.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateCuadreCommand {
    LocalDate fecha;
    UUID puntoVentaId;
    BigDecimal totalTirilla;
    BigDecimal totalBancos;
    BigDecimal totalContable;
    UUID creadoPor;
}
