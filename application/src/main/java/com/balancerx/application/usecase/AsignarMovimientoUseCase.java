package com.balancerx.application.usecase;

import com.balancerx.application.command.AsignarMovimientoCommand;
import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.model.MovimientoBancario;
import com.balancerx.domain.repository.CuadreRepository;
import com.balancerx.domain.repository.MovimientoBancarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsignarMovimientoUseCase {
    private final CuadreRepository cuadreRepository;
    private final MovimientoBancarioRepository movimientoBancarioRepository;

    public AsignarMovimientoUseCase(CuadreRepository cuadreRepository, MovimientoBancarioRepository movimientoBancarioRepository) {
        this.cuadreRepository = cuadreRepository;
        this.movimientoBancarioRepository = movimientoBancarioRepository;
    }

    @Transactional
    public MovimientoBancario handle(AsignarMovimientoCommand command) {
        Cuadre cuadre = cuadreRepository
                .findById(command.getCuadreId())
                .orElseThrow(() -> new IllegalArgumentException("Cuadre no encontrado"));
        MovimientoBancario movimiento = movimientoBancarioRepository
                .findById(command.getMovimientoId())
                .orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado"));

        MovimientoBancario actualizado = movimiento
                .withCuadreId(cuadre.getId())
                .withAsignadoPor(command.getUsuarioId());
        return movimientoBancarioRepository.save(actualizado);
    }
}
