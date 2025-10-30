package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.infrastructure.persistence.jpa.JpaTransferenciaHistorial;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferenciaHistorialJpaRepository extends JpaRepository<JpaTransferenciaHistorial, UUID> {
    List<JpaTransferenciaHistorial> findByTransferenciaIdOrderByTimestampAsc(UUID transferenciaId);
}
