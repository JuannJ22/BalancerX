package com.balancerx.application.usecase;

import com.balancerx.application.query.ConsultarTransferenciasQuery;
import com.balancerx.domain.model.Transferencia;
import com.balancerx.domain.repository.TransferenciaRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ConsultarTransferenciasUseCase {
    private final TransferenciaRepository transferenciaRepository;

    public ConsultarTransferenciasUseCase(TransferenciaRepository transferenciaRepository) {
        this.transferenciaRepository = transferenciaRepository;
    }

    public List<Transferencia> handle(ConsultarTransferenciasQuery query) {
        return transferenciaRepository.search(
                query.getBanco(),
                query.getFechaDesde(),
                query.getFechaHasta(),
                query.getValorMin(),
                query.getValorMax(),
                query.getTipoAsignacion(),
                query.getDestinoId(),
                query.getEstado()
        );
    }
}
