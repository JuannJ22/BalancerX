package com.balancerx.application.usecase;

import com.balancerx.application.command.RegistrarTransferenciaCommand;
import com.balancerx.application.service.FileStoragePort;
import com.balancerx.domain.model.Archivo;
import com.balancerx.domain.model.Transferencia;
import com.balancerx.domain.model.TransferenciaHistorialEntry;
import com.balancerx.domain.repository.ArchivoRepository;
import com.balancerx.domain.repository.TransferenciaHistorialRepository;
import com.balancerx.domain.repository.TransferenciaRepository;
import com.balancerx.domain.valueobject.EstadoTransferencia;
import com.balancerx.domain.valueobject.TipoAsignacionTransferencia;
import com.balancerx.domain.valueobject.TipoArchivo;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarTransferenciaUseCase {
    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaHistorialRepository historialRepository;
    private final ArchivoRepository archivoRepository;
    private final FileStoragePort fileStoragePort;
    private final Clock clock;

    public RegistrarTransferenciaUseCase(TransferenciaRepository transferenciaRepository,
                                         TransferenciaHistorialRepository historialRepository,
                                         ArchivoRepository archivoRepository,
                                         FileStoragePort fileStoragePort,
                                         Clock clock) {
        this.transferenciaRepository = transferenciaRepository;
        this.historialRepository = historialRepository;
        this.archivoRepository = archivoRepository;
        this.fileStoragePort = fileStoragePort;
        this.clock = clock;
    }

    @Transactional
    public Transferencia handle(RegistrarTransferenciaCommand command) {
        if (command.getNombreArchivo() == null || command.getNombreArchivo().isBlank()) {
            throw new IllegalArgumentException("El nombre del archivo es obligatorio");
        }
        if (command.getContenido() == null || command.getContenido().length == 0) {
            throw new IllegalArgumentException("El archivo de la transferencia es obligatorio");
        }
        if (command.getValor() == null || BigDecimal.ZERO.compareTo(command.getValor()) >= 0) {
            throw new IllegalArgumentException("El valor de la transferencia debe ser mayor a cero");
        }
        if (command.getFecha() == null) {
            throw new IllegalArgumentException("La fecha de la transferencia es obligatoria");
        }
        command.getTipoAsignacion().ifPresent(tipo -> {
            if (tipo.requiereDestino() && command.getDestinoId().isEmpty()) {
                throw new IllegalArgumentException("La asignación seleccionada requiere un destino");
            }
        });

        FileStoragePort.StoredFile storedFile = fileStoragePort.storePdf(command.getNombreArchivo(), command.getContenido());
        Instant now = Instant.now(clock);

        Archivo archivo = new Archivo(
                UUID.randomUUID(),
                TipoArchivo.PDF,
                storedFile.path(),
                storedFile.checksum(),
                "{\"tipo\":\"TRANSFERENCIA\"}",
                command.getUsuarioId(),
                null,
                now
        );
        archivoRepository.save(archivo);

        UUID transferenciaId = UUID.randomUUID();

        Transferencia.Builder builder = Transferencia.builder()
                .id(transferenciaId)
                .banco(command.getBanco())
                .fecha(command.getFecha())
                .valor(command.getValor())
                .comentario(command.getComentario())
                .archivoId(archivo.getId())
                .cargadoPor(command.getUsuarioId())
                .createdAt(now)
                .actualizadoPor(command.getUsuarioId())
                .actualizadoEn(now);

        List<TransferenciaHistorialEntry> historial = new ArrayList<>();
        historial.add(new TransferenciaHistorialEntry(
                UUID.randomUUID(),
                transferenciaId,
                "REGISTRADA",
                command.getUsuarioId(),
                now,
                metadataRegistro(command)
        ));

        if (command.getTipoAsignacion().isPresent()) {
            builder.estado(EstadoTransferencia.ASIGNADA)
                    .tipoAsignacion(command.getTipoAsignacion().get())
                    .destinoId(command.getDestinoId().orElse(null))
                    .asignadoPor(command.getUsuarioId())
                    .asignadoEn(now);

            historial.add(new TransferenciaHistorialEntry(
                    UUID.randomUUID(),
                    transferenciaId,
                    "ASIGNADA",
                    command.getUsuarioId(),
                    now,
                    metadataAsignacion(command.getTipoAsignacion().get(), command.getDestinoId().orElse(null))
            ));
        } else {
            builder.estado(EstadoTransferencia.REGISTRADA);
        }

        Transferencia transferencia = builder.build();
        transferenciaRepository.save(transferencia);
        historialRepository.saveAll(historial);
        return transferencia;
    }

    private String metadataRegistro(RegistrarTransferenciaCommand command) {
        return "{\"banco\":\"" + command.getBanco().name() + "\"," +
                "\"valor\":\"" + command.getValor() + "\"," +
                "\"fecha\":\"" + command.getFecha() + "\"}";
    }

    private String metadataAsignacion(TipoAsignacionTransferencia tipo,
                                      UUID destinoId) {
        String destino = destinoId != null ? destinoId.toString() : "";
        return "{\"tipo\":\"" + tipo.name() + "\",\"destino\":\"" + destino + "\"}";
    }
}
