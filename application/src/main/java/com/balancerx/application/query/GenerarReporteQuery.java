package com.balancerx.application.query;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GenerarReporteQuery {
    String tipo;
    LocalDate desde;
    LocalDate hasta;
    UUID puntoVentaId;
    
    public GenerarReporteQuery(String tipo, LocalDate desde, LocalDate hasta, UUID puntoVentaId) {
        this.tipo = tipo;
        this.desde = desde;
        this.hasta = hasta;
        this.puntoVentaId = puntoVentaId;
    }

    // Métodos getter manuales para resolver errores de compilación
    public String getTipo() {
        return tipo;
    }

    public LocalDate getDesde() {
        return desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public UUID getPuntoVentaId() {
        return puntoVentaId;
    }
}
