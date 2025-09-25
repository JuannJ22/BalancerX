package com.balancerx.application.command;

import com.balancerx.domain.valueobject.FuenteMovimiento;
import java.io.InputStream;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ImportMovimientosCommand {
    UUID puntoVentaId;
    FuenteMovimiento fuente;
    InputStream inputStream;
    UUID usuarioId;
}
