package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.domain.model.DocumentoContable;
import com.balancerx.domain.repository.DocumentoContableRepository;
import com.balancerx.infrastructure.persistence.mapper.DocumentoContableMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DocumentoContableRepositoryAdapter implements DocumentoContableRepository {
    private final DocumentoContableJpaRepository jpaRepository;
    private final DocumentoContableMapper mapper;

    @Override
    public DocumentoContable save(DocumentoContable documento) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(documento)));
    }

    @Override
    public void saveAll(List<DocumentoContable> documentos) {
        jpaRepository.saveAll(documentos.stream().map(mapper::toEntity).toList());
    }

    @Override
    public Optional<DocumentoContable> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<DocumentoContable> findByCuadreId(UUID cuadreId) {
        return jpaRepository.findByCuadreId(cuadreId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<DocumentoContable> findByFecha(LocalDate fecha) {
        return jpaRepository.findByFecha(fecha).stream().map(mapper::toDomain).toList();
    }
}
