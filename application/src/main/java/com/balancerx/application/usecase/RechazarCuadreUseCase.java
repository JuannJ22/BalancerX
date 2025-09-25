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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RechazarCuadreUseCase {
    private final CuadreRepository cuadreRepository;
    private final ObservacionRepository observacionRepository;
    private final Clock clock;

    @Transactional
    public Cuadre handle(RechazarCuadreCommand command) {
        Cuadre cuadre = cuadreRepository
                .findById(command.getCuadreId())
                .orElseThrow(() -> new IllegalArgumentException("Cuadre no encontrado"));

        Observacion observacion = Observacion.builder()
                .id(UUID.randomUUID())
                .cuadreId(cuadre.getId())
                .autorId(command.getUsuarioId())
                .severidad(SeveridadObservacion.ERROR)
                .texto("Rechazado: " + command.getMotivo())
                .createdAt(Instant.now(clock))
                .build();
        observacionRepository.save(observacion);

        Cuadre actualizado = cuadre.changeEstado(EstadoCuadre.RECHAZADO, command.getUsuarioId());
        return cuadreRepository.save(actualizado);
    }
}
