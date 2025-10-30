package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.domain.model.TransferenciaHistorialEntry;
import com.balancerx.domain.repository.TransferenciaHistorialRepository;
import com.balancerx.infrastructure.persistence.mapper.TransferenciaHistorialMapper;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class TransferenciaHistorialRepositoryAdapter implements TransferenciaHistorialRepository {
    private final TransferenciaHistorialJpaRepository jpaRepository;
    private final TransferenciaHistorialMapper mapper;

    public TransferenciaHistorialRepositoryAdapter(TransferenciaHistorialJpaRepository jpaRepository,
                                                   TransferenciaHistorialMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(TransferenciaHistorialEntry entry) {
        jpaRepository.save(mapper.toEntity(entry));
    }

    @Override
    public void saveAll(List<TransferenciaHistorialEntry> entries) {
        jpaRepository.saveAll(entries.stream().map(mapper::toEntity).toList());
    }

    @Override
    public List<TransferenciaHistorialEntry> findByTransferenciaId(UUID transferenciaId) {
        return jpaRepository.findByTransferenciaIdOrderByTimestampAsc(transferenciaId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
