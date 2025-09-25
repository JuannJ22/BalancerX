package com.balancerx.infrastructure.persistence.mapper;

import com.balancerx.domain.model.MovimientoBancario;
import com.balancerx.infrastructure.persistence.jpa.JpaMovimientoBancario;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MovimientoBancarioMapper {
    public JpaMovimientoBancario toEntity(MovimientoBancario movimiento) {
        JpaMovimientoBancario entity = new JpaMovimientoBancario();
        entity.setId(movimiento.getId() != null ? movimiento.getId() : UUID.randomUUID());
        entity.setTipo(movimiento.getTipo());
        entity.setBanco(movimiento.getBanco());
        entity.setFecha(movimiento.getFecha());
        entity.setValor(movimiento.getValor());
        entity.setReferenciaBanco(movimiento.getReferenciaBanco());
        entity.setFuente(movimiento.getFuente());
        entity.setAsignadoPor(movimiento.getAsignadoPor().orElse(null));
        entity.setPuntoVentaId(movimiento.getPuntoVentaId().orElse(null));
        entity.setCuadreId(movimiento.getCuadreId().orElse(null));
        entity.setCreatedAt(movimiento.getCreatedAt() != null ? movimiento.getCreatedAt() : Instant.now());
        entity.setVersion(movimiento.getVersion());
        return entity;
    }

    public MovimientoBancario toDomain(JpaMovimientoBancario entity) {
        return MovimientoBancario.builder()
                .id(entity.getId())
                .tipo(entity.getTipo())
                .banco(entity.getBanco())
                .fecha(entity.getFecha())
                .valor(entity.getValor())
                .referenciaBanco(entity.getReferenciaBanco())
                .fuente(entity.getFuente())
                .asignadoPor(entity.getAsignadoPor())
                .puntoVentaId(entity.getPuntoVentaId())
                .cuadreId(entity.getCuadreId())
                .createdAt(entity.getCreatedAt())
                .version(entity.getVersion())
                .build();
    }
}
