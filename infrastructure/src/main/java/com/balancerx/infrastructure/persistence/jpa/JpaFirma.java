package com.balancerx.infrastructure.persistence.jpa;

import com.balancerx.domain.valueobject.MetodoFirma;
import com.balancerx.domain.valueobject.RolUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "firmas")
@Getter
@Setter
public class JpaFirma {
    @Id
    private UUID id;

    @Column(name = "cuadre_id", nullable = false)
    private UUID cuadreId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;

    @Column(name = "firmante_id", nullable = false)
    private UUID firmanteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoFirma metodo;

    @Column(nullable = false)
    private String hash;

    @Column(nullable = false)
    private Instant timestamp;
}
