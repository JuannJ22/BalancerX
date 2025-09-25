package com.balancerx.infrastructure.persistence.mapper;

import com.balancerx.domain.model.Firma;
import com.balancerx.infrastructure.persistence.jpa.JpaFirma;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FirmaMapper {
    public JpaFirma toEntity(Firma firma) {
        JpaFirma entity = new JpaFirma();
        entity.setId(firma.getId() != null ? firma.getId() : UUID.randomUUID());
        entity.setCuadreId(firma.getCuadreId());
        entity.setRol(firma.getRol());
        entity.setFirmanteId(firma.getFirmanteId());
        entity.setMetodo(firma.getMetodo());
        entity.setHash(firma.getHash());
        entity.setTimestamp(firma.getTimestamp() != null ? firma.getTimestamp() : Instant.now());
        return entity;
    }

    public Firma toDomain(JpaFirma entity) {
        return Firma.builder()
                .id(entity.getId())
                .cuadreId(entity.getCuadreId())
                .rol(entity.getRol())
                .firmanteId(entity.getFirmanteId())
                .metodo(entity.getMetodo())
                .hash(entity.getHash())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
