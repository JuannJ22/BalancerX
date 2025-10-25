package com.balancerx.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa un cuadre de caja en el sistema.
 */
public class Cuadre {
    private Long id;
    private LocalDate fecha;
    private Long puntoVentaId;
    private EstadoCuadre estado;
    private BigDecimal totalTirilla;
    private BigDecimal totalBancos;
    private BigDecimal totalContable;
    private String pdfPath;
    private String checksumPdf;
    private Long creadoPor;
    private Long actualizadoPor;
    private boolean firmadoElabora;
    private boolean firmadoAutoriza;
    private boolean firmadoAudita;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    /**
     * Enumeración para los posibles estados de un cuadre.
     */
    public enum EstadoCuadre {
        BORRADOR, ENVIADO, OBSERVADO, APROBADO, RECHAZADO, PENDIENTE
    }

    // Constructores
    public Cuadre() {
    }

    public Cuadre(Long id, LocalDate fecha, Long puntoVentaId, EstadoCuadre estado, 
                  BigDecimal totalTirilla, BigDecimal totalBancos, BigDecimal totalContable, 
                  String pdfPath, String checksumPdf, Long creadoPor, Long actualizadoPor, 
                  boolean firmadoElabora, boolean firmadoAutoriza, boolean firmadoAudita, 
                  LocalDateTime createdAt, LocalDateTime updatedAt, Long version) {
        this.id = id;
        this.fecha = fecha;
        this.puntoVentaId = puntoVentaId;
        this.estado = estado;
        this.totalTirilla = totalTirilla;
        this.totalBancos = totalBancos;
        this.totalContable = totalContable;
        this.pdfPath = pdfPath;
        this.checksumPdf = checksumPdf;
        this.creadoPor = creadoPor;
        this.actualizadoPor = actualizadoPor;
        this.firmadoElabora = firmadoElabora;
        this.firmadoAutoriza = firmadoAutoriza;
        this.firmadoAudita = firmadoAudita;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Long getPuntoVentaId() {
        return puntoVentaId;
    }

    public void setPuntoVentaId(Long puntoVentaId) {
        this.puntoVentaId = puntoVentaId;
    }

    public EstadoCuadre getEstado() {
        return estado;
    }

    public void setEstado(EstadoCuadre estado) {
        this.estado = estado;
    }

    public BigDecimal getTotalTirilla() {
        return totalTirilla;
    }

    public void setTotalTirilla(BigDecimal totalTirilla) {
        this.totalTirilla = totalTirilla;
    }

    public BigDecimal getTotalBancos() {
        return totalBancos;
    }

    public void setTotalBancos(BigDecimal totalBancos) {
        this.totalBancos = totalBancos;
    }

    public BigDecimal getTotalContable() {
        return totalContable;
    }

    public void setTotalContable(BigDecimal totalContable) {
        this.totalContable = totalContable;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String getChecksumPdf() {
        return checksumPdf;
    }

    public void setChecksumPdf(String checksumPdf) {
        this.checksumPdf = checksumPdf;
    }

    public Long getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Long creadoPor) {
        this.creadoPor = creadoPor;
    }

    public Long getActualizadoPor() {
        return actualizadoPor;
    }

    public void setActualizadoPor(Long actualizadoPor) {
        this.actualizadoPor = actualizadoPor;
    }

    public boolean isFirmadoElabora() {
        return firmadoElabora;
    }

    public void setFirmadoElabora(boolean firmadoElabora) {
        this.firmadoElabora = firmadoElabora;
    }

    public boolean isFirmadoAutoriza() {
        return firmadoAutoriza;
    }

    public void setFirmadoAutoriza(boolean firmadoAutoriza) {
        this.firmadoAutoriza = firmadoAutoriza;
    }

    public boolean isFirmadoAudita() {
        return firmadoAudita;
    }

    public void setFirmadoAudita(boolean firmadoAudita) {
        this.firmadoAudita = firmadoAudita;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}