package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.repository.CuadreRepository;
import com.balancerx.domain.valueobject.EstadoCuadre;
import com.balancerx.infrastructure.persistence.mapper.CuadreMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CuadreRepositoryAdapter implements CuadreRepository {
    private final CuadreJpaRepository jpaRepository;
    private final CuadreMapper mapper;

    @Override
    public Cuadre save(Cuadre cuadre) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(cuadre)));
    }

    @Override
    public Optional<Cuadre> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Cuadre> findByFechaAndPuntoVenta(LocalDate fecha, UUID puntoVentaId) {
        return jpaRepository.findByFechaAndPuntoVentaId(fecha, puntoVentaId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsAprobadoByFechaAndPuntoVenta(LocalDate fecha, UUID puntoVentaId) {
        return jpaRepository.existsByFechaAndPuntoVentaIdAndEstado(fecha, puntoVentaId, EstadoCuadre.APROBADO);
    }

    @Override
    public List<Cuadre> findByFiltros(LocalDate fecha, EstadoCuadre estado, UUID puntoVentaId, int page, int size) {
        return jpaRepository.search(fecha, estado, puntoVentaId, PageRequest.of(page, size)).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
