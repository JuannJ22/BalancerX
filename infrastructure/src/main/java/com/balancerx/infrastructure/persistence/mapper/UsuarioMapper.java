package com.balancerx.infrastructure.persistence.mapper;

import com.balancerx.domain.model.Usuario;
import com.balancerx.infrastructure.persistence.jpa.JpaUsuario;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public JpaUsuario toEntity(Usuario usuario) {
        JpaUsuario entity = new JpaUsuario();
        entity.setId(usuario.getId() != null ? usuario.getId() : UUID.randomUUID());
        entity.setNombre(usuario.getNombre());
        entity.setEmail(usuario.getEmail());
        entity.setRol(usuario.getRol());
        entity.setHashPassword(usuario.getHashPassword());
        entity.setActivo(usuario.isActivo());
        entity.setCreatedAt(usuario.getCreatedAt() != null ? usuario.getCreatedAt() : Instant.now());
        entity.setFirmaPath(usuario.getFirmaPath().orElse(null));
        entity.setFirmaChecksum(usuario.getFirmaChecksum().orElse(null));
        return entity;
    }

    public Usuario toDomain(JpaUsuario entity) {
        return new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getRol(),
                entity.getHashPassword(),
                entity.isActivo(),
                entity.getCreatedAt(),
                entity.getFirmaPath(),
                entity.getFirmaChecksum()
        );
    }
}
