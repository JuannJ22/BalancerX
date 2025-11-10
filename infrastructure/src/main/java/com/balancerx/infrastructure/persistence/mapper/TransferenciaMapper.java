package com.balancerx.infrastructure.persistence.mapper;

import com.balancerx.domain.model.Transferencia;
import com.balancerx.domain.valueobject.EstadoTransferencia;
import com.balancerx.infrastructure.persistence.jpa.JpaTransferencia;
import org.springframework.stereotype.Component;

@Component
public class TransferenciaMapper {
    public JpaTransferencia toEntity(Transferencia transferencia) {
        JpaTransferencia entity = new JpaTransferencia();
        entity.setId(transferencia.getId());
        entity.setBanco(transferencia.getBanco());
        entity.setFecha(transferencia.getFecha());
        entity.setValor(transferencia.getValor());
        entity.setComentario(transferencia.getComentario());
        entity.setArchivoId(transferencia.getArchivoId().orElse(null));
        entity.setCargadoPor(transferencia.getCargadoPor());
        entity.setCreatedAt(transferencia.getCreatedAt());
        entity.setEstado(transferencia.getEstado());
        entity.setTipoAsignacion(transferencia.getTipoAsignacion().orElse(null));
        entity.setDestinoId(transferencia.getDestinoId().orElse(null));
        entity.setAsignadoPor(transferencia.getAsignadoPor().orElse(null));
        entity.setAsignadoEn(transferencia.getAsignadoEn().orElse(null));
        entity.setCuentaContable(transferencia.getCuentaContable().orElse(null));
        entity.setCuentaBancaria(transferencia.getCuentaBancaria().orElse(null));
        entity.setPuntoVentaTexto(transferencia.getPuntoVentaTexto().orElse(null));
        entity.setValorTexto(transferencia.getValorTexto().orElse(null));
        entity.setFechaTexto(transferencia.getFechaTexto().orElse(null));
        entity.setActualizadoPor(transferencia.getActualizadoPor().orElse(null));
        entity.setActualizadoEn(transferencia.getActualizadoEn().orElse(null));
        entity.setReceptorId(transferencia.getReceptorId().orElse(null));
        entity.setFirmadaPor(transferencia.getFirmadaPor().orElse(null));
        entity.setFirmadaEn(transferencia.getFirmadaEn().orElse(null));
        entity.setRecibidaPor(transferencia.getRecibidaPor().orElse(null));
        entity.setRecibidaEn(transferencia.getRecibidaEn().orElse(null));
        entity.setImpresaPor(transferencia.getImpresaPor().orElse(null));
        entity.setImpresaEn(transferencia.getImpresaEn().orElse(null));
        entity.setComentarioRecepcion(transferencia.getComentarioRecepcion().orElse(null));
        entity.setVersion(transferencia.getVersion());
        return entity;
    }

    public Transferencia toDomain(JpaTransferencia entity) {
        Transferencia.Builder builder = Transferencia.builder()
                .id(entity.getId())
                .banco(entity.getBanco())
                .fecha(entity.getFecha())
                .valor(entity.getValor())
                .comentario(entity.getComentario())
                .archivoId(entity.getArchivoId())
                .cargadoPor(entity.getCargadoPor())
                .createdAt(entity.getCreatedAt())
                .estado(entity.getEstado() != null ? entity.getEstado() : EstadoTransferencia.REGISTRADA)
                .puntoVentaTexto(entity.getPuntoVentaTexto())
                .valorTexto(entity.getValorTexto())
                .fechaTexto(entity.getFechaTexto())
                .firmadaPor(entity.getFirmadaPor())
                .firmadaEn(entity.getFirmadaEn())
                .recibidaPor(entity.getRecibidaPor())
                .recibidaEn(entity.getRecibidaEn())
                .impresaPor(entity.getImpresaPor())
                .impresaEn(entity.getImpresaEn())
                .comentarioRecepcion(entity.getComentarioRecepcion())
                .receptorId(entity.getReceptorId())
                .version(entity.getVersion());

        if (entity.getTipoAsignacion() != null) {
            builder.tipoAsignacion(entity.getTipoAsignacion());
        }
        if (entity.getDestinoId() != null) {
            builder.destinoId(entity.getDestinoId());
        }
        if (entity.getAsignadoPor() != null) {
            builder.asignadoPor(entity.getAsignadoPor());
        }
        if (entity.getAsignadoEn() != null) {
            builder.asignadoEn(entity.getAsignadoEn());
        }
        if (entity.getCuentaContable() != null) {
            builder.cuentaContable(entity.getCuentaContable());
        }
        if (entity.getCuentaBancaria() != null) {
            builder.cuentaBancaria(entity.getCuentaBancaria());
        }
        if (entity.getActualizadoPor() != null) {
            builder.actualizadoPor(entity.getActualizadoPor());
        }
        if (entity.getActualizadoEn() != null) {
            builder.actualizadoEn(entity.getActualizadoEn());
        }

        return builder.build();
    }
}
