package com.balancerx.model.service.impl;

import com.balancerx.model.entity.PuntoVenta;
import com.balancerx.model.service.PuntoVentaService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación del servicio de puntos de venta.
 * Esta implementación utiliza datos en memoria para la interfaz JavaFX.
 */
public class PuntoVentaServiceImpl implements PuntoVentaService {
    
    private final List<PuntoVenta> puntosVenta;
    private final AtomicLong idGenerator;
    
    public PuntoVentaServiceImpl() {
        this.puntosVenta = new ArrayList<>();
        this.idGenerator = new AtomicLong(1);
        
        // Inicializar con datos de ejemplo
        inicializarDatos();
    }
    
    private void inicializarDatos() {
        puntosVenta.add(new PuntoVenta(idGenerator.getAndIncrement(), "PV-001", "Punto de Venta Principal",
                "Calle 10 #20-30", "555-0101", "principal@balancerx.com", true, LocalDateTime.now()));
        puntosVenta.add(new PuntoVenta(idGenerator.getAndIncrement(), "PV-002", "Punto de Venta Norte",
                "Carrera 45 #12-45", "555-0102", "norte@balancerx.com", true, LocalDateTime.now()));
        puntosVenta.add(new PuntoVenta(idGenerator.getAndIncrement(), "PV-003", "Punto de Venta Temporal",
                "Av. Siempre Viva 123", "555-0103", "temporal@balancerx.com", false, LocalDateTime.now()));
    }

    @Override
    public PuntoVenta guardar(PuntoVenta puntoVenta) {
        if (puntoVenta.getId() == null) {
            // Nuevo punto de venta
            PuntoVenta nuevo = new PuntoVenta(
                idGenerator.getAndIncrement(),
                puntoVenta.getCodigo(),
                puntoVenta.getNombre(),
                puntoVenta.getDireccion(),
                puntoVenta.getTelefono(),
                puntoVenta.getEmail(),
                puntoVenta.isActivo(),
                LocalDateTime.now()
            );
            puntosVenta.add(nuevo);
            return nuevo;
        } else {
            // Actualizar existente
            return actualizar(puntoVenta.getId(), puntoVenta);
        }
    }
    
    @Override
    public Optional<PuntoVenta> buscarPorId(Long id) {
        return puntosVenta.stream()
                .filter(pv -> pv.getId().equals(id))
                .findFirst();
    }
    
    @Override
    public List<PuntoVenta> obtenerTodos() {
        return new ArrayList<>(puntosVenta);
    }
    
    @Override
    public List<PuntoVenta> obtenerActivos() {
        return puntosVenta.stream()
                .filter(PuntoVenta::isActivo)
                .toList();
    }
    
    @Override
    public PuntoVenta actualizar(Long id, PuntoVenta puntoVentaActualizado) {
        Optional<PuntoVenta> existente = buscarPorId(id);
        if (existente.isPresent()) {
            PuntoVenta pv = existente.get();
            pv.setCodigo(puntoVentaActualizado.getCodigo());
            pv.setNombre(puntoVentaActualizado.getNombre());
            pv.setDireccion(puntoVentaActualizado.getDireccion());
            pv.setTelefono(puntoVentaActualizado.getTelefono());
            pv.setEmail(puntoVentaActualizado.getEmail());
            pv.setActivo(puntoVentaActualizado.isActivo());
            return pv;
        }
        throw new IllegalArgumentException("Punto de venta no encontrado con ID: " + id);
    }
    
    @Override
    public void desactivar(Long id) {
        Optional<PuntoVenta> existente = buscarPorId(id);
        if (existente.isPresent()) {
            existente.get().setActivo(false);
        } else {
            throw new IllegalArgumentException("Punto de venta no encontrado con ID: " + id);
        }
    }
    
    @Override
    public boolean existePorNombre(String nombre) {
        return puntosVenta.stream()
                .anyMatch(pv -> pv.getNombre().equalsIgnoreCase(nombre));
    }
}