package com.balancerx.api.controller;

import com.balancerx.api.dto.CreateCuadreRequest;
import com.balancerx.application.command.AprobarCuadreCommand;
import com.balancerx.application.command.CreateCuadreCommand;
import com.balancerx.application.command.EnviarCuadreCommand;
import com.balancerx.application.command.PreConciliarCommand;
import com.balancerx.application.query.GenerarReporteQuery;
import com.balancerx.application.usecase.AprobarCuadreUseCase;
import com.balancerx.application.usecase.CreateCuadreUseCase;
import com.balancerx.application.usecase.EnviarCuadreUseCase;
import com.balancerx.application.usecase.GenerarReporteUseCase;
import com.balancerx.application.usecase.PreConciliarUseCase;
import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.model.Match;
import com.balancerx.domain.repository.CuadreRepository;
import com.balancerx.domain.valueobject.EstadoCuadre;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cuadres")
@RequiredArgsConstructor
public class CuadreController {
    private final CreateCuadreUseCase createCuadreUseCase;
    private final PreConciliarUseCase preConciliarUseCase;
    private final EnviarCuadreUseCase enviarCuadreUseCase;
    private final AprobarCuadreUseCase aprobarCuadreUseCase;
    private final GenerarReporteUseCase generarReporteUseCase;
    private final CuadreRepository cuadreRepository;

    @PostMapping
    public Cuadre crear(@RequestBody CreateCuadreRequest request) {
        CreateCuadreCommand command = new CreateCuadreCommand(
                request.fecha(),
                request.puntoVentaId(),
                request.totalTirilla(),
                request.totalBancos(),
                request.totalContable(),
                UUID.randomUUID()
        );
        return createCuadreUseCase.handle(command);
    }

    @PostMapping("/{id}/preconciliar")
    public List<Match> preconciliar(@PathVariable UUID id) {
        return preConciliarUseCase.handle(new PreConciliarCommand(id, UUID.randomUUID()));
    }

    @PostMapping("/{id}/enviar")
    public Cuadre enviar(@PathVariable UUID id) {
        return enviarCuadreUseCase.handle(
                new EnviarCuadreCommand(id, UUID.randomUUID(), false, null));
    }

    @PostMapping("/{id}/aprobar")
    public Cuadre aprobar(@PathVariable UUID id) {
        return aprobarCuadreUseCase.handle(new AprobarCuadreCommand(id, UUID.randomUUID()));
    }

    @GetMapping
    public List<Cuadre> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fecha,
            @RequestParam(required = false) EstadoCuadre estado,
            @RequestParam(required = false) UUID pvId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return cuadreRepository.findByFiltros(fecha, estado, pvId, page, size);
    }

    @GetMapping("/reportes/{tipo}")
    public Map<String, Object> generarReporte(
            @PathVariable String tipo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate hasta,
            @RequestParam(required = false) UUID pvId) {
        return generarReporteUseCase.handle(
                new GenerarReporteQuery(tipo, desde, hasta, pvId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cuadre> detalle(@PathVariable UUID id) {
        return cuadreRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
