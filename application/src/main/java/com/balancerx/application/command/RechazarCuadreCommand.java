package com.balancerx.application.command;

import java.util.UUID;

public class RechazarCuadreCommand {
    private final UUID cuadreId;
    private final UUID usuarioId;
    private final String motivo;

    public RechazarCuadreCommand(UUID cuadreId, UUID usuarioId, String motivo) {
        this.cuadreId = cuadreId;
        this.usuarioId = usuarioId;
        this.motivo = motivo;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getMotivo() {
        return motivo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RechazarCuadreCommand that = (RechazarCuadreCommand) o;
        return java.util.Objects.equals(cuadreId, that.cuadreId) &&
               java.util.Objects.equals(usuarioId, that.usuarioId) &&
               java.util.Objects.equals(motivo, that.motivo);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(cuadreId, usuarioId, motivo);
    }

    @Override
    public String toString() {
        return "RechazarCuadreCommand{" +
                "cuadreId=" + cuadreId +
                ", usuarioId=" + usuarioId +
                ", motivo='" + motivo + '\'' +
                '}';
    }
}
