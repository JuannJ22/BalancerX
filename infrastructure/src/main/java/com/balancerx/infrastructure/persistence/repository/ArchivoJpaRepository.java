package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.infrastructure.persistence.jpa.JpaArchivo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivoJpaRepository extends JpaRepository<JpaArchivo, UUID> {
    Optional<JpaArchivo> findFirstByCuadreId(UUID cuadreId);

    List<JpaArchivo> findByTipo(String tipo);
}
