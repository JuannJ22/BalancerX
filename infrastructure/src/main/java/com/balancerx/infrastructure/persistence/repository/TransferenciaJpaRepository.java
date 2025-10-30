package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.infrastructure.persistence.jpa.JpaTransferencia;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransferenciaJpaRepository extends JpaRepository<JpaTransferencia, UUID>,
        JpaSpecificationExecutor<JpaTransferencia> {
}
