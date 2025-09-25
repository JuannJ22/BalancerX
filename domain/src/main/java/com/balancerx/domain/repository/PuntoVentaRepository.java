package com.balancerx.domain.repository;

import com.balancerx.domain.model.PuntoVenta;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PuntoVentaRepository {
    PuntoVenta save(PuntoVenta puntoVenta);

    Optional<PuntoVenta> findById(UUID id);

    List<PuntoVenta> findAll();

    boolean existsByNombre(String nombre);
}
