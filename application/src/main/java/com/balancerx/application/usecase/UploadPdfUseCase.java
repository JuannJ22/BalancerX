package com.balancerx.application.usecase;

import com.balancerx.application.command.UploadPdfCommand;
import com.balancerx.application.service.FileStoragePort;
import com.balancerx.domain.model.Archivo;
import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.model.Observacion;
import com.balancerx.domain.repository.ArchivoRepository;
import com.balancerx.domain.repository.CuadreRepository;
import com.balancerx.domain.repository.ObservacionRepository;
import com.balancerx.domain.service.OcrPipeline;
import com.balancerx.domain.valueobject.TipoArchivo;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UploadPdfUseCase {
    private final CuadreRepository cuadreRepository;
    private final ArchivoRepository archivoRepository;
    private final ObservacionRepository observacionRepository;
    private final OcrPipeline ocrPipeline;
    private final FileStoragePort fileStoragePort;
    private final Clock clock;

    @Transactional
    public Archivo handle(UploadPdfCommand command) {
        Cuadre cuadre = cuadreRepository
                .findById(command.getCuadreId())
                .orElseThrow(() -> new IllegalArgumentException("Cuadre no encontrado"));

        FileStoragePort.StoredFile storedFile = fileStoragePort.storePdf(command.getNombreArchivo(), command.getContenido());
        Instant now = Instant.now(clock);
        Archivo archivo = Archivo.builder()
                .id(UUID.randomUUID())
                .tipo(TipoArchivo.PDF)
                .path(storedFile.path())
                .checksum(storedFile.checksum())
                .metadataJson("{}")
                .subidoPor(command.getUsuarioId())
                .cuadreId(cuadre.getId())
                .createdAt(now)
                .build();
        archivoRepository.save(archivo);

        List<Observacion> observaciones = ocrPipeline.procesar(archivo);
        observacionRepository.saveAll(observaciones);

        return archivo;
    }
}
