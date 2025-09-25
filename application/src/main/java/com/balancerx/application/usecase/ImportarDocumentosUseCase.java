package com.balancerx.application.usecase;

import com.balancerx.application.command.ImportDocumentosCommand;
import com.balancerx.application.service.ImportadorDocumentosPort;
import com.balancerx.domain.model.DocumentoContable;
import com.balancerx.domain.repository.CuadreRepository;
import com.balancerx.domain.repository.DocumentoContableRepository;
import com.balancerx.domain.valueobject.TipoArchivo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImportarDocumentosUseCase {
    private final CuadreRepository cuadreRepository;
    private final DocumentoContableRepository documentoContableRepository;
    private final ImportadorDocumentosPort importadorDocumentosPort;

    @Transactional
    public List<DocumentoContable> handle(ImportDocumentosCommand command) {
        cuadreRepository
                .findById(command.getCuadreId())
                .orElseThrow(() -> new IllegalArgumentException("Cuadre no encontrado"));

        TipoArchivo tipo = TipoArchivo.valueOf(command.getTipoArchivo().toUpperCase());
        List<DocumentoContable> documentos = switch (tipo) {
            case XLSX -> importadorDocumentosPort.importarDesdeExcel(command.getInputStream());
            case CSV -> importadorDocumentosPort.importarDesdeCsv(command.getInputStream());
            default -> throw new IllegalArgumentException("Tipo de archivo no soportado: " + command.getTipoArchivo());
        };

        List<DocumentoContable> enriquecidos = documentos.stream()
                .map(doc -> doc.toBuilder().cuadreId(command.getCuadreId()).build())
                .collect(Collectors.toList());
        documentoContableRepository.saveAll(enriquecidos);
        return enriquecidos;
    }
}
