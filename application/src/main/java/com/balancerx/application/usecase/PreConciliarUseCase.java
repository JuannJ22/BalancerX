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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreConciliarUseCase {
    private final CuadreRepository cuadreRepository;
    private final MovimientoBancarioRepository movimientoRepository;
    private final DocumentoContableRepository documentoRepository;
    private final MatchRepository matchRepository;
    private final ConciliacionService conciliacionService;

    public PreConciliarUseCase(CuadreRepository cuadreRepository, MovimientoBancarioRepository movimientoRepository,
                              DocumentoContableRepository documentoRepository, MatchRepository matchRepository,
                              ConciliacionService conciliacionService) {
        this.cuadreRepository = cuadreRepository;
        this.movimientoRepository = movimientoRepository;
        this.documentoRepository = documentoRepository;
        this.matchRepository = matchRepository;
        this.conciliacionService = conciliacionService;
    }

    @Transactional
    public List<Match> handle(PreConciliarCommand command) {
        Cuadre cuadre = cuadreRepository
                .findById(command.getCuadreId())
                .orElseThrow(() -> new IllegalArgumentException("Cuadre no encontrado"));
        List<DocumentoContable> documentos = documentoRepository.findByCuadreId(cuadre.getId());
        List<MovimientoBancario> movimientos = movimientoRepository.findByCuadreId(cuadre.getId());

        List<Match> matches = conciliacionService.conciliar(cuadre, documentos, movimientos);
        matchRepository.saveAll(matches);
        return matches;
    }
}
