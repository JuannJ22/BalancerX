package com.balancerx.domain.model;

import java.time.Instant;
import java.util.UUID;

public class PuntoVenta {
    private final UUID id;
    private final String nombre;
    private final boolean activo;
    private final Instant createdAt;

    public PuntoVenta(UUID id, String nombre, boolean activo, Instant createdAt) {
        this.id = id;
        this.nombre = nombre;
        this.activo = activo;
        this.createdAt = createdAt;
    }

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

    public PuntoVenta withId(UUID id) {
        return new PuntoVenta(id, this.nombre, this.activo, this.createdAt);
    }

    public PuntoVenta withNombre(String nombre) {
        return new PuntoVenta(this.id, nombre, this.activo, this.createdAt);
    }

    public PuntoVenta withActivo(boolean activo) {
        return new PuntoVenta(this.id, this.nombre, activo, this.createdAt);
    }

    public PuntoVenta withCreatedAt(Instant createdAt) {
        return new PuntoVenta(this.id, this.nombre, this.activo, createdAt);
    }

    public PuntoVentaBuilder toBuilder() {
        return new PuntoVentaBuilder()
                .id(this.id)
                .nombre(this.nombre)
                .activo(this.activo)
                .createdAt(this.createdAt);
    }

    public static PuntoVentaBuilder builder() {
        return new PuntoVentaBuilder();
    }

    public static class PuntoVentaBuilder {
        private UUID id;
        private String nombre;
        private boolean activo;
        private Instant createdAt;

        public PuntoVentaBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public PuntoVentaBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public PuntoVentaBuilder activo(boolean activo) {
            this.activo = activo;
            return this;
        }

        public PuntoVentaBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PuntoVenta build() {
            return new PuntoVenta(id, nombre, activo, createdAt);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PuntoVenta that = (PuntoVenta) o;
        return activo == that.activo &&
               java.util.Objects.equals(id, that.id) &&
               java.util.Objects.equals(nombre, that.nombre) &&
               java.util.Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, nombre, activo, createdAt);
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

