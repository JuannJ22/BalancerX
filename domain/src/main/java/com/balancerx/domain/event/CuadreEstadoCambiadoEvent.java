package com.balancerx.domain.event;

import com.balancerx.domain.valueobject.EstadoCuadre;
import java.time.Instant;
import java.util.UUID;
import lombok.Value;

@Value
public class CuadreEstadoCambiadoEvent implements DomainEvent {
    UUID cuadreId;
    EstadoCuadre estadoAnterior;
    EstadoCuadre estadoNuevo;
    Instant occurredOn;

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
