package com.balancerx.model.service;

import com.balancerx.model.entity.PuntoVenta;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el servicio de puntos de venta.
 */
public interface PuntoVentaService {
    
    /**
     * Guarda un punto de venta.
     * @param puntoVenta El punto de venta a guardar
     * @return El punto de venta guardado
     */
    PuntoVenta guardar(PuntoVenta puntoVenta);
    
    /**
     * Busca un punto de venta por su ID.
     * @param id El ID del punto de venta
     * @return Optional con el punto de venta si existe
     */
    Optional<PuntoVenta> buscarPorId(Long id);
    
    /**
     * Obtiene todos los puntos de venta.
     * @return Lista de todos los puntos de venta
     */
    List<PuntoVenta> obtenerTodos();
    
    /**
     * Obtiene todos los puntos de venta activos.
     * @return Lista de puntos de venta activos
     */
    List<PuntoVenta> obtenerActivos();
    
    /**
     * Actualiza un punto de venta existente.
     * @param id El ID del punto de venta a actualizar
     * @param puntoVenta Los nuevos datos del punto de venta
     * @return El punto de venta actualizado
     */
    PuntoVenta actualizar(Long id, PuntoVenta puntoVenta);
    
    /**
     * Desactiva un punto de venta.
     * @param id El ID del punto de venta a desactivar
     */
    void desactivar(Long id);
    
    /**
     * Verifica si existe un punto de venta con el nombre dado.
     * @param nombre El nombre a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existePorNombre(String nombre);
}