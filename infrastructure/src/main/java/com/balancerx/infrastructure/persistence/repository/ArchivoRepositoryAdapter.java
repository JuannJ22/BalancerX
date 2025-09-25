package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.domain.model.Archivo;
import com.balancerx.domain.repository.ArchivoRepository;
import com.balancerx.infrastructure.persistence.mapper.ArchivoMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArchivoRepositoryAdapter implements ArchivoRepository {
    private final ArchivoJpaRepository jpaRepository;
    private final ArchivoMapper mapper;

    @Override
    public Archivo save(Archivo archivo) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(archivo)));
    }

    @Override
    public Optional<Archivo> findByCuadreId(UUID cuadreId) {
        return jpaRepository.findFirstByCuadreId(cuadreId).map(mapper::toDomain);
    }

    @Override
    public List<Archivo> findByTipo(String tipo) {
        return jpaRepository.findByTipo(tipo).stream().map(mapper::toDomain).toList();
    }
}
