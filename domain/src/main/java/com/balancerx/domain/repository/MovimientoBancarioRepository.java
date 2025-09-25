package com.balancerx.domain.repository;

import com.balancerx.domain.model.MovimientoBancario;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MovimientoBancarioRepository {
    MovimientoBancario save(MovimientoBancario movimiento);

    void saveAll(List<MovimientoBancario> movimientos);

    Optional<MovimientoBancario> findById(UUID id);

    List<MovimientoBancario> findByCuadreId(UUID cuadreId);

    List<MovimientoBancario> findByFecha(LocalDate fecha);
}
