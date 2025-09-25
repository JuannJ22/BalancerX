package com.balancerx.domain.repository;

import com.balancerx.domain.model.Archivo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArchivoRepository {
    Archivo save(Archivo archivo);

    Optional<Archivo> findByCuadreId(UUID cuadreId);

    List<Archivo> findByTipo(String tipo);
}
