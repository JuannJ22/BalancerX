package com.balancerx.application.command;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RechazarCuadreCommand {
    UUID cuadreId;
    UUID usuarioId;
    String motivo;
}
