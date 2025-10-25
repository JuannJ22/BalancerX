package com.balancerx.application.command;

import java.util.UUID;

public class PreConciliarCommand {
    private final UUID cuadreId;
    private final UUID usuarioId;

    public PreConciliarCommand(UUID cuadreId, UUID usuarioId) {
        this.cuadreId = cuadreId;
        this.usuarioId = usuarioId;
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
        PreConciliarCommand that = (PreConciliarCommand) o;
        return java.util.Objects.equals(cuadreId, that.cuadreId) &&
               java.util.Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(cuadreId, usuarioId);
    }

    @Override
    public String toString() {
        return "PreConciliarCommand{" +
                "cuadreId=" + cuadreId +
                ", usuarioId=" + usuarioId +
                '}';
    }
}
