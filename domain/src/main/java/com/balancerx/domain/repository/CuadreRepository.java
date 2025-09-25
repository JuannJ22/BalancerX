package com.balancerx.domain.repository;

import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.valueobject.EstadoCuadre;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CuadreRepository {
    Cuadre save(Cuadre cuadre);

    Optional<Cuadre> findById(UUID id);

    List<Cuadre> findByFechaAndPuntoVenta(LocalDate fecha, UUID puntoVentaId);

    boolean existsAprobadoByFechaAndPuntoVenta(LocalDate fecha, UUID puntoVentaId);

    List<Cuadre> findByFiltros(LocalDate fecha, EstadoCuadre estado, UUID puntoVentaId, int page, int size);
}
