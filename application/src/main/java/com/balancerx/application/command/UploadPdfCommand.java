package com.balancerx.application.command;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UploadPdfCommand {
    UUID cuadreId;
    byte[] contenido;
    String nombreArchivo;
    UUID usuarioId;
}
