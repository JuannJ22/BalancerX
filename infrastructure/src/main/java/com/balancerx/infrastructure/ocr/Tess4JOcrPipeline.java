package com.balancerx.infrastructure.ocr;

import com.balancerx.domain.model.Archivo;
import com.balancerx.domain.model.Observacion;
import com.balancerx.domain.service.OcrPipeline;
import com.balancerx.domain.valueobject.SeveridadObservacion;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Tess4JOcrPipeline implements OcrPipeline {
    private static final Logger log = LoggerFactory.getLogger(Tess4JOcrPipeline.class);
    private static final List<String> ORDEN_OBLIGATORIO = List.of(
            "TIRILLA", "CUADRE", "VOUCHER", "TRANSFERENCIA", "CONSIGNACION", "FORMATO L1");

    @Override
    public List<Observacion> procesar(Archivo pdfArchivo) {
        List<Observacion> observaciones = new ArrayList<>();
        Path path = Path.of(pdfArchivo.getPath());
        try (PDDocument document = PDDocument.load(path.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String texto = stripper.getText(document).toUpperCase();
            validarOrden(pdfArchivo, texto, observaciones);
        } catch (IOException e) {
            log.error("No se pudo procesar el PDF {}", pdfArchivo.getPath(), e);
            observaciones.add(new Observacion(
                    UUID.randomUUID(),
                    pdfArchivo.getCuadreId(),
                    pdfArchivo.getSubidoPor(),
                    SeveridadObservacion.ERROR,
                    "Error procesando PDF: " + e.getMessage(),
                    Instant.now()
            ));
        }
        return observaciones;
    }

    private void validarOrden(Archivo pdfArchivo, String texto, List<Observacion> observaciones) {
        int posicionAnterior = -1;
        for (String marcador : ORDEN_OBLIGATORIO) {
            int index = texto.indexOf(marcador);
            if (index == -1) {
                observaciones.add(new Observacion(
                        UUID.randomUUID(),
                        pdfArchivo.getCuadreId(),
                        pdfArchivo.getSubidoPor(),
                        SeveridadObservacion.WARNING,
                        "Sección no encontrada: " + marcador,
                        Instant.now()
                ));
            } else if (index < posicionAnterior) {
                observaciones.add(new Observacion(
                        UUID.randomUUID(),
                        pdfArchivo.getCuadreId(),
                        pdfArchivo.getSubidoPor(),
                        SeveridadObservacion.ERROR,
                        "Orden incorrecto para sección: " + marcador,
                        Instant.now()
                ));
            }
            posicionAnterior = Math.max(posicionAnterior, index);
        }
    }
}
