package com.balancerx.domain.repository;

import com.balancerx.domain.model.Ajuste;
import java.util.List;
import java.util.UUID;

public interface AjusteRepository {
    Ajuste save(Ajuste ajuste);

    List<Ajuste> findByCuadreId(UUID cuadreId);
}
