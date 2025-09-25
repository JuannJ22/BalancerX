package com.balancerx.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateCuadreRequest(
        LocalDate fecha,
        UUID puntoVentaId,
        BigDecimal totalTirilla,
        BigDecimal totalBancos,
        BigDecimal totalContable) {}
