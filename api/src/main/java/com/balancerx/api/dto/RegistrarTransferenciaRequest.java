package com.balancerx.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrarTransferenciaRequest(
        UUID usuarioId,
        String archivoNombre,
        String archivoBase64,
        String banco,
        LocalDate fecha,
        BigDecimal valor,
        String comentario,
        String destinoTipo,
        UUID destinoId) {
}
