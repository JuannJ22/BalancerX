package com.balancerx.infrastructure.persistence.jpa;

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
@Table(name = "usuarios")
@Getter
@Setter
public class JpaUsuario {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;

    @Column(name = "hash_password", nullable = false)
    private String hashPassword;

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

    public String getEmail() {
        return email;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public String getHashPassword() {
        return hashPassword;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
