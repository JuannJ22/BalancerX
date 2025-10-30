package com.balancerx.domain.repository;

import com.balancerx.domain.model.TransferenciaHistorialEntry;
import java.util.List;
import java.util.UUID;

public interface TransferenciaHistorialRepository {
    void save(TransferenciaHistorialEntry entry);

    void saveAll(List<TransferenciaHistorialEntry> entries);

    List<TransferenciaHistorialEntry> findByTransferenciaId(UUID transferenciaId);
}
