package com.balancerx.application.command;

import java.io.InputStream;
import java.util.UUID;

public class ImportDocumentosCommand {
    private final UUID cuadreId;
    private final String tipoArchivo;
    private final InputStream inputStream;
    private final UUID usuarioId;

    public ImportDocumentosCommand(UUID cuadreId, String tipoArchivo, InputStream inputStream, UUID usuarioId) {
        this.cuadreId = cuadreId;
        this.tipoArchivo = tipoArchivo;
        this.inputStream = inputStream;
        this.usuarioId = usuarioId;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportDocumentosCommand that = (ImportDocumentosCommand) o;
        return java.util.Objects.equals(cuadreId, that.cuadreId) &&
               java.util.Objects.equals(tipoArchivo, that.tipoArchivo) &&
               java.util.Objects.equals(inputStream, that.inputStream) &&
               java.util.Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(cuadreId, tipoArchivo, inputStream, usuarioId);
    }

    @Override
    public String toString() {
        return "ImportDocumentosCommand{" +
                "cuadreId=" + cuadreId +
                ", tipoArchivo='" + tipoArchivo + '\'' +
                ", inputStream=" + inputStream +
                ", usuarioId=" + usuarioId +
                '}';
    }
}
