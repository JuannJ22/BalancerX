package com.balancerx.model.entity;

import java.time.LocalDateTime;

/**
 * Entidad que representa un usuario del sistema.
 */
public class Usuario {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private String hashPassword;
    private boolean activo;
    private LocalDateTime createdAt;

    // Constructores
    public Usuario() {
    }

    public Usuario(Long id, String nombre, String email, String rol, String hashPassword, boolean activo, LocalDateTime createdAt) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.hashPassword = hashPassword;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
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
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                ", activo=" + activo +
                ", createdAt=" + createdAt +
                '}';
    }
}