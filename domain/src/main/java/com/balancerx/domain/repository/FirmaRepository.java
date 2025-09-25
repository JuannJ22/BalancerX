package com.balancerx.domain.repository;

import com.balancerx.domain.model.Firma;
import java.util.List;
import java.util.UUID;

public interface FirmaRepository {
    Firma save(Firma firma);

    List<Firma> findByCuadreId(UUID cuadreId);
}
