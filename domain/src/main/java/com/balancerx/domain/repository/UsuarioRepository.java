package com.balancerx.domain.repository;

import com.balancerx.domain.model.Usuario;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findById(UUID id);

    List<Usuario> findByRol(String rol);
}
