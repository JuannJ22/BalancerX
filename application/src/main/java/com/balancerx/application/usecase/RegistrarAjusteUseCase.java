package com.balancerx.application.usecase;

import com.balancerx.application.command.RegistrarAjusteCommand;
import com.balancerx.domain.model.Ajuste;
import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.repository.AjusteRepository;
import com.balancerx.domain.repository.CuadreRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrarAjusteUseCase {
    private final CuadreRepository cuadreRepository;
    private final AjusteRepository ajusteRepository;
    private final Clock clock;

    @Transactional
    public Ajuste handle(RegistrarAjusteCommand command) {
        Cuadre cuadre = cuadreRepository
                .findById(command.getCuadreId())
                .orElseThrow(() -> new IllegalArgumentException("Cuadre no encontrado"));
        Instant now = Instant.now(clock);
        Ajuste ajuste = Ajuste.builder()
                .id(UUID.randomUUID())
                .cuadreId(cuadre.getId())
                .tipo(command.getTipo())
                .monto(command.getMonto())
                .motivo(command.getMotivo())
                .autorId(command.getUsuarioId())
                .createdAt(now)
                .build();
        return ajusteRepository.save(ajuste);
    }
}
