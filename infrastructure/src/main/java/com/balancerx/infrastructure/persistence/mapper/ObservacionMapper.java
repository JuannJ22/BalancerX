package com.balancerx.infrastructure.persistence.mapper;

import com.balancerx.domain.model.Observacion;
import com.balancerx.infrastructure.persistence.jpa.JpaObservacion;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ObservacionMapper {
    public JpaObservacion toEntity(Observacion observacion) {
        JpaObservacion entity = new JpaObservacion();
        entity.setId(observacion.getId() != null ? observacion.getId() : UUID.randomUUID());
        entity.setCuadreId(observacion.getCuadreId());
        entity.setAutorId(observacion.getAutorId());
        entity.setSeveridad(observacion.getSeveridad());
        entity.setTexto(observacion.getTexto());
        entity.setCreatedAt(observacion.getCreatedAt() != null ? observacion.getCreatedAt() : Instant.now());
        return entity;
    }

    public Observacion toDomain(JpaObservacion entity) {
        return new Observacion(
                entity.getId(),
                entity.getCuadreId(),
                entity.getAutorId(),
                entity.getSeveridad(),
                entity.getTexto(),
                entity.getCreatedAt()
        );
    }
}
