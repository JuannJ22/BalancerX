package com.balancerx.controller;

import com.balancerx.model.entity.Cuadre;
import com.balancerx.model.service.CuadreService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de cuadres.
 * Actúa como puente entre el modelo y el viewController.
 */
public class CuadreController {
    
    private final CuadreService cuadreService;
    
    /**
     * Constructor que inyecta el servicio de cuadres.
     * @param cuadreService Servicio de cuadres
     */
    public CuadreController(CuadreService cuadreService) {
        this.cuadreService = cuadreService;
    }
    
    /**
     * Crea un nuevo cuadre en el sistema.
     * @param fecha Fecha del cuadre
     * @param puntoVentaId ID del punto de venta
     * @param usuarioId ID del usuario que crea el cuadre
     * @return El cuadre creado
     */
    public Cuadre crearCuadre(LocalDate fecha, Long puntoVentaId, Long usuarioId) {
        return cuadreService.crearCuadre(fecha, puntoVentaId, usuarioId);
    }
    
    /**
     * Actualiza los totales de un cuadre.
     * @param cuadreId ID del cuadre
     * @param totalTirilla Total de la tirilla
     * @param totalBancos Total de bancos
     * @param totalContable Total contable
     * @return El cuadre actualizado
     */
    public Optional<Cuadre> actualizarTotales(Long cuadreId, BigDecimal totalTirilla, 
                                             BigDecimal totalBancos, BigDecimal totalContable) {
        return cuadreService.actualizarTotales(cuadreId, totalTirilla, totalBancos, totalContable);
    }
    
    /**
     * Actualiza el estado de un cuadre.
     * @param cuadreId ID del cuadre
     * @param nuevoEstado Nuevo estado del cuadre
     * @param usuarioId ID del usuario que actualiza el estado
     * @return El cuadre actualizado
     */
    public Optional<Cuadre> actualizarEstado(Long cuadreId, Cuadre.EstadoCuadre nuevoEstado, Long usuarioId) {
        return cuadreService.actualizarEstado(cuadreId, nuevoEstado, usuarioId);
    }
    
    /**
     * Obtiene un cuadre por su ID.
     * @param cuadreId ID del cuadre
     * @return Optional con el cuadre si existe
     */
    public Optional<Cuadre> obtenerCuadrePorId(Long cuadreId) {
        return cuadreService.obtenerCuadrePorId(cuadreId);
    }
    
    /**
     * Obtiene todos los cuadres de un punto de venta en una fecha específica.
     * @param puntoVentaId ID del punto de venta
     * @param fecha Fecha de los cuadres
     * @return Lista de cuadres
     */
    public List<Cuadre> obtenerCuadresPorPuntoVentaYFecha(Long puntoVentaId, LocalDate fecha) {
        return cuadreService.obtenerCuadresPorPuntoVentaYFecha(puntoVentaId, fecha);
    }
    
    /**
     * Registra el PDF de un cuadre.
     * @param cuadreId ID del cuadre
     * @param pdfPath Ruta del archivo PDF
     * @param checksum Checksum del archivo PDF
     * @return El cuadre actualizado
     */
    public Optional<Cuadre> registrarPdf(Long cuadreId, String pdfPath, String checksum) {
        return cuadreService.registrarPdf(cuadreId, pdfPath, checksum);
    }
    
    /**
     * Firma un cuadre por parte de un usuario con un rol específico.
     * @param cuadreId ID del cuadre
     * @param usuarioId ID del usuario que firma
     * @param rol Rol del usuario que firma
     * @return true si la firma fue exitosa, false en caso contrario
     */
    public boolean firmarCuadre(Long cuadreId, Long usuarioId, String rol) {
        return cuadreService.firmarCuadre(cuadreId, usuarioId, rol);
    }
}