package com.balancerx.application.command;

import com.balancerx.domain.valueobject.TipoAsignacionTransferencia;
import java.time.Instant;
import java.util.UUID;

public class AsignarTransferenciaCommand {
    private final UUID transferenciaId;
    private final TipoAsignacionTransferencia tipoAsignacion;
    private final UUID destinoId;
    private final UUID usuarioId;
    private final Instant timestamp;

    public AsignarTransferenciaCommand(UUID transferenciaId, TipoAsignacionTransferencia tipoAsignacion,
                                       UUID destinoId, UUID usuarioId, Instant timestamp) {
        this.transferenciaId = transferenciaId;
        this.tipoAsignacion = tipoAsignacion;
        this.destinoId = destinoId;
        this.usuarioId = usuarioId;
        this.timestamp = timestamp;
    }

    public UUID getTransferenciaId() {
        return transferenciaId;
    }

    public TipoAsignacionTransferencia getTipoAsignacion() {
        return tipoAsignacion;
    }

    public UUID getDestinoId() {
        return destinoId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
