package com.balancerx.model.entity;

import java.time.LocalDateTime;

/**
 * Entidad que representa un punto de venta en el sistema.
 */
public class PuntoVenta {
    private Long id;
    private String nombre;
    private boolean activo;
    private LocalDateTime createdAt;

    // Constructores
    public PuntoVenta() {
    }

    public PuntoVenta(Long id, String nombre, boolean activo, LocalDateTime createdAt) {
        this.id = id;
        this.nombre = nombre;
        this.activo = activo;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PuntoVenta{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", activo=" + activo +
                ", createdAt=" + createdAt +
                '}';
    }
}