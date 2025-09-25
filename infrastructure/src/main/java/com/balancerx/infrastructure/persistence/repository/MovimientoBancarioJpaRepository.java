package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.infrastructure.persistence.jpa.JpaMovimientoBancario;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoBancarioJpaRepository extends JpaRepository<JpaMovimientoBancario, UUID> {
    List<JpaMovimientoBancario> findByCuadreId(UUID cuadreId);

    List<JpaMovimientoBancario> findByFecha(LocalDate fecha);
}
