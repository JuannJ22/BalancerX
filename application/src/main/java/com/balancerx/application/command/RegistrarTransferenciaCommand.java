package com.balancerx.application.command;

import com.balancerx.domain.valueobject.BancoTransferencia;
import com.balancerx.domain.valueobject.TipoAsignacionTransferencia;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class RegistrarTransferenciaCommand {
    private final UUID usuarioId;
    private final String nombreArchivo;
    private final byte[] contenido;
    private final BancoTransferencia banco;
    private final LocalDate fecha;
    private final BigDecimal valor;
    private final String comentario;
    private final TipoAsignacionTransferencia tipoAsignacion;
    private final UUID destinoId;

    public RegistrarTransferenciaCommand(UUID usuarioId, String nombreArchivo, byte[] contenido,
                                         BancoTransferencia banco, LocalDate fecha, BigDecimal valor,
                                         String comentario, TipoAsignacionTransferencia tipoAsignacion,
                                         UUID destinoId) {
        this.usuarioId = usuarioId;
        this.nombreArchivo = nombreArchivo;
        this.contenido = contenido;
        this.banco = banco;
        this.fecha = fecha;
        this.valor = valor;
        this.comentario = comentario;
        this.tipoAsignacion = tipoAsignacion;
        this.destinoId = destinoId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public BancoTransferencia getBanco() {
        return banco;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getComentario() {
        return comentario;
    }

    public Optional<TipoAsignacionTransferencia> getTipoAsignacion() {
        return Optional.ofNullable(tipoAsignacion);
    }

    public Optional<UUID> getDestinoId() {
        return Optional.ofNullable(destinoId);
    }
}
