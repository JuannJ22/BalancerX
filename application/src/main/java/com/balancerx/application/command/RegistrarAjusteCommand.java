package com.balancerx.application.command;

import com.balancerx.domain.valueobject.TipoAjuste;
import java.math.BigDecimal;
import java.util.UUID;

public class RegistrarAjusteCommand {
    private final UUID cuadreId;
    private final TipoAjuste tipo;
    private final BigDecimal monto;
    private final String motivo;
    private final UUID usuarioId;

    public RegistrarAjusteCommand(UUID cuadreId, TipoAjuste tipo, BigDecimal monto, String motivo, UUID usuarioId) {
        this.cuadreId = cuadreId;
        this.tipo = tipo;
        this.monto = monto;
        this.motivo = motivo;
        this.usuarioId = usuarioId;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public TipoAjuste getTipo() {
        return tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getMotivo() {
        return motivo;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrarAjusteCommand that = (RegistrarAjusteCommand) o;
        return java.util.Objects.equals(cuadreId, that.cuadreId) &&
               java.util.Objects.equals(tipo, that.tipo) &&
               java.util.Objects.equals(monto, that.monto) &&
               java.util.Objects.equals(motivo, that.motivo) &&
               java.util.Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(cuadreId, tipo, monto, motivo, usuarioId);
    }

    @Override
    public String toString() {
        return "RegistrarAjusteCommand{" +
                "cuadreId=" + cuadreId +
                ", tipo=" + tipo +
                ", monto=" + monto +
                ", motivo='" + motivo + '\'' +
                ", usuarioId=" + usuarioId +
                '}';
    }
}
