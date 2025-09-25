package com.balancerx.infrastructure.persistence.mapper;

import com.balancerx.domain.model.Cuadre;
import com.balancerx.infrastructure.persistence.jpa.JpaCuadre;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CuadreMapper {
    public JpaCuadre toEntity(Cuadre cuadre) {
        JpaCuadre entity = new JpaCuadre();
        entity.setId(cuadre.getId() != null ? cuadre.getId() : UUID.randomUUID());
        entity.setFecha(cuadre.getFecha());
        entity.setPuntoVentaId(cuadre.getPuntoVentaId());
        entity.setEstado(cuadre.getEstado());
        entity.setTotalTirilla(cuadre.getTotalTirilla());
        entity.setTotalBancos(cuadre.getTotalBancos());
        entity.setTotalContable(cuadre.getTotalContable());
        entity.setPdfPath(cuadre.getPdfPath().orElse(null));
        entity.setChecksumPdf(cuadre.getChecksumPdf().orElse(null));
        entity.setCreadoPor(cuadre.getCreadoPor());
        entity.setActualizadoPor(cuadre.getActualizadoPor());
        entity.setFirmadoElabora(cuadre.isFirmadoPorElabora());
        entity.setFirmadoAutoriza(cuadre.isFirmadoPorAutoriza());
        entity.setFirmadoAudita(cuadre.isFirmadoPorAudita());
        entity.setCreatedAt(cuadre.getCreatedAt() != null ? cuadre.getCreatedAt() : Instant.now());
        entity.setUpdatedAt(cuadre.getUpdatedAt() != null ? cuadre.getUpdatedAt() : Instant.now());
        entity.setVersion(cuadre.getVersion());
        return entity;
    }

    public Cuadre toDomain(JpaCuadre entity) {
        return Cuadre.builder()
                .id(entity.getId())
                .fecha(entity.getFecha())
                .puntoVentaId(entity.getPuntoVentaId())
                .estado(entity.getEstado())
                .totalTirilla(entity.getTotalTirilla())
                .totalBancos(entity.getTotalBancos())
                .totalContable(entity.getTotalContable())
                .pdfPath(entity.getPdfPath())
                .checksumPdf(entity.getChecksumPdf())
                .creadoPor(entity.getCreadoPor())
                .actualizadoPor(entity.getActualizadoPor())
                .firmadoElabora(entity.isFirmadoElabora())
                .firmadoAutoriza(entity.isFirmadoAutoriza())
                .firmadoAudita(entity.isFirmadoAudita())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }
}
