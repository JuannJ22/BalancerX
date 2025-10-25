package com.balancerx.application.usecase;

import com.balancerx.application.command.CreateCuadreCommand;
import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.repository.CuadreRepository;
import com.balancerx.domain.repository.PuntoVentaRepository;
import com.balancerx.domain.valueobject.EstadoCuadre;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCuadreUseCase {
    private final CuadreRepository cuadreRepository;
    private final PuntoVentaRepository puntoVentaRepository;
    private final Clock clock;

    public CreateCuadreUseCase(CuadreRepository cuadreRepository, PuntoVentaRepository puntoVentaRepository, Clock clock) {
        this.cuadreRepository = cuadreRepository;
        this.puntoVentaRepository = puntoVentaRepository;
        this.clock = clock;
    }

    @Transactional
    public Cuadre handle(CreateCuadreCommand command) {
        Objects.requireNonNull(command, "command");
        puntoVentaRepository
                .findById(command.getPuntoVentaId())
                .orElseThrow(() -> new IllegalArgumentException("Punto de venta inexistente"));

        if (cuadreRepository.existsAprobadoByFechaAndPuntoVenta(command.getFecha(), command.getPuntoVentaId())) {
            throw new IllegalStateException("Ya existe un cuadre aprobado para la fecha y punto de venta");
        }

        Instant now = Instant.now(clock);
        Cuadre nuevo = new Cuadre(
                UUID.randomUUID(),
                command.getFecha(),
                command.getPuntoVentaId(),
                EstadoCuadre.BORRADOR,
                command.getTotalTirilla(),
                command.getTotalBancos(),
                command.getTotalContable(),
                null, // pdfPath
                null, // checksumPdf
                command.getCreadoPor(),
                command.getCreadoPor(),
                false, // firmadoElabora
                false, // firmadoAutoriza
                false, // firmadoAudita
                now,
                now,
                0L
        );
        return cuadreRepository.save(nuevo);
    }
}
