package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.domain.model.Firma;
import com.balancerx.domain.repository.FirmaRepository;
import com.balancerx.infrastructure.persistence.mapper.FirmaMapper;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository

public class FirmaRepositoryAdapter implements FirmaRepository {
    private final FirmaJpaRepository jpaRepository;
    private final FirmaMapper mapper;

    public FirmaRepositoryAdapter(FirmaJpaRepository jpaRepository, FirmaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Firma save(Firma firma) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(firma)));
    }

    @Override
    public List<Firma> findByCuadreId(UUID cuadreId) {
        return jpaRepository.findByCuadreId(cuadreId).stream().map(mapper::toDomain).toList();
    }
}
