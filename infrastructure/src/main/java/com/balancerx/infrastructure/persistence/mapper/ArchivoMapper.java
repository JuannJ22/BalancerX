package com.balancerx.infrastructure.persistence.mapper;

import com.balancerx.domain.model.Archivo;
import com.balancerx.infrastructure.persistence.jpa.JpaArchivo;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ArchivoMapper {
    public JpaArchivo toEntity(Archivo archivo) {
        JpaArchivo entity = new JpaArchivo();
        entity.setId(archivo.getId() != null ? archivo.getId() : UUID.randomUUID());
        entity.setTipo(archivo.getTipo());
        entity.setPath(archivo.getPath());
        entity.setChecksum(archivo.getChecksum());
        entity.setMetadataJson(archivo.getMetadataJson());
        entity.setSubidoPor(archivo.getSubidoPor());
        entity.setCuadreId(archivo.getCuadreId());
        entity.setCreatedAt(archivo.getCreatedAt() != null ? archivo.getCreatedAt() : Instant.now());
        return entity;
    }

    public Archivo toDomain(JpaArchivo entity) {
        return new Archivo(
                entity.getId(),
                entity.getTipo(),
                entity.getPath(),
                entity.getChecksum(),
                entity.getMetadataJson(),
                entity.getSubidoPor(),
                entity.getCuadreId(),
                entity.getCreatedAt()
        );
    }
}
