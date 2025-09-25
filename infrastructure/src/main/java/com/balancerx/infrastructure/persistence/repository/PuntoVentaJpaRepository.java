package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.infrastructure.persistence.jpa.JpaPuntoVenta;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PuntoVentaJpaRepository extends JpaRepository<JpaPuntoVenta, UUID> {
    boolean existsByNombreIgnoreCase(String nombre);

    Optional<JpaPuntoVenta> findByNombreIgnoreCase(String nombre);
}
