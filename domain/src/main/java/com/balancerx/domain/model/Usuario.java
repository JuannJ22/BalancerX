package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.RolUsuario;
import java.time.Instant;
import java.util.UUID;

public class Usuario {
    private final UUID id;
    private final String nombre;
    private final String email;
    private final RolUsuario rol;
    private final String hashPassword;
    private final boolean activo;
    private final Instant createdAt;
    private final String firmaPath;
    private final String firmaChecksum;

    public Usuario(UUID id, String nombre, String email, RolUsuario rol,
                   String hashPassword, boolean activo, Instant createdAt,
                   String firmaPath, String firmaChecksum) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.hashPassword = hashPassword;
        this.activo = activo;
        this.createdAt = createdAt;
        this.firmaPath = firmaPath;
        this.firmaChecksum = firmaChecksum;
    }

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

    public java.util.Optional<String> getFirmaPath() {
        return java.util.Optional.ofNullable(firmaPath);
    }

    public java.util.Optional<String> getFirmaChecksum() {
        return java.util.Optional.ofNullable(firmaChecksum);
    }

    public Usuario withId(UUID id) {
        return new Usuario(id, this.nombre, this.email, this.rol, this.hashPassword, this.activo, this.createdAt,
                this.firmaPath, this.firmaChecksum);
    }

    public Usuario withNombre(String nombre) {
        return new Usuario(this.id, nombre, this.email, this.rol, this.hashPassword, this.activo, this.createdAt,
                this.firmaPath, this.firmaChecksum);
    }

    public Usuario withEmail(String email) {
        return new Usuario(this.id, this.nombre, email, this.rol, this.hashPassword, this.activo, this.createdAt,
                this.firmaPath, this.firmaChecksum);
    }

    public Usuario withRol(RolUsuario rol) {
        return new Usuario(this.id, this.nombre, this.email, rol, this.hashPassword, this.activo, this.createdAt,
                this.firmaPath, this.firmaChecksum);
    }

    public Usuario withHashPassword(String hashPassword) {
        return new Usuario(this.id, this.nombre, this.email, this.rol, hashPassword, this.activo, this.createdAt,
                this.firmaPath, this.firmaChecksum);
    }

    public Usuario withActivo(boolean activo) {
        return new Usuario(this.id, this.nombre, this.email, this.rol, this.hashPassword, activo, this.createdAt,
                this.firmaPath, this.firmaChecksum);
    }

    public Usuario withCreatedAt(Instant createdAt) {
        return new Usuario(this.id, this.nombre, this.email, this.rol, this.hashPassword, this.activo, createdAt,
                this.firmaPath, this.firmaChecksum);
    }

    public Usuario withFirma(String firmaPath, String firmaChecksum) {
        return new Usuario(this.id, this.nombre, this.email, this.rol, this.hashPassword, this.activo, this.createdAt,
                firmaPath, firmaChecksum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return activo == usuario.activo &&
               java.util.Objects.equals(id, usuario.id) &&
               java.util.Objects.equals(nombre, usuario.nombre) &&
               java.util.Objects.equals(email, usuario.email) &&
               rol == usuario.rol &&
               java.util.Objects.equals(hashPassword, usuario.hashPassword) &&
               java.util.Objects.equals(createdAt, usuario.createdAt) &&
               java.util.Objects.equals(firmaPath, usuario.firmaPath) &&
               java.util.Objects.equals(firmaChecksum, usuario.firmaChecksum);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, nombre, email, rol, hashPassword, activo, createdAt, firmaPath, firmaChecksum);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", rol=" + rol +
                ", hashPassword='[PROTECTED]'" +
                ", activo=" + activo +
                ", createdAt=" + createdAt +
                ", firmaPath='" + (firmaPath != null ? "***" : null) + '\'' +
                '}';
    }
}
