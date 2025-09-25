package com.balancerx.domain.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder(toBuilder = true)
@With
public class PuntoVenta {
    UUID id;
    String nombre;
    boolean activo;
    Instant createdAt;
}
