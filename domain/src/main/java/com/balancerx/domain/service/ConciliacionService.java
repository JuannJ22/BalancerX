package com.balancerx.domain.service;

import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.model.DocumentoContable;
import com.balancerx.domain.model.Match;
import com.balancerx.domain.model.MovimientoBancario;
import java.util.List;

public interface ConciliacionService {
    List<Match> conciliar(
            Cuadre cuadre, List<DocumentoContable> documentos, List<MovimientoBancario> movimientos);
}
