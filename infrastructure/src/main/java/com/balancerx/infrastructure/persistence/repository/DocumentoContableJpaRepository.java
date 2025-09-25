package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.infrastructure.persistence.jpa.JpaDocumentoContable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoContableJpaRepository extends JpaRepository<JpaDocumentoContable, UUID> {
    List<JpaDocumentoContable> findByCuadreId(UUID cuadreId);

    List<JpaDocumentoContable> findByFecha(LocalDate fecha);
}
