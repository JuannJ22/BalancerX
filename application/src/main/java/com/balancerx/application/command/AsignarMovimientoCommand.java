package com.balancerx.application.command;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AsignarMovimientoCommand {
    UUID movimientoId;
    UUID cuadreId;
    UUID usuarioId;
}
