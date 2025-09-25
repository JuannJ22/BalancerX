package com.balancerx.domain.repository;

import com.balancerx.domain.model.DocumentoContable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentoContableRepository {
    DocumentoContable save(DocumentoContable documento);

    void saveAll(List<DocumentoContable> documentos);

    Optional<DocumentoContable> findById(UUID id);

    List<DocumentoContable> findByCuadreId(UUID cuadreId);

    List<DocumentoContable> findByFecha(LocalDate fecha);
}
