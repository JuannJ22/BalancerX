package com.balancerx.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateCuadreCommand {
    private final LocalDate fecha;
    private final UUID puntoVentaId;
    private final BigDecimal totalTirilla;
    private final BigDecimal totalBancos;
    private final BigDecimal totalContable;
    private final UUID creadoPor;

    public CreateCuadreCommand(LocalDate fecha, UUID puntoVentaId, BigDecimal totalTirilla, 
                              BigDecimal totalBancos, BigDecimal totalContable, UUID creadoPor) {
        this.fecha = fecha;
        this.puntoVentaId = puntoVentaId;
        this.totalTirilla = totalTirilla;
        this.totalBancos = totalBancos;
        this.totalContable = totalContable;
        this.creadoPor = creadoPor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public UUID getPuntoVentaId() {
        return puntoVentaId;
    }

    public BigDecimal getTotalTirilla() {
        return totalTirilla;
    }

    public BigDecimal getTotalBancos() {
        return totalBancos;
    }

    public BigDecimal getTotalContable() {
        return totalContable;
    }

    public UUID getCreadoPor() {
        return creadoPor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateCuadreCommand that = (CreateCuadreCommand) o;
        return java.util.Objects.equals(fecha, that.fecha) &&
               java.util.Objects.equals(puntoVentaId, that.puntoVentaId) &&
               java.util.Objects.equals(totalTirilla, that.totalTirilla) &&
               java.util.Objects.equals(totalBancos, that.totalBancos) &&
               java.util.Objects.equals(totalContable, that.totalContable) &&
               java.util.Objects.equals(creadoPor, that.creadoPor);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(fecha, puntoVentaId, totalTirilla, totalBancos, totalContable, creadoPor);
    }

    @Override
    public String toString() {
        return "CreateCuadreCommand{" +
                "fecha=" + fecha +
                ", puntoVentaId=" + puntoVentaId +
                ", totalTirilla=" + totalTirilla +
                ", totalBancos=" + totalBancos +
                ", totalContable=" + totalContable +
                ", creadoPor=" + creadoPor +
                '}';
    }
}
