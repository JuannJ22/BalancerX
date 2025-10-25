package com.balancerx.application.command;

import java.util.UUID;

public class AsignarMovimientoCommand {
    private final UUID movimientoId;
    private final UUID cuadreId;
    private final UUID usuarioId;

    public AsignarMovimientoCommand(UUID movimientoId, UUID cuadreId, UUID usuarioId) {
        this.movimientoId = movimientoId;
        this.cuadreId = cuadreId;
        this.usuarioId = usuarioId;
    }

    public UUID getMovimientoId() {
        return movimientoId;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AsignarMovimientoCommand that = (AsignarMovimientoCommand) o;
        return java.util.Objects.equals(movimientoId, that.movimientoId) &&
               java.util.Objects.equals(cuadreId, that.cuadreId) &&
               java.util.Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(movimientoId, cuadreId, usuarioId);
    }

    @Override
    public String toString() {
        return "AsignarMovimientoCommand{" +
                "movimientoId=" + movimientoId +
                ", cuadreId=" + cuadreId +
                ", usuarioId=" + usuarioId +
                '}';
    }
}
