package com.balancerx.application.command;

import java.io.InputStream;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ImportDocumentosCommand {
    UUID cuadreId;
    String tipoArchivo;
    InputStream inputStream;
    UUID usuarioId;
}
