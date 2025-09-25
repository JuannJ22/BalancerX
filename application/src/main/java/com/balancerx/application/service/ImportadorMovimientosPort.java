package com.balancerx.application.service;

import com.balancerx.domain.model.MovimientoBancario;
import com.balancerx.domain.valueobject.FuenteMovimiento;
import java.io.InputStream;
import java.util.List;

public interface ImportadorMovimientosPort {
    List<MovimientoBancario> importar(FuenteMovimiento fuente, InputStream inputStream);
}
