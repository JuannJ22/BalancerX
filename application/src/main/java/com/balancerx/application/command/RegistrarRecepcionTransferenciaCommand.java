package com.balancerx.application.command;

import java.util.Optional;
import java.util.UUID;

public class RegistrarRecepcionTransferenciaCommand {
    private final UUID transferenciaId;
    private final UUID usuarioId;
    private final String comentario;

    public RegistrarRecepcionTransferenciaCommand(UUID transferenciaId, UUID usuarioId, String comentario) {
        this.transferenciaId = transferenciaId;
        this.usuarioId = usuarioId;
        this.comentario = comentario;
    }

    public UUID getTransferenciaId() {
        return transferenciaId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public Optional<String> getComentario() {
        return Optional.ofNullable(comentario);
    }
}
