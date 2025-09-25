package com.balancerx.infrastructure.persistence.mapper;

import com.balancerx.domain.model.PuntoVenta;
import com.balancerx.infrastructure.persistence.jpa.JpaPuntoVenta;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PuntoVentaMapper {
    public JpaPuntoVenta toEntity(PuntoVenta puntoVenta) {
        JpaPuntoVenta entity = new JpaPuntoVenta();
        entity.setId(puntoVenta.getId() != null ? puntoVenta.getId() : UUID.randomUUID());
        entity.setNombre(puntoVenta.getNombre());
        entity.setActivo(puntoVenta.isActivo());
        entity.setCreatedAt(puntoVenta.getCreatedAt() != null ? puntoVenta.getCreatedAt() : Instant.now());
        return entity;
    }

    public PuntoVenta toDomain(JpaPuntoVenta entity) {
        return PuntoVenta.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .activo(entity.isActivo())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
