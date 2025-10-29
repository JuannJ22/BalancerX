package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.domain.model.Observacion;
import com.balancerx.domain.repository.ObservacionRepository;
import com.balancerx.infrastructure.persistence.mapper.ObservacionMapper;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository

public class ObservacionRepositoryAdapter implements ObservacionRepository {
    private final ObservacionJpaRepository jpaRepository;
    private final ObservacionMapper mapper;

    public ObservacionRepositoryAdapter(ObservacionJpaRepository jpaRepository, ObservacionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Observacion save(Observacion observacion) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(observacion)));
    }

    @Override
    public void saveAll(List<Observacion> observaciones) {
        jpaRepository.saveAll(observaciones.stream().map(mapper::toEntity).toList());
    }

    @Override
    public List<Observacion> findByCuadreId(UUID cuadreId) {
        return jpaRepository.findByCuadreId(cuadreId).stream().map(mapper::toDomain).toList();
    }
}
