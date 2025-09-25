package com.balancerx.domain.service;

import com.balancerx.domain.model.Archivo;
import com.balancerx.domain.model.Observacion;
import java.util.List;

public interface OcrPipeline {
    List<Observacion> procesar(Archivo pdfArchivo);
}
