package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.domain.model.Transferencia;
import com.balancerx.domain.repository.TransferenciaRepository;
import com.balancerx.domain.valueobject.BancoTransferencia;
import com.balancerx.domain.valueobject.EstadoTransferencia;
import com.balancerx.domain.valueobject.TipoAsignacionTransferencia;
import com.balancerx.infrastructure.persistence.jpa.JpaTransferencia;
import com.balancerx.infrastructure.persistence.mapper.TransferenciaMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class TransferenciaRepositoryAdapter implements TransferenciaRepository {
    private final TransferenciaJpaRepository jpaRepository;
    private final TransferenciaMapper mapper;

    public TransferenciaRepositoryAdapter(TransferenciaJpaRepository jpaRepository,
                                          TransferenciaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Transferencia save(Transferencia transferencia) {
        JpaTransferencia entity = mapper.toEntity(transferencia);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Transferencia> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Transferencia> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Transferencia> search(Optional<BancoTransferencia> banco,
                                      Optional<LocalDate> fechaDesde,
                                      Optional<LocalDate> fechaHasta,
                                      Optional<TipoAsignacionTransferencia> tipoAsignacion,
                                      Optional<UUID> destinoId,
                                      Optional<EstadoTransferencia> estado) {
        Specification<JpaTransferencia> spec = Specification.where(null);

        if (banco.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("banco"), banco.get()));
        }
        if (fechaDesde.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fecha"), fechaDesde.get()));
        }
        if (fechaHasta.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fecha"), fechaHasta.get()));
        }
        if (tipoAsignacion.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tipoAsignacion"), tipoAsignacion.get()));
        }
        if (destinoId.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("destinoId"), destinoId.get()));
        }
        if (estado.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), estado.get()));
        }

        return jpaRepository.findAll(spec).stream().map(mapper::toDomain).toList();
    }
}
