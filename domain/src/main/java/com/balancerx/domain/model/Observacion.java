package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.SeveridadObservacion;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Observacion {
    UUID id;
    UUID cuadreId;
    UUID autorId;
    SeveridadObservacion severidad;
    String texto;
    Instant createdAt;
}
