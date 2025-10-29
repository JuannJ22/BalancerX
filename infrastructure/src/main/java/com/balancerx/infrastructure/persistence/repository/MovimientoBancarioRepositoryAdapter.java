package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.domain.model.MovimientoBancario;
import com.balancerx.domain.repository.MovimientoBancarioRepository;
import com.balancerx.infrastructure.persistence.mapper.MovimientoBancarioMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class MovimientoBancarioRepositoryAdapter implements MovimientoBancarioRepository {
    private final MovimientoBancarioJpaRepository jpaRepository;
    private final MovimientoBancarioMapper mapper;

    public MovimientoBancarioRepositoryAdapter(MovimientoBancarioJpaRepository jpaRepository, MovimientoBancarioMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public MovimientoBancario save(MovimientoBancario movimiento) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(movimiento)));
    }

    @Override
    public void saveAll(List<MovimientoBancario> movimientos) {
        jpaRepository.saveAll(movimientos.stream().map(mapper::toEntity).toList());
    }

    @Override
    public Optional<MovimientoBancario> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<MovimientoBancario> findByCuadreId(UUID cuadreId) {
        return jpaRepository.findByCuadreId(cuadreId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<MovimientoBancario> findByFecha(LocalDate fecha) {
        return jpaRepository.findByFecha(fecha).stream().map(mapper::toDomain).toList();
    }
}
