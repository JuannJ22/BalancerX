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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCuadreUseCase {
    private final CuadreRepository cuadreRepository;
    private final PuntoVentaRepository puntoVentaRepository;
    private final Clock clock;

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
        Cuadre nuevo = Cuadre.builder()
                .id(UUID.randomUUID())
                .fecha(command.getFecha())
                .puntoVentaId(command.getPuntoVentaId())
                .estado(EstadoCuadre.BORRADOR)
                .totalTirilla(command.getTotalTirilla())
                .totalBancos(command.getTotalBancos())
                .totalContable(command.getTotalContable())
                .creadoPor(command.getCreadoPor())
                .actualizadoPor(command.getCreadoPor())
                .createdAt(now)
                .updatedAt(now)
                .version(0L)
                .build();
        return cuadreRepository.save(nuevo);
    }
}
