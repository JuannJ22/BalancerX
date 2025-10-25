package com.balancerx.application.usecase;

import com.balancerx.application.service.ReporteGeneratorPort;
import com.balancerx.application.query.GenerarReporteQuery;
import com.balancerx.domain.repository.CuadreRepository;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class GenerarReporteUseCase {
    private final CuadreRepository cuadreRepository;
    private final ReporteGeneratorPort reporteGeneratorPort;

    public GenerarReporteUseCase(CuadreRepository cuadreRepository, ReporteGeneratorPort reporteGeneratorPort) {
        this.cuadreRepository = cuadreRepository;
        this.reporteGeneratorPort = reporteGeneratorPort;
    }

    public Map<String, Object> handle(GenerarReporteQuery query) {
        return reporteGeneratorPort.generar(query);
    }
}
