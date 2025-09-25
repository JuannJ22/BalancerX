package com.balancerx.application.usecase;

import com.balancerx.application.query.GenerarReporteQuery;
import com.balancerx.application.service.ReporteGeneratorPort;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenerarReporteUseCase {
    private final ReporteGeneratorPort reporteGeneratorPort;

    public Map<String, Object> handle(GenerarReporteQuery query) {
        return reporteGeneratorPort.generar(query);
    }
}
