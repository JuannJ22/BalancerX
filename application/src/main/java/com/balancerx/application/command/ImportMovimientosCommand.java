package com.balancerx.application.command;

import com.balancerx.domain.valueobject.FuenteMovimiento;
import java.io.InputStream;
import java.util.UUID;

public class ImportMovimientosCommand {
    private final UUID puntoVentaId;
    private final FuenteMovimiento fuente;
    private final InputStream inputStream;
    private final UUID usuarioId;

    public ImportMovimientosCommand(UUID puntoVentaId, FuenteMovimiento fuente, InputStream inputStream, UUID usuarioId) {
        this.puntoVentaId = puntoVentaId;
        this.fuente = fuente;
        this.inputStream = inputStream;
        this.usuarioId = usuarioId;
    }

    public UUID getPuntoVentaId() {
        return puntoVentaId;
    }

    public FuenteMovimiento getFuente() {
        return fuente;
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
        ImportMovimientosCommand that = (ImportMovimientosCommand) o;
        return java.util.Objects.equals(puntoVentaId, that.puntoVentaId) &&
               java.util.Objects.equals(fuente, that.fuente) &&
               java.util.Objects.equals(inputStream, that.inputStream) &&
               java.util.Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(puntoVentaId, fuente, inputStream, usuarioId);
    }

    @Override
    public String toString() {
        return "ImportMovimientosCommand{" +
                "puntoVentaId=" + puntoVentaId +
                ", fuente=" + fuente +
                ", inputStream=" + inputStream +
                ", usuarioId=" + usuarioId +
                '}';
    }
}
