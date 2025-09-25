package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.infrastructure.persistence.jpa.JpaObservacion;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObservacionJpaRepository extends JpaRepository<JpaObservacion, UUID> {
    List<JpaObservacion> findByCuadreId(UUID cuadreId);
}
