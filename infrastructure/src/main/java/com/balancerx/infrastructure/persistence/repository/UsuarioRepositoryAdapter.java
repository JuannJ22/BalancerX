package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.domain.model.Usuario;
import com.balancerx.domain.repository.UsuarioRepository;
import com.balancerx.infrastructure.persistence.mapper.UsuarioMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepository {
    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioMapper mapper;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository, UsuarioMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Usuario save(Usuario usuario) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(usuario)));
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaRepository.findByEmailIgnoreCase(email).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Usuario> findByRol(String rol) {
        return jpaRepository.findByRol(rol).stream().map(mapper::toDomain).toList();
    }
}
