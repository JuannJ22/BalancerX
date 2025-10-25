package com.balancerx.infrastructure.persistence.mapper;

import com.balancerx.domain.model.Ajuste;
import com.balancerx.infrastructure.persistence.jpa.JpaAjuste;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AjusteMapper {
    public JpaAjuste toEntity(Ajuste ajuste) {
        JpaAjuste entity = new JpaAjuste();
        entity.setId(ajuste.getId() != null ? ajuste.getId() : UUID.randomUUID());
        entity.setCuadreId(ajuste.getCuadreId());
        entity.setTipo(ajuste.getTipo());
        entity.setMonto(ajuste.getMonto());
        entity.setMotivo(ajuste.getMotivo());
        entity.setAutorId(ajuste.getAutorId());
        entity.setCreatedAt(ajuste.getCreatedAt() != null ? ajuste.getCreatedAt() : Instant.now());
        return entity;
    }

    public Ajuste toDomain(JpaAjuste entity) {
        return new Ajuste(
                entity.getId(),
                entity.getCuadreId(),
                entity.getTipo(),
                entity.getMonto(),
                entity.getMotivo(),
                entity.getAutorId(),
                entity.getCreatedAt()
        );
    }
}
