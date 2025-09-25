package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.domain.valueobject.EstadoCuadre;
import com.balancerx.infrastructure.persistence.jpa.JpaCuadre;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CuadreJpaRepository extends JpaRepository<JpaCuadre, UUID> {
    List<JpaCuadre> findByFechaAndPuntoVentaId(LocalDate fecha, UUID puntoVentaId);

    boolean existsByFechaAndPuntoVentaIdAndEstado(LocalDate fecha, UUID puntoVentaId, EstadoCuadre estado);

    @Query("SELECT c FROM JpaCuadre c WHERE (:fecha IS NULL OR c.fecha = :fecha) "
            + "AND (:estado IS NULL OR c.estado = :estado) "
            + "AND (:puntoVentaId IS NULL OR c.puntoVentaId = :puntoVentaId)")
    Page<JpaCuadre> search(LocalDate fecha, EstadoCuadre estado, UUID puntoVentaId, Pageable pageable);
}
