package com.balancerx.api.controller;

import com.balancerx.domain.model.PuntoVenta;
import com.balancerx.domain.repository.PuntoVentaRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/puntos-venta")
@RequiredArgsConstructor
public class PuntoVentaController {
    private final PuntoVentaRepository puntoVentaRepository;

    @GetMapping
    public List<PuntoVenta> listar() {
        return puntoVentaRepository.findAll();
    }

    @PostMapping
    public PuntoVenta crear(@RequestBody PuntoVenta request) {
        PuntoVenta nuevo = request.toBuilder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .build();
        return puntoVentaRepository.save(nuevo);
    }

    @PutMapping("/{id}")
    public PuntoVenta actualizar(@PathVariable UUID id, @RequestBody PuntoVenta request) {
        PuntoVenta existente = puntoVentaRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Punto de venta no encontrado"));
        PuntoVenta actualizado = existente.toBuilder()
                .nombre(request.getNombre())
                .activo(request.isActivo())
                .build();
        return puntoVentaRepository.save(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        puntoVentaRepository.findById(id).ifPresent(pv -> puntoVentaRepository.save(pv.toBuilder().activo(false).build()));
        return ResponseEntity.noContent().build();
    }
}
