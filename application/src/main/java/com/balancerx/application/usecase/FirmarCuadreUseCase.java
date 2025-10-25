package com.balancerx.application.usecase;

import com.balancerx.application.command.FirmarCuadreCommand;
import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.model.Firma;
import com.balancerx.domain.model.Usuario;
import com.balancerx.domain.repository.CuadreRepository;
import com.balancerx.domain.repository.FirmaRepository;
import com.balancerx.domain.repository.UsuarioRepository;
import com.balancerx.domain.service.FirmaAutomaticaService;
import com.balancerx.domain.valueobject.RolUsuario;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FirmarCuadreUseCase {
    private final CuadreRepository cuadreRepository;
    private final FirmaRepository firmaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FirmaAutomaticaService firmaAutomaticaService;
    private final Clock clock;

    public FirmarCuadreUseCase(CuadreRepository cuadreRepository, FirmaRepository firmaRepository, 
                               UsuarioRepository usuarioRepository, FirmaAutomaticaService firmaAutomaticaService, 
                               Clock clock) {
        this.cuadreRepository = cuadreRepository;
        this.firmaRepository = firmaRepository;
        this.usuarioRepository = usuarioRepository;
        this.firmaAutomaticaService = firmaAutomaticaService;
        this.clock = clock;
    }

    @Transactional
    public Firma handle(FirmarCuadreCommand command) {
        Cuadre cuadre = cuadreRepository
                .findById(command.getCuadreId())
                .orElseThrow(() -> new IllegalArgumentException("Cuadre no encontrado"));
        Usuario usuario = usuarioRepository
                .findById(command.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Firma firma = firmaAutomaticaService.firmar(cuadre, usuario, command.getRolFirma());
        return firmaRepository.save(firma);
    }
}
