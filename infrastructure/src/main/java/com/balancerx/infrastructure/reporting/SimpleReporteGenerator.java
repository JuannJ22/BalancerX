package com.balancerx.infrastructure.reporting;

import com.balancerx.application.query.GenerarReporteQuery;
import com.balancerx.application.service.ReporteGeneratorPort;
import com.balancerx.domain.repository.CuadreRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpleReporteGenerator implements ReporteGeneratorPort {
    private final CuadreRepository cuadreRepository;

    @Override
    public Map<String, Object> generar(GenerarReporteQuery query) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("tipo", query.getTipo());
        resultado.put("totalCuadres", cuadreRepository
                .findByFiltros(query.getDesde(), null, query.getPuntoVentaId(), 0, 100)
                .size());
        return resultado;
    }
}
