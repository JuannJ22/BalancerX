package com.balancerx.infrastructure.persistence.mapper;

import com.balancerx.domain.model.TransferenciaHistorialEntry;
import com.balancerx.infrastructure.persistence.jpa.JpaTransferenciaHistorial;
import org.springframework.stereotype.Component;

@Component
public class TransferenciaHistorialMapper {
    public JpaTransferenciaHistorial toEntity(TransferenciaHistorialEntry entry) {
        JpaTransferenciaHistorial entity = new JpaTransferenciaHistorial();
        entity.setId(entry.getId());
        entity.setTransferenciaId(entry.getTransferenciaId());
        entity.setAccion(entry.getAccion());
        entity.setUsuarioId(entry.getUsuarioId());
        entity.setTimestamp(entry.getTimestamp());
        entity.setMetadataJson(entry.getMetadataJson());
        return entity;
    }

    public TransferenciaHistorialEntry toDomain(JpaTransferenciaHistorial entity) {
        return new TransferenciaHistorialEntry(
                entity.getId(),
                entity.getTransferenciaId(),
                entity.getAccion(),
                entity.getUsuarioId(),
                entity.getTimestamp(),
                entity.getMetadataJson()
        );
    }
}
