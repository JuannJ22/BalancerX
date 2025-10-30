package com.balancerx.application.usecase;

import com.balancerx.application.command.AsignarTransferenciaCommand;
import com.balancerx.domain.model.Transferencia;
import com.balancerx.domain.model.TransferenciaHistorialEntry;
import com.balancerx.domain.repository.TransferenciaHistorialRepository;
import com.balancerx.domain.repository.TransferenciaRepository;
import com.balancerx.domain.valueobject.TipoAsignacionTransferencia;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsignarTransferenciaUseCase {
    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaHistorialRepository historialRepository;
    private final Clock clock;

    public AsignarTransferenciaUseCase(TransferenciaRepository transferenciaRepository,
                                       TransferenciaHistorialRepository historialRepository,
                                       Clock clock) {
        this.transferenciaRepository = transferenciaRepository;
        this.historialRepository = historialRepository;
        this.clock = clock;
    }

    @Transactional
    public Transferencia handle(AsignarTransferenciaCommand command) {
        Transferencia transferencia = transferenciaRepository
                .findById(command.getTransferenciaId())
                .orElseThrow(() -> new IllegalArgumentException("Transferencia no encontrada"));

        TipoAsignacionTransferencia tipo = command.getTipoAsignacion();
        if (tipo.requiereDestino() && command.getDestinoId() == null) {
            throw new IllegalArgumentException("La asignación seleccionada requiere un destino");
        }

        Instant now = command.getTimestamp() != null ? command.getTimestamp() : Instant.now(clock);

        Transferencia actualizada = transferencia.asignar(tipo, command.getDestinoId(), command.getUsuarioId(), now);
        transferenciaRepository.save(actualizada);

        TransferenciaHistorialEntry entry = new TransferenciaHistorialEntry(
                UUID.randomUUID(),
                actualizada.getId(),
                "ASIGNADA",
                command.getUsuarioId(),
                now,
                "{\"tipo\":\"" + tipo.name() + "\",\"destino\":\"" +
                        (command.getDestinoId() != null ? command.getDestinoId() : "") + "\"}"
        );
        historialRepository.save(entry);

        return actualizada;
    }
}
