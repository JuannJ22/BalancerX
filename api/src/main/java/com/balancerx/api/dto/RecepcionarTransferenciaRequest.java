package com.balancerx.api.dto;

import java.util.UUID;

public record RecepcionarTransferenciaRequest(
        UUID usuarioId,
        String comentario) {
}
