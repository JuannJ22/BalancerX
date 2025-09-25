package com.balancerx.application.command;

import com.balancerx.domain.valueobject.RolUsuario;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FirmarCuadreCommand {
    UUID cuadreId;
    UUID usuarioId;
    RolUsuario rolFirma;
}
