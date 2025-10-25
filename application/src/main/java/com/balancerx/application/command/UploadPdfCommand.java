package com.balancerx.application.command;

import java.util.UUID;

public class UploadPdfCommand {
    private final UUID cuadreId;
    private final byte[] contenido;
    private final String nombreArchivo;
    private final UUID usuarioId;

    public UploadPdfCommand(UUID cuadreId, byte[] contenido, String nombreArchivo, UUID usuarioId) {
        this.cuadreId = cuadreId;
        this.contenido = contenido;
        this.nombreArchivo = nombreArchivo;
        this.usuarioId = usuarioId;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadPdfCommand that = (UploadPdfCommand) o;
        return java.util.Objects.equals(cuadreId, that.cuadreId) &&
               java.util.Arrays.equals(contenido, that.contenido) &&
               java.util.Objects.equals(nombreArchivo, that.nombreArchivo) &&
               java.util.Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        int result = java.util.Objects.hash(cuadreId, nombreArchivo, usuarioId);
        result = 31 * result + java.util.Arrays.hashCode(contenido);
        return result;
    }

    @Override
    public String toString() {
        return "UploadPdfCommand{" +
                "cuadreId=" + cuadreId +
                ", contenido=" + java.util.Arrays.toString(contenido) +
                ", nombreArchivo='" + nombreArchivo + '\'' +
                ", usuarioId=" + usuarioId +
                '}';
    }
}
