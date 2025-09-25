package com.balancerx.domain.repository;

import com.balancerx.domain.model.Observacion;
import java.util.List;
import java.util.UUID;

public interface ObservacionRepository {
    Observacion save(Observacion observacion);

    void saveAll(List<Observacion> observaciones);

    List<Observacion> findByCuadreId(UUID cuadreId);
}
