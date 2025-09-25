package com.balancerx.application.usecase;

import com.balancerx.application.command.AsignarMovimientoCommand;
import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.model.MovimientoBancario;
import com.balancerx.domain.repository.CuadreRepository;
import com.balancerx.domain.repository.MovimientoBancarioRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AsignarMovimientoUseCase {
    private final MovimientoBancarioRepository movimientoBancarioRepository;
    private final CuadreRepository cuadreRepository;

    @Transactional
    public MovimientoBancario handle(AsignarMovimientoCommand command) {
        Cuadre cuadre = cuadreRepository
                .findById(command.getCuadreId())
                .orElseThrow(() -> new IllegalArgumentException("Cuadre no encontrado"));
        MovimientoBancario movimiento = movimientoBancarioRepository
                .findById(command.getMovimientoId())
                .orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado"));

        MovimientoBancario actualizado = movimiento.toBuilder()
                .cuadreId(cuadre.getId())
                .asignadoPor(command.getUsuarioId())
                .build();
        return movimientoBancarioRepository.save(actualizado);
    }
}
