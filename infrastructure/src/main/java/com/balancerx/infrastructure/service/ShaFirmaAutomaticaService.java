package com.balancerx.infrastructure.service;

import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.model.Firma;
import com.balancerx.domain.model.Usuario;
import com.balancerx.domain.service.ChecksumService;
import com.balancerx.domain.service.FirmaAutomaticaService;
import com.balancerx.domain.valueobject.MetodoFirma;
import com.balancerx.domain.valueobject.RolUsuario;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShaFirmaAutomaticaService implements FirmaAutomaticaService {
    private final ChecksumService checksumService;

    @Override
    public Firma firmar(Cuadre cuadre, Usuario usuario, RolUsuario rol) {
        String payload = String.format(
                "%s|%s|%s|%s",
                usuario.getId(), rol.name(), Instant.now(), cuadre.getChecksumPdf().orElse(""));
        String hash = checksumService.sha256(payload.getBytes(StandardCharsets.UTF_8));
        return Firma.builder()
                .id(UUID.randomUUID())
                .cuadreId(cuadre.getId())
                .rol(rol)
                .firmanteId(usuario.getId())
                .metodo(MetodoFirma.AUTO)
                .hash(hash)
                .timestamp(Instant.now())
                .build();
    }
}
