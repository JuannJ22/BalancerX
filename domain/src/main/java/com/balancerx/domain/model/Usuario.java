package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.RolUsuario;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder(toBuilder = true)
@With
public class Usuario {
    UUID id;
    String nombre;
    String email;
    RolUsuario rol;
    String hashPassword;
    boolean activo;
    Instant createdAt;
}
