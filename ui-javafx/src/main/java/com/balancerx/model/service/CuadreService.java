package com.balancerx.model.service;

import com.balancerx.model.entity.Cuadre;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el servicio de cuadres siguiendo el patrón Service.
 */
public interface CuadreService {
    
    /**
     * Crea un nuevo cuadre en el sistema.
     * @param fecha Fecha del cuadre
     * @param puntoVentaId ID del punto de venta
     * @param usuarioId ID del usuario que crea el cuadre
     * @return El cuadre creado
     */
    Cuadre crearCuadre(LocalDate fecha, Long puntoVentaId, Long usuarioId);
    
    /**
     * Actualiza los totales de un cuadre.
     * @param cuadreId ID del cuadre
     * @param totalTirilla Total de la tirilla
     * @param totalBancos Total de bancos
     * @param totalContable Total contable
     * @return Optional con el cuadre actualizado
     */
    Optional<Cuadre> actualizarTotales(Long cuadreId, BigDecimal totalTirilla, 
                                      BigDecimal totalBancos, BigDecimal totalContable);
    
    /**
     * Actualiza el estado de un cuadre.
     * @param cuadreId ID del cuadre
     * @param nuevoEstado Nuevo estado del cuadre
     * @param usuarioId ID del usuario que actualiza el estado
     * @return Optional con el cuadre actualizado
     */
    Optional<Cuadre> actualizarEstado(Long cuadreId, Cuadre.EstadoCuadre nuevoEstado, Long usuarioId);
    
    /**
     * Obtiene un cuadre por su ID.
     * @param cuadreId ID del cuadre
     * @return Optional con el cuadre si existe
     */
    Optional<Cuadre> obtenerCuadrePorId(Long cuadreId);
    
    /**
     * Obtiene todos los cuadres de un punto de venta en una fecha específica.
     * @param puntoVentaId ID del punto de venta
     * @param fecha Fecha de los cuadres
     * @return Lista de cuadres
     */
    List<Cuadre> obtenerCuadresPorPuntoVentaYFecha(Long puntoVentaId, LocalDate fecha);
    
    /**
     * Registra el PDF de un cuadre.
     * @param cuadreId ID del cuadre
     * @param pdfPath Ruta del archivo PDF
     * @param checksum Checksum del archivo PDF
     * @return Optional con el cuadre actualizado
     */
    Optional<Cuadre> registrarPdf(Long cuadreId, String pdfPath, String checksum);
    
    /**
     * Firma un cuadre por parte de un usuario con un rol específico.
     * @param cuadreId ID del cuadre
     * @param usuarioId ID del usuario que firma
     * @param rol Rol del usuario que firma
     * @return true si la firma fue exitosa, false en caso contrario
     */
    boolean firmarCuadre(Long cuadreId, Long usuarioId, String rol);

    /**
     * Obtiene todos los cuadres registrados en memoria.
     * @return Lista de cuadres
     */
    List<Cuadre> obtenerTodos();
}