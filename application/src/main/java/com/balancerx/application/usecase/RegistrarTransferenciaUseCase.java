package com.balancerx.application.usecase;

import com.balancerx.application.command.RegistrarTransferenciaCommand;
import com.balancerx.application.service.FileStoragePort;
import com.balancerx.application.service.PdfSignaturePort;
import com.balancerx.application.service.SignatureStoragePort;
import com.balancerx.domain.model.Archivo;
import com.balancerx.domain.model.Transferencia;
import com.balancerx.domain.model.TransferenciaHistorialEntry;
import com.balancerx.domain.model.Usuario;
import com.balancerx.domain.repository.ArchivoRepository;
import com.balancerx.domain.repository.TransferenciaHistorialRepository;
import com.balancerx.domain.repository.TransferenciaRepository;
import com.balancerx.domain.repository.UsuarioRepository;
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
import org.springframework.util.StringUtils;

@Service
public class RegistrarTransferenciaUseCase {
    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaHistorialRepository historialRepository;
    private final ArchivoRepository archivoRepository;
    private final FileStoragePort fileStoragePort;
    private final UsuarioRepository usuarioRepository;
    private final SignatureStoragePort signatureStoragePort;
    private final PdfSignaturePort pdfSignaturePort;
    private final Clock clock;

    public RegistrarTransferenciaUseCase(TransferenciaRepository transferenciaRepository,
                                         TransferenciaHistorialRepository historialRepository,
                                         ArchivoRepository archivoRepository,
                                         FileStoragePort fileStoragePort,
                                         UsuarioRepository usuarioRepository,
                                         SignatureStoragePort signatureStoragePort,
                                         PdfSignaturePort pdfSignaturePort,
                                         Clock clock) {
        this.transferenciaRepository = transferenciaRepository;
        this.historialRepository = historialRepository;
        this.archivoRepository = archivoRepository;
        this.fileStoragePort = fileStoragePort;
        this.usuarioRepository = usuarioRepository;
        this.signatureStoragePort = signatureStoragePort;
        this.pdfSignaturePort = pdfSignaturePort;
        this.clock = clock;
    }

    @Transactional
    public Transferencia handle(RegistrarTransferenciaCommand command) {
        validar(command);

        Usuario remitente = usuarioRepository.findById(command.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario remitente no encontrado"));
        if (!remitente.isActivo()) {
            throw new IllegalArgumentException("El usuario remitente se encuentra inactivo");
        }

        String firmaPath = remitente.getFirmaPath()
                .filter(StringUtils::hasText)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no tiene firma electrónica configurada"));
        byte[] firmaBytes = signatureStoragePort.loadSignature(firmaPath);
        byte[] pdfFirmado = pdfSignaturePort.applySignature(command.getContenido(), firmaBytes);

        command.getReceptorId().ifPresent(receptorId -> {
            Usuario receptor = usuarioRepository.findById(receptorId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario receptor no encontrado"));
            if (!receptor.isActivo()) {
                throw new IllegalArgumentException("El usuario receptor se encuentra inactivo");
            }
        });

        FileStoragePort.StoredFile storedFile = fileStoragePort.storePdf(command.getNombreArchivo(), pdfFirmado);
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
                .puntoVentaTexto(command.getPuntoVenta()
                        .map(String::trim)
                        .filter(StringUtils::hasText)
                        .orElse(null))
                .valorTexto(command.getValor().toPlainString())
                .fechaTexto(command.getFecha().toString())
                .actualizadoPor(command.getUsuarioId())
                .actualizadoEn(now)
                .firmadaPor(remitente.getId())
                .firmadaEn(now)
                .receptorId(command.getReceptorId().orElse(null));

        List<TransferenciaHistorialEntry> historial = new ArrayList<>();
        historial.add(new TransferenciaHistorialEntry(
                UUID.randomUUID(),
                transferenciaId,
                "REGISTRADA",
                command.getUsuarioId(),
                now,
                metadataRegistro(command)
        ));

        historial.add(new TransferenciaHistorialEntry(
                UUID.randomUUID(),
                transferenciaId,
                "FIRMADA",
                remitente.getId(),
                now,
                metadataFirma(remitente)
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

        command.getReceptorId().ifPresent(receptorId -> historial.add(new TransferenciaHistorialEntry(
                UUID.randomUUID(),
                transferenciaId,
                "ENVIADA",
                command.getUsuarioId(),
                now,
                metadataEnvio(receptorId)
        )));

        Transferencia transferencia = builder.build();
        transferenciaRepository.save(transferencia);
        historialRepository.saveAll(historial);
        return transferencia;
    }

    private void validar(RegistrarTransferenciaCommand command) {
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
        if (command.getReceptorId().isPresent() && command.getReceptorId().get().equals(command.getUsuarioId())) {
            throw new IllegalArgumentException("El usuario receptor debe ser diferente al remitente");
        }
        command.getTipoAsignacion().ifPresent(tipo -> {
            if (tipo.requiereDestino() && command.getDestinoId().isEmpty()) {
                throw new IllegalArgumentException("La asignación seleccionada requiere un destino");
            }
        });
    }

    private String metadataRegistro(RegistrarTransferenciaCommand command) {
        StringBuilder builder = new StringBuilder("{\"banco\":\"")
                .append(command.getBanco().name())
                .append("\",\"valor\":\"")
                .append(command.getValor())
                .append("\",\"fecha\":\"")
                .append(command.getFecha())
                .append("\"");
        command.getPuntoVenta()
                .filter(StringUtils::hasText)
                .ifPresent(pv -> builder.append(",\"puntoVenta\":\"").append(escape(pv.trim())).append("\""));
        builder.append('}');
        return builder.toString();
    }

    private String metadataAsignacion(TipoAsignacionTransferencia tipo,
                                      UUID destinoId) {
        String destino = destinoId != null ? destinoId.toString() : "";
        return "{\"tipo\":\"" + tipo.name() + "\",\"destino\":\"" + destino + "\"}";
    }

    private String metadataFirma(Usuario usuario) {
        return "{\"firmadaPor\":\"" + usuario.getId() + "\"}";
    }

    private String metadataEnvio(UUID receptorId) {
        return "{\"receptorId\":\"" + receptorId + "\"}";
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
