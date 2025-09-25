package com.balancerx.infrastructure.persistence.mapper;

import com.balancerx.domain.model.DocumentoContable;
import com.balancerx.infrastructure.persistence.jpa.JpaDocumentoContable;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DocumentoContableMapper {
    public JpaDocumentoContable toEntity(DocumentoContable documento) {
        JpaDocumentoContable entity = new JpaDocumentoContable();
        entity.setId(documento.getId() != null ? documento.getId() : UUID.randomUUID());
        entity.setTipo(documento.getTipo());
        entity.setNumero(documento.getNumero());
        entity.setFecha(documento.getFecha());
        entity.setValor(documento.getValor());
        entity.setReferencia(documento.getReferencia().orElse(null));
        entity.setCuadreId(documento.getCuadreId().orElse(null));
        entity.setObservacion(documento.getObservacion().orElse(null));
        return entity;
    }

    public DocumentoContable toDomain(JpaDocumentoContable entity) {
        return DocumentoContable.builder()
                .id(entity.getId())
                .tipo(entity.getTipo())
                .numero(entity.getNumero())
                .fecha(entity.getFecha())
                .valor(entity.getValor())
                .referencia(entity.getReferencia())
                .cuadreId(entity.getCuadreId())
                .observacion(entity.getObservacion())
                .build();
    }
}
