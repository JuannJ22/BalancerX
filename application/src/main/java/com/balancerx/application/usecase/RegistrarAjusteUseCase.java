package com.balancerx.application.usecase;

import com.balancerx.application.command.RegistrarAjusteCommand;
import com.balancerx.domain.model.Ajuste;
import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.repository.AjusteRepository;
import com.balancerx.domain.repository.CuadreRepository;
import com.balancerx.domain.valueobject.TipoAjuste;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarAjusteUseCase {
    private final CuadreRepository cuadreRepository;
    private final AjusteRepository ajusteRepository;
    private final Clock clock;

    public RegistrarAjusteUseCase(CuadreRepository cuadreRepository, AjusteRepository ajusteRepository, Clock clock) {
        this.cuadreRepository = cuadreRepository;
        this.ajusteRepository = ajusteRepository;
        this.clock = clock;
    }

    @Transactional
    public Ajuste handle(RegistrarAjusteCommand command) {
        Cuadre cuadre = cuadreRepository
                .findById(command.getCuadreId())
                .orElseThrow(() -> new IllegalArgumentException("Cuadre no encontrado"));
        Instant now = Instant.now(clock);
        Ajuste ajuste = new Ajuste(
                UUID.randomUUID(),
                cuadre.getId(),
                command.getTipo(),
                command.getMonto(),
                command.getMotivo(),
                command.getUsuarioId(),
                now
        );
        return ajusteRepository.save(ajuste);
    }
}
