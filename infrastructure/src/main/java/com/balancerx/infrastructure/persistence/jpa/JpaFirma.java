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

    // Métodos setter manuales para resolver errores de compilación
    public void setId(UUID id) {
        this.id = id;
    }

    public void setCuadreId(UUID cuadreId) {
        this.cuadreId = cuadreId;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public void setFirmanteId(UUID firmanteId) {
        this.firmanteId = firmanteId;
    }

    public void setMetodo(MetodoFirma metodo) {
        this.metodo = metodo;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    // Métodos getter manuales para resolver errores de compilación
    public UUID getId() {
        return id;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public UUID getFirmanteId() {
        return firmanteId;
    }

    public MetodoFirma getMetodo() {
        return metodo;
    }

    public String getHash() {
        return hash;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}