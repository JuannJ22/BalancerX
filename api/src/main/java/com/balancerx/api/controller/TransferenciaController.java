package com.balancerx.api.controller;

import com.balancerx.api.dto.AsignarTransferenciaRequest;
import com.balancerx.api.dto.RegistrarTransferenciaRequest;
import com.balancerx.api.dto.TransferenciaHistorialResponse;
import com.balancerx.api.dto.TransferenciaResponse;
import com.balancerx.application.command.AsignarTransferenciaCommand;
import com.balancerx.application.command.RegistrarTransferenciaCommand;
import com.balancerx.application.query.ConsultarTransferenciasQuery;
import com.balancerx.application.usecase.AsignarTransferenciaUseCase;
import com.balancerx.application.usecase.ConsultarHistorialTransferenciaUseCase;
import com.balancerx.application.usecase.ConsultarTransferenciasUseCase;
import com.balancerx.application.usecase.RegistrarTransferenciaUseCase;
import com.balancerx.domain.valueobject.BancoTransferencia;
import com.balancerx.domain.valueobject.EstadoTransferencia;
import com.balancerx.domain.valueobject.TipoAsignacionTransferencia;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transferencias")
public class TransferenciaController {
    private final RegistrarTransferenciaUseCase registrarTransferenciaUseCase;
    private final AsignarTransferenciaUseCase asignarTransferenciaUseCase;
    private final ConsultarTransferenciasUseCase consultarTransferenciasUseCase;
    private final ConsultarHistorialTransferenciaUseCase consultarHistorialTransferenciaUseCase;

    public TransferenciaController(RegistrarTransferenciaUseCase registrarTransferenciaUseCase,
                                   AsignarTransferenciaUseCase asignarTransferenciaUseCase,
                                   ConsultarTransferenciasUseCase consultarTransferenciasUseCase,
                                   ConsultarHistorialTransferenciaUseCase consultarHistorialTransferenciaUseCase) {
        this.registrarTransferenciaUseCase = registrarTransferenciaUseCase;
        this.asignarTransferenciaUseCase = asignarTransferenciaUseCase;
        this.consultarTransferenciasUseCase = consultarTransferenciasUseCase;
        this.consultarHistorialTransferenciaUseCase = consultarHistorialTransferenciaUseCase;
    }

    @PostMapping
    public TransferenciaResponse registrar(@RequestBody RegistrarTransferenciaRequest request) {
        UUID usuarioId = Optional.ofNullable(request.usuarioId()).orElse(UUID.randomUUID());
        byte[] contenido = decodeBase64(request.archivoBase64());
        TipoAsignacionTransferencia tipoAsignacion = parseTipoAsignacion(request.destinoTipo());

        RegistrarTransferenciaCommand command = new RegistrarTransferenciaCommand(
                usuarioId,
                request.archivoNombre(),
                contenido,
                BancoTransferencia.fromString(request.banco()),
                request.fecha(),
                request.valor(),
                request.comentario(),
                tipoAsignacion,
                request.destinoId()
        );

        return TransferenciaResponse.fromDomain(registrarTransferenciaUseCase.handle(command));
    }

    @PostMapping("/{id}/asignacion")
    public TransferenciaResponse asignar(@PathVariable UUID id,
                                         @RequestBody AsignarTransferenciaRequest request) {
        UUID usuarioId = Optional.ofNullable(request.usuarioId()).orElse(UUID.randomUUID());
        TipoAsignacionTransferencia tipo = parseTipoAsignacion(request.destinoTipo());

        AsignarTransferenciaCommand command = new AsignarTransferenciaCommand(
                id,
                tipo,
                request.destinoId(),
                usuarioId,
                Instant.now()
        );

        return TransferenciaResponse.fromDomain(asignarTransferenciaUseCase.handle(command));
    }

    @GetMapping
    public List<TransferenciaResponse> listar(
            @RequestParam(required = false) String banco,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String tipoAsignacion,
            @RequestParam(required = false) UUID destinoId) {

        ConsultarTransferenciasQuery query = new ConsultarTransferenciasQuery(
                banco != null ? BancoTransferencia.fromString(banco) : null,
                fechaDesde,
                fechaHasta,
                parseTipoAsignacion(tipoAsignacion),
                destinoId,
                estado != null ? EstadoTransferencia.valueOf(estado.toUpperCase()) : null
        );

        return consultarTransferenciasUseCase.handle(query).stream()
                .map(TransferenciaResponse::fromDomain)
                .toList();
    }

    @GetMapping("/{id}/historial")
    public List<TransferenciaHistorialResponse> historial(@PathVariable UUID id) {
        return consultarHistorialTransferenciaUseCase.handle(id).stream()
                .map(TransferenciaHistorialResponse::fromDomain)
                .toList();
    }

    private TipoAsignacionTransferencia parseTipoAsignacion(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String normalized = raw.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        return TipoAsignacionTransferencia.valueOf(normalized);
    }

    private byte[] decodeBase64(String base64) {
        if (base64 == null || base64.isBlank()) {
            throw new IllegalArgumentException("El archivo de la transferencia es obligatorio");
        }
        return Base64.getDecoder().decode(base64);
    }
}
