package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.MetodoFirma;
import com.balancerx.domain.valueobject.RolUsuario;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Firma {
    UUID id;
    UUID cuadreId;
    RolUsuario rol;
    UUID firmanteId;
    MetodoFirma metodo;
    String hash;
    Instant timestamp;
}
