package com.balancerx.application.usecase;

import com.balancerx.application.command.ImportMovimientosCommand;
import com.balancerx.application.service.ImportadorMovimientosPort;
import com.balancerx.domain.model.MovimientoBancario;
import com.balancerx.domain.repository.MovimientoBancarioRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportarMovimientosUseCase {
    private final MovimientoBancarioRepository movimientoRepository;
    private final MovimientoBancarioRepository movimientoBancarioRepository;
    private final ImportadorMovimientosPort importadorMovimientosPort;
    private final Clock clock;

    public ImportarMovimientosUseCase(MovimientoBancarioRepository movimientoRepository, 
                                      MovimientoBancarioRepository movimientoBancarioRepository,
                                      ImportadorMovimientosPort importadorMovimientosPort, 
                                      Clock clock) {
        this.movimientoRepository = movimientoRepository;
        this.movimientoBancarioRepository = movimientoBancarioRepository;
        this.importadorMovimientosPort = importadorMovimientosPort;
        this.clock = clock;
    }

    @Transactional
    public List<MovimientoBancario> handle(ImportMovimientosCommand command) {
        List<MovimientoBancario> movimientos = importadorMovimientosPort.importar(
                command.getFuente(), command.getInputStream());
        Instant now = Instant.now(clock);
        List<MovimientoBancario> enriquecidos = movimientos.stream()
                .map(mov -> mov.withCreatedAt(now))
                .collect(Collectors.toList());
        movimientoBancarioRepository.saveAll(enriquecidos);
        return enriquecidos;
    }
}
