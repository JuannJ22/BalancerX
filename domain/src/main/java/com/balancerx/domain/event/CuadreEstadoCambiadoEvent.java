package com.balancerx.domain.event;

import com.balancerx.domain.valueobject.EstadoCuadre;
import java.time.Instant;
import java.util.UUID;

public class CuadreEstadoCambiadoEvent implements DomainEvent {
    private final UUID cuadreId;
    private final EstadoCuadre estadoAnterior;
    private final EstadoCuadre estadoNuevo;
    private final Instant occurredOn;

    public CuadreEstadoCambiadoEvent(UUID cuadreId, EstadoCuadre estadoAnterior, 
                                    EstadoCuadre estadoNuevo, Instant occurredOn) {
        this.cuadreId = cuadreId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.occurredOn = occurredOn;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public EstadoCuadre getEstadoAnterior() {
        return estadoAnterior;
    }

    public EstadoCuadre getEstadoNuevo() {
        return estadoNuevo;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CuadreEstadoCambiadoEvent that = (CuadreEstadoCambiadoEvent) o;
        return java.util.Objects.equals(cuadreId, that.cuadreId) &&
               estadoAnterior == that.estadoAnterior &&
               estadoNuevo == that.estadoNuevo &&
               java.util.Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(cuadreId, estadoAnterior, estadoNuevo, occurredOn);
    }

    @Override
    public String toString() {
        return "CuadreEstadoCambiadoEvent{" +
                "cuadreId=" + cuadreId +
                ", estadoAnterior=" + estadoAnterior +
                ", estadoNuevo=" + estadoNuevo +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
