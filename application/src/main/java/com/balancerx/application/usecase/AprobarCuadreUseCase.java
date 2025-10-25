package com.balancerx.application.usecase;

import com.balancerx.application.command.AprobarCuadreCommand;
import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.repository.CuadreRepository;
import com.balancerx.domain.valueobject.EstadoCuadre;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AprobarCuadreUseCase {
    private final CuadreRepository cuadreRepository;
    private final Clock clock;

    public AprobarCuadreUseCase(CuadreRepository cuadreRepository, Clock clock) {
        this.cuadreRepository = cuadreRepository;
        this.clock = clock;
    }

    @Transactional
    public Cuadre handle(AprobarCuadreCommand command) {
        Cuadre cuadre = cuadreRepository
                .findById(command.getCuadreId())
                .orElseThrow(() -> new IllegalArgumentException("Cuadre no encontrado"));

        LocalDate fecha = cuadre.getFecha();
        if (cuadreRepository.existsAprobadoByFechaAndPuntoVenta(fecha, cuadre.getPuntoVentaId())) {
            throw new IllegalStateException("Ya existe un cuadre aprobado para la fecha y punto de venta");
        }

        Cuadre actualizado = cuadre.changeEstado(EstadoCuadre.APROBADO, command.getUsuarioId());
        return cuadreRepository.save(actualizado);
    }
}
