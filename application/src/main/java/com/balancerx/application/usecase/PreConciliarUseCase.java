package com.balancerx.application.usecase;

import com.balancerx.application.command.PreConciliarCommand;
import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.model.DocumentoContable;
import com.balancerx.domain.model.Match;
import com.balancerx.domain.model.MovimientoBancario;
import com.balancerx.domain.repository.CuadreRepository;
import com.balancerx.domain.repository.DocumentoContableRepository;
import com.balancerx.domain.repository.MatchRepository;
import com.balancerx.domain.repository.MovimientoBancarioRepository;
import com.balancerx.domain.service.ConciliacionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreConciliarUseCase {
    private final CuadreRepository cuadreRepository;
    private final DocumentoContableRepository documentoContableRepository;
    private final MovimientoBancarioRepository movimientoBancarioRepository;
    private final MatchRepository matchRepository;
    private final ConciliacionService conciliacionService;

    @Transactional
    public List<Match> handle(PreConciliarCommand command) {
        Cuadre cuadre = cuadreRepository
                .findById(command.getCuadreId())
                .orElseThrow(() -> new IllegalArgumentException("Cuadre no encontrado"));
        List<DocumentoContable> documentos = documentoContableRepository.findByCuadreId(cuadre.getId());
        List<MovimientoBancario> movimientos = movimientoBancarioRepository.findByCuadreId(cuadre.getId());

        List<Match> matches = conciliacionService.conciliar(cuadre, documentos, movimientos);
        matchRepository.saveAll(matches);
        return matches;
    }
}
