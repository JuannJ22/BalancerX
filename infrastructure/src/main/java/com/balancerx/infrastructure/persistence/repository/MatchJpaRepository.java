package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.infrastructure.persistence.jpa.JpaMatch;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchJpaRepository extends JpaRepository<JpaMatch, UUID> {
    List<JpaMatch> findByDocumentoId(UUID documentoId);

    List<JpaMatch> findByDocumentoIdIn(List<UUID> documentoIds);

    List<JpaMatch> findByMovimientoBancarioId(UUID movimientoId);
}
