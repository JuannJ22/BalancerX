package com.balancerx.application.service;

import com.balancerx.application.query.GenerarReporteQuery;
import java.util.Map;

public interface ReporteGeneratorPort {
    Map<String, Object> generar(GenerarReporteQuery query);
}
