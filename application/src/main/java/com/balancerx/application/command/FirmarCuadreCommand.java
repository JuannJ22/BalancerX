package com.balancerx.application.command;

import com.balancerx.domain.valueobject.RolUsuario;
import java.util.UUID;

public class FirmarCuadreCommand {
    private final UUID cuadreId;
    private final UUID usuarioId;
    private final RolUsuario rolFirma;

    public FirmarCuadreCommand(UUID cuadreId, UUID usuarioId, RolUsuario rolFirma) {
        this.cuadreId = cuadreId;
        this.usuarioId = usuarioId;
        this.rolFirma = rolFirma;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public RolUsuario getRolFirma() {
        return rolFirma;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirmarCuadreCommand that = (FirmarCuadreCommand) o;
        return java.util.Objects.equals(cuadreId, that.cuadreId) &&
               java.util.Objects.equals(usuarioId, that.usuarioId) &&
               java.util.Objects.equals(rolFirma, that.rolFirma);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(cuadreId, usuarioId, rolFirma);
    }

    @Override
    public String toString() {
        return "FirmarCuadreCommand{" +
                "cuadreId=" + cuadreId +
                ", usuarioId=" + usuarioId +
                ", rolFirma=" + rolFirma +
                '}';
    }
}
