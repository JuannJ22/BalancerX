package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.infrastructure.persistence.jpa.JpaAjuste;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AjusteJpaRepository extends JpaRepository<JpaAjuste, UUID> {
    List<JpaAjuste> findByCuadreId(UUID cuadreId);
}
