package com.balancerx.application.usecase;

import com.balancerx.application.command.RechazarCuadreCommand;
import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.model.Observacion;
import com.balancerx.domain.repository.CuadreRepository;
import com.balancerx.domain.repository.ObservacionRepository;
import com.balancerx.domain.valueobject.EstadoCuadre;
import com.balancerx.domain.valueobject.SeveridadObservacion;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RechazarCuadreUseCase {
    private final CuadreRepository cuadreRepository;
    private final ObservacionRepository observacionRepository;
    private final Clock clock;

    public RechazarCuadreUseCase(CuadreRepository cuadreRepository, ObservacionRepository observacionRepository, Clock clock) {
        this.cuadreRepository = cuadreRepository;
        this.observacionRepository = observacionRepository;
        this.clock = clock;
    }

    @Transactional
    public Cuadre handle(RechazarCuadreCommand command) {
        Cuadre cuadre = cuadreRepository
                .findById(command.getCuadreId())
                .orElseThrow(() -> new IllegalArgumentException("Cuadre no encontrado"));

        Observacion observacion = new Observacion(
                UUID.randomUUID(),
                cuadre.getId(),
                command.getUsuarioId(),
                SeveridadObservacion.ERROR,
                "Rechazado: " + command.getMotivo(),
                Instant.now(clock)
        );
        observacionRepository.save(observacion);

        Cuadre actualizado = cuadre.changeEstado(EstadoCuadre.RECHAZADO, command.getUsuarioId());
        return cuadreRepository.save(actualizado);
    }
}
