package com.balancerx.application.usecase;

import com.balancerx.application.command.RegistrarRecepcionTransferenciaCommand;
import com.balancerx.domain.model.Transferencia;
import com.balancerx.domain.model.TransferenciaHistorialEntry;
import com.balancerx.domain.repository.TransferenciaHistorialRepository;
import com.balancerx.domain.repository.TransferenciaRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class RegistrarRecepcionTransferenciaUseCase {
    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaHistorialRepository historialRepository;
    private final Clock clock;

    public RegistrarRecepcionTransferenciaUseCase(TransferenciaRepository transferenciaRepository,
                                                  TransferenciaHistorialRepository historialRepository,
                                                  Clock clock) {
        this.transferenciaRepository = transferenciaRepository;
        this.historialRepository = historialRepository;
        this.clock = clock;
    }

    @Transactional
    public Transferencia handle(RegistrarRecepcionTransferenciaCommand command) {
        Transferencia transferencia = transferenciaRepository.findById(command.getTransferenciaId())
                .orElseThrow(() -> new IllegalArgumentException("Transferencia no encontrada"));

        transferencia.getReceptorId().ifPresent(receptorId -> {
            if (!receptorId.equals(command.getUsuarioId())) {
                throw new IllegalArgumentException("El usuario no está autorizado para recibir esta transferencia");
            }
        });

        Instant now = Instant.now(clock);
        Transferencia actualizada = transferencia.registrarRecepcion(
                command.getUsuarioId(),
                now,
                command.getComentario().map(String::trim).filter(StringUtils::hasText).orElse(null)
        );

        transferenciaRepository.save(actualizada);

        historialRepository.save(new TransferenciaHistorialEntry(
                UUID.randomUUID(),
                actualizada.getId(),
                "IMPRESA",
                command.getUsuarioId(),
                now,
                metadataRecepcion(command)
        ));

        return actualizada;
    }

    private String metadataRecepcion(RegistrarRecepcionTransferenciaCommand command) {
        return command.getComentario()
                .filter(StringUtils::hasText)
                .map(comentario -> "{\"comentario\":\"" + escape(comentario.trim()) + "\"}")
                .orElse("{\"comentario\":\"\"}");
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
