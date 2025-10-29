package com.balancerx.infrastructure.persistence.jpa;

import com.balancerx.domain.valueobject.EstadoCuadre;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cuadres")
@Getter
@Setter
public class JpaCuadre {
    @Id
    private UUID id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "punto_venta_id", nullable = false)
    private UUID puntoVentaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCuadre estado;

    @Column(name = "total_tirilla", precision = 15, scale = 2)
    private BigDecimal totalTirilla;

    @Column(name = "total_bancos", precision = 15, scale = 2)
    private BigDecimal totalBancos;

    @Column(name = "total_contable", precision = 15, scale = 2)
    private BigDecimal totalContable;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(name = "checksum_pdf")
    private String checksumPdf;

    @Column(name = "creado_por")
    private UUID creadoPor;

    @Column(name = "actualizado_por")
    private UUID actualizadoPor;

    @Column(name = "firmado_elabora")
    private boolean firmadoElabora;

    @Column(name = "firmado_autoriza")
    private boolean firmadoAutoriza;

    @Column(name = "firmado_audita")
    private boolean firmadoAudita;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private long version;

    // Métodos getter manuales para resolver errores de compilación
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }

    public boolean isFirmadoAudita() {
        return firmadoAudita;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UUID getCreadoPor() {
        return creadoPor;
    }

    public UUID getActualizadoPor() {
        return actualizadoPor;
    }

    public boolean isFirmadoElabora() {
        return firmadoElabora;
    }

    public boolean isFirmadoAutoriza() {
        return firmadoAutoriza;
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public UUID getPuntoVentaId() {
        return puntoVentaId;
    }

    public EstadoCuadre getEstado() {
        return estado;
    }

    public BigDecimal getTotalTirilla() {
        return totalTirilla;
    }

    public BigDecimal getTotalBancos() {
        return totalBancos;
    }

    public BigDecimal getTotalContable() {
        return totalContable;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public String getChecksumPdf() {
        return checksumPdf;
    }

    // Métodos setter manuales para resolver errores de compilación
    public void setId(UUID id) {
        this.id = id;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setPuntoVentaId(UUID puntoVentaId) {
        this.puntoVentaId = puntoVentaId;
    }

    public void setEstado(EstadoCuadre estado) {
        this.estado = estado;
    }

    public void setTotalTirilla(BigDecimal totalTirilla) {
        this.totalTirilla = totalTirilla;
    }

    public void setTotalBancos(BigDecimal totalBancos) {
        this.totalBancos = totalBancos;
    }

    public void setTotalContable(BigDecimal totalContable) {
        this.totalContable = totalContable;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public void setChecksumPdf(String checksumPdf) {
        this.checksumPdf = checksumPdf;
    }

    public void setCreadoPor(UUID creadoPor) {
        this.creadoPor = creadoPor;
    }

    public void setActualizadoPor(UUID actualizadoPor) {
        this.actualizadoPor = actualizadoPor;
    }

    public void setFirmadoElabora(boolean firmadoElabora) {
        this.firmadoElabora = firmadoElabora;
    }

    public void setFirmadoAutoriza(boolean firmadoAutoriza) {
        this.firmadoAutoriza = firmadoAutoriza;
    }

    public void setFirmadoAudita(boolean firmadoAudita) {
        this.firmadoAudita = firmadoAudita;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}