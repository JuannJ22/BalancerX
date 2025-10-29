package com.balancerx.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "puntos_venta")
@Getter
@Setter
public class JpaPuntoVenta {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // Métodos getter manuales para resolver errores de compilación
    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    // Métodos setter manuales para resolver errores de compilación
    public void setId(UUID id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
