package com.balancerx.application.command;

import java.util.UUID;

public class EnviarCuadreCommand {
    private final UUID cuadreId;
    private final UUID usuarioId;
    private final boolean fueraDeCalendario;
    private final String justificacion;

    public EnviarCuadreCommand(UUID cuadreId, UUID usuarioId, boolean fueraDeCalendario, String justificacion) {
        this.cuadreId = cuadreId;
        this.usuarioId = usuarioId;
        this.fueraDeCalendario = fueraDeCalendario;
        this.justificacion = justificacion;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public boolean isFueraDeCalendario() {
        return fueraDeCalendario;
    }

    public String getJustificacion() {
        return justificacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnviarCuadreCommand that = (EnviarCuadreCommand) o;
        return fueraDeCalendario == that.fueraDeCalendario &&
               java.util.Objects.equals(cuadreId, that.cuadreId) &&
               java.util.Objects.equals(usuarioId, that.usuarioId) &&
               java.util.Objects.equals(justificacion, that.justificacion);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(cuadreId, usuarioId, fueraDeCalendario, justificacion);
    }

    @Override
    public String toString() {
        return "EnviarCuadreCommand{" +
                "cuadreId=" + cuadreId +
                ", usuarioId=" + usuarioId +
                ", fueraDeCalendario=" + fueraDeCalendario +
                ", justificacion='" + justificacion + '\'' +
                '}';
    }
}
