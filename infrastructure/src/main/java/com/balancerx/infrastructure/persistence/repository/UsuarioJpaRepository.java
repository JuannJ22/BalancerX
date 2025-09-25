package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.infrastructure.persistence.jpa.JpaUsuario;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioJpaRepository extends JpaRepository<JpaUsuario, UUID> {
    Optional<JpaUsuario> findByEmailIgnoreCase(String email);

    List<JpaUsuario> findByRol(String rol);
}
