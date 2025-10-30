package com.balancerx.api.dto;

import java.util.UUID;

public record AsignarTransferenciaRequest(
        UUID usuarioId,
        String destinoTipo,
        UUID destinoId) {
}
