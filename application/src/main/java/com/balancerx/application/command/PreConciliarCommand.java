package com.balancerx.application.command;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PreConciliarCommand {
    UUID cuadreId;
    UUID usuarioId;
}
