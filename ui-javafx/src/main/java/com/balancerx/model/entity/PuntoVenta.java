package com.balancerx.model.entity;

import java.time.LocalDateTime;

/**
 * Entidad que representa un punto de venta en el sistema.
 */
public class PuntoVenta {
    private Long id;
    private String codigo;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
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
    
    public PuntoVenta(Long id, String codigo, String nombre, String direccion, String telefono, String email, boolean activo, LocalDateTime createdAt) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
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
    
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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