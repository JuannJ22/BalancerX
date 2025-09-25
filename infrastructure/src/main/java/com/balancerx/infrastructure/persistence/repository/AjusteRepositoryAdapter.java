package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.domain.model.Ajuste;
import com.balancerx.domain.repository.AjusteRepository;
import com.balancerx.infrastructure.persistence.mapper.AjusteMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AjusteRepositoryAdapter implements AjusteRepository {
    private final AjusteJpaRepository jpaRepository;
    private final AjusteMapper mapper;

    @Override
    public Ajuste save(Ajuste ajuste) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(ajuste)));
    }

    @Override
    public List<Ajuste> findByCuadreId(UUID cuadreId) {
        return jpaRepository.findByCuadreId(cuadreId).stream().map(mapper::toDomain).toList();
    }
}
