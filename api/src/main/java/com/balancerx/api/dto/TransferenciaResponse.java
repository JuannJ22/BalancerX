package com.balancerx.api.dto;

import com.balancerx.domain.model.Transferencia;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TransferenciaResponse(
        UUID id,
        String banco,
        LocalDate fecha,
        BigDecimal valor,
        String comentario,
        String estado,
        String tipoAsignacion,
        UUID destinoId,
        UUID receptorId,
        UUID asignadoPor,
        Instant asignadoEn,
        String cuentaContable,
        String cuentaBancaria,
        String puntoVentaTexto,
        String valorTexto,
        String fechaTexto,
        UUID archivoId,
        UUID cargadoPor,
        Instant createdAt,
        UUID firmadaPor,
        Instant firmadaEn,
        UUID recibidaPor,
        Instant recibidaEn,
        UUID impresaPor,
        Instant impresaEn,
        String comentarioRecepcion) {

    public static TransferenciaResponse fromDomain(Transferencia transferencia) {
        return new TransferenciaResponse(
                transferencia.getId(),
                transferencia.getBanco().name(),
                transferencia.getFecha(),
                transferencia.getValor(),
                transferencia.getComentario(),
                transferencia.getEstado().name(),
                transferencia.getTipoAsignacion().map(Enum::name).orElse(null),
                transferencia.getDestinoId().orElse(null),
                transferencia.getReceptorId().orElse(null),
                transferencia.getAsignadoPor().orElse(null),
                transferencia.getAsignadoEn().orElse(null),
                transferencia.getCuentaContable().orElse(null),
                transferencia.getCuentaBancaria().orElse(null),
                transferencia.getPuntoVentaTexto().orElse(null),
                transferencia.getValorTexto().orElse(null),
                transferencia.getFechaTexto().orElse(null),
                transferencia.getArchivoId().orElse(null),
                transferencia.getCargadoPor(),
                transferencia.getCreatedAt(),
                transferencia.getFirmadaPor().orElse(null),
                transferencia.getFirmadaEn().orElse(null),
                transferencia.getRecibidaPor().orElse(null),
                transferencia.getRecibidaEn().orElse(null),
                transferencia.getImpresaPor().orElse(null),
                transferencia.getImpresaEn().orElse(null),
                transferencia.getComentarioRecepcion().orElse(null)
        );
    }
}
