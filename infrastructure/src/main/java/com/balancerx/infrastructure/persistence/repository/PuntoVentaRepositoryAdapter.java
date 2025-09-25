package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.domain.model.PuntoVenta;
import com.balancerx.domain.repository.PuntoVentaRepository;
import com.balancerx.infrastructure.persistence.mapper.PuntoVentaMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PuntoVentaRepositoryAdapter implements PuntoVentaRepository {
    private final PuntoVentaJpaRepository jpaRepository;
    private final PuntoVentaMapper mapper;

    @Override
    public PuntoVenta save(PuntoVenta puntoVenta) {
        PuntoVenta enriched = puntoVenta.getCreatedAt() == null
                ? puntoVenta.toBuilder().createdAt(Instant.now()).build()
                : puntoVenta;
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(enriched)));
    }

    @Override
    public Optional<PuntoVenta> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PuntoVenta> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return jpaRepository.existsByNombreIgnoreCase(nombre);
    }
}
