package com.balancerx.application.usecase;

import com.balancerx.application.command.EnviarCuadreCommand;
import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.model.Observacion;
import com.balancerx.domain.repository.ArchivoRepository;
import com.balancerx.domain.repository.CuadreRepository;
import com.balancerx.domain.repository.ObservacionRepository;
import com.balancerx.domain.valueobject.EstadoCuadre;
import com.balancerx.domain.valueobject.SeveridadObservacion;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnviarCuadreUseCase {
    private final CuadreRepository cuadreRepository;
    private final ArchivoRepository archivoRepository;
    private final ObservacionRepository observacionRepository;
    private final Clock clock;

    @Transactional
    public Cuadre handle(EnviarCuadreCommand command) {
        Cuadre cuadre = cuadreRepository
                .findById(command.getCuadreId())
                .orElseThrow(() -> new IllegalArgumentException("Cuadre no encontrado"));
        archivoRepository
                .findByCuadreId(cuadre.getId())
                .orElseThrow(() -> new IllegalStateException("Debe cargarse un PDF antes de enviar"));
        List<Observacion> observaciones = observacionRepository.findByCuadreId(cuadre.getId());
        boolean tieneErrores = observaciones.stream()
                .anyMatch(obs -> obs.getSeveridad() == SeveridadObservacion.ERROR);
        if (tieneErrores) {
            throw new IllegalStateException("Existen observaciones con severidad ERROR");
        }

        if (command.isFueraDeCalendario()) {
            Observacion observacion = Observacion.builder()
                    .id(UUID.randomUUID())
                    .cuadreId(cuadre.getId())
                    .autorId(command.getUsuarioId())
                    .severidad(SeveridadObservacion.WARNING)
                    .texto("Fuera de calendario: " + command.getJustificacion())
                    .createdAt(Instant.now(clock))
                    .build();
            observacionRepository.save(observacion);
        }

        Cuadre actualizado = cuadre.changeEstado(EstadoCuadre.ENVIADO, command.getUsuarioId());
        return cuadreRepository.save(actualizado);
    }
}
