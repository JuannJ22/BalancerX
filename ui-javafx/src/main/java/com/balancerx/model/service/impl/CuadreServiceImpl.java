package com.balancerx.model.service.impl;

import com.balancerx.model.entity.Cuadre;
import com.balancerx.model.service.CuadreService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación simple del servicio de cuadres para la interfaz JavaFX.
 * Esta implementación utiliza datos simulados para propósitos de demostración.
 */
public class CuadreServiceImpl implements CuadreService {

    private final List<Cuadre> cuadres = new ArrayList<>();
    private Long nextId = 1L;

    public CuadreServiceImpl() {
        inicializarDatos();
    }

    private void inicializarDatos() {
        Cuadre cuadreHoy = new Cuadre();
        cuadreHoy.setId(nextId++);
        cuadreHoy.setFecha(LocalDate.now());
        cuadreHoy.setPuntoVentaId(1L);
        cuadreHoy.setEstado(Cuadre.EstadoCuadre.BORRADOR);
        cuadreHoy.setTotalTirilla(new BigDecimal("1250.50"));
        cuadreHoy.setTotalBancos(new BigDecimal("1230.40"));
        cuadreHoy.setTotalContable(new BigDecimal("1240.00"));
        cuadreHoy.setCreadoPor(1L);
        cuadreHoy.setActualizadoPor(1L);
        cuadreHoy.setCreatedAt(LocalDateTime.now());
        cuadreHoy.setUpdatedAt(LocalDateTime.now());
        cuadres.add(cuadreHoy);

        Cuadre cuadreAyer = new Cuadre();
        cuadreAyer.setId(nextId++);
        cuadreAyer.setFecha(LocalDate.now().minusDays(1));
        cuadreAyer.setPuntoVentaId(2L);
        cuadreAyer.setEstado(Cuadre.EstadoCuadre.APROBADO);
        cuadreAyer.setTotalTirilla(new BigDecimal("980.00"));
        cuadreAyer.setTotalBancos(new BigDecimal("980.00"));
        cuadreAyer.setTotalContable(new BigDecimal("980.00"));
        cuadreAyer.setCreadoPor(2L);
        cuadreAyer.setActualizadoPor(2L);
        cuadreAyer.setCreatedAt(LocalDateTime.now().minusDays(1));
        cuadreAyer.setUpdatedAt(LocalDateTime.now().minusHours(12));
        cuadres.add(cuadreAyer);
    }
    
    @Override
    public Cuadre crearCuadre(LocalDate fecha, Long puntoVentaId, Long usuarioId) {
        Cuadre cuadre = new Cuadre();
        cuadre.setId(nextId++);
        cuadre.setFecha(fecha);
        cuadre.setPuntoVentaId(puntoVentaId);
        cuadre.setEstado(Cuadre.EstadoCuadre.BORRADOR);
        cuadre.setCreadoPor(usuarioId);
        cuadre.setActualizadoPor(usuarioId);
        cuadre.setCreatedAt(LocalDateTime.now());
        cuadre.setUpdatedAt(LocalDateTime.now());
        cuadre.setVersion(0L);
        
        cuadres.add(cuadre);
        return cuadre;
    }
    
    @Override
    public Optional<Cuadre> actualizarTotales(Long cuadreId, BigDecimal totalTirilla, 
                                            BigDecimal totalBancos, BigDecimal totalContable) {
        Optional<Cuadre> cuadreOpt = obtenerCuadrePorId(cuadreId);
        if (cuadreOpt.isPresent()) {
            Cuadre cuadre = cuadreOpt.get();
            cuadre.setTotalTirilla(totalTirilla);
            cuadre.setTotalBancos(totalBancos);
            cuadre.setTotalContable(totalContable);
            cuadre.setUpdatedAt(LocalDateTime.now());
            return Optional.of(cuadre);
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<Cuadre> actualizarEstado(Long cuadreId, Cuadre.EstadoCuadre nuevoEstado, Long usuarioId) {
        Optional<Cuadre> cuadreOpt = obtenerCuadrePorId(cuadreId);
        if (cuadreOpt.isPresent()) {
            Cuadre cuadre = cuadreOpt.get();
            cuadre.setEstado(nuevoEstado);
            cuadre.setActualizadoPor(usuarioId);
            cuadre.setUpdatedAt(LocalDateTime.now());
            return Optional.of(cuadre);
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<Cuadre> obtenerCuadrePorId(Long cuadreId) {
        return cuadres.stream()
                .filter(c -> c.getId().equals(cuadreId))
                .findFirst();
    }
    
    @Override
    public List<Cuadre> obtenerCuadresPorPuntoVentaYFecha(Long puntoVentaId, LocalDate fecha) {
        return cuadres.stream()
                .filter(c -> c.getPuntoVentaId().equals(puntoVentaId) && c.getFecha().equals(fecha))
                .toList();
    }
    
    @Override
    public Optional<Cuadre> registrarPdf(Long cuadreId, String pdfPath, String checksum) {
        Optional<Cuadre> cuadreOpt = obtenerCuadrePorId(cuadreId);
        if (cuadreOpt.isPresent()) {
            Cuadre cuadre = cuadreOpt.get();
            cuadre.setPdfPath(pdfPath);
            cuadre.setChecksumPdf(checksum);
            cuadre.setUpdatedAt(LocalDateTime.now());
            return Optional.of(cuadre);
        }
        return Optional.empty();
    }
    
    @Override
    public boolean firmarCuadre(Long cuadreId, Long usuarioId, String rol) {
        Optional<Cuadre> cuadreOpt = obtenerCuadrePorId(cuadreId);
        if (cuadreOpt.isPresent()) {
            Cuadre cuadre = cuadreOpt.get();
            switch (rol.toUpperCase()) {
                case "ELABORADOR":
                    cuadre.setFirmadoElabora(true);
                    break;
                case "AUTORIZADOR":
                    cuadre.setFirmadoAutoriza(true);
                    break;
                case "AUDITOR":
                    cuadre.setFirmadoAudita(true);
                    break;
                default:
                    return false;
            }
            cuadre.setActualizadoPor(usuarioId);
            cuadre.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }
    
    /**
     * Obtiene todos los cuadres (método auxiliar para la interfaz).
     * @return Lista de todos los cuadres
     */
    public List<Cuadre> obtenerTodosLosCuadres() {
        return new ArrayList<>(cuadres);
    }

    @Override
    public List<Cuadre> obtenerTodos() {
        return new ArrayList<>(cuadres);
    }
}