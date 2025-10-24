package com.balancerx.controller;

import com.balancerx.model.entity.PuntoVenta;
import com.balancerx.model.service.PuntoVentaService;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de puntos de venta.
 * Actúa como puente entre el modelo y el viewController.
 */
public class PuntoVentaController {
    
    private final PuntoVentaService puntoVentaService;
    
    /**
     * Constructor que inyecta el servicio de puntos de venta.
     * @param puntoVentaService Servicio de puntos de venta
     */
    public PuntoVentaController(PuntoVentaService puntoVentaService) {
        this.puntoVentaService = puntoVentaService;
    }
    
    /**
     * Obtiene todos los puntos de venta.
     * @return Lista de todos los puntos de venta
     */
    public List<PuntoVenta> obtenerTodos() {
        return puntoVentaService.obtenerTodos();
    }
    
    /**
     * Obtiene todos los puntos de venta activos.
     * @return Lista de puntos de venta activos
     */
    public List<PuntoVenta> obtenerActivos() {
        return puntoVentaService.obtenerActivos();
    }
    
    /**
     * Busca un punto de venta por su ID.
     * @param id El ID del punto de venta
     * @return Optional con el punto de venta si existe
     */
    public Optional<PuntoVenta> buscarPorId(Long id) {
        return puntoVentaService.buscarPorId(id);
    }
    
    /**
     * Guarda un nuevo punto de venta.
     * @param nombre Nombre del punto de venta
     * @param activo Estado activo del punto de venta
     * @return El punto de venta guardado
     */
    public PuntoVenta guardar(String nombre, boolean activo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del punto de venta es obligatorio");
        }
        
        if (puntoVentaService.existePorNombre(nombre.trim())) {
            throw new IllegalArgumentException("Ya existe un punto de venta con ese nombre");
        }
        
        PuntoVenta nuevoPuntoVenta = new PuntoVenta();
        nuevoPuntoVenta.setNombre(nombre.trim());
        nuevoPuntoVenta.setActivo(activo);
        
        return puntoVentaService.guardar(nuevoPuntoVenta);
    }
    
    /**
     * Actualiza un punto de venta existente.
     * @param id ID del punto de venta a actualizar
     * @param nombre Nuevo nombre del punto de venta
     * @param activo Nuevo estado activo del punto de venta
     * @return El punto de venta actualizado
     */
    public PuntoVenta actualizar(Long id, String nombre, boolean activo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del punto de venta es obligatorio");
        }
        
        // Verificar que no existe otro punto de venta con el mismo nombre
        Optional<PuntoVenta> existente = puntoVentaService.buscarPorId(id);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Punto de venta no encontrado");
        }
        
        // Solo verificar duplicados si el nombre cambió
        if (!existente.get().getNombre().equalsIgnoreCase(nombre.trim()) && 
            puntoVentaService.existePorNombre(nombre.trim())) {
            throw new IllegalArgumentException("Ya existe un punto de venta con ese nombre");
        }
        
        PuntoVenta puntoVentaActualizado = new PuntoVenta();
        puntoVentaActualizado.setNombre(nombre.trim());
        puntoVentaActualizado.setActivo(activo);
        
        return puntoVentaService.actualizar(id, puntoVentaActualizado);
    }
    
    /**
     * Desactiva un punto de venta.
     * @param id ID del punto de venta a desactivar
     */
    public void desactivar(Long id) {
        puntoVentaService.desactivar(id);
    }
    
    /**
     * Verifica si existe un punto de venta con el nombre dado.
     * @param nombre El nombre a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existePorNombre(String nombre) {
        return puntoVentaService.existePorNombre(nombre);
    }
}