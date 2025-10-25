package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.EstadoCuadre;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Cuadre {
    private final UUID id;
    private final LocalDate fecha;
    private final UUID puntoVentaId;
    private final EstadoCuadre estado;
    private final BigDecimal totalTirilla;
    private final BigDecimal totalBancos;
    private final BigDecimal totalContable;
    private final String pdfPath;
    private final String checksumPdf;
    private final UUID creadoPor;
    private final UUID actualizadoPor;
    private final boolean firmadoElabora;
    private final boolean firmadoAutoriza;
    private final boolean firmadoAudita;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public Cuadre(UUID id, LocalDate fecha, UUID puntoVentaId, EstadoCuadre estado,
                  BigDecimal totalTirilla, BigDecimal totalBancos, BigDecimal totalContable,
                  String pdfPath, String checksumPdf, UUID creadoPor, UUID actualizadoPor,
                  boolean firmadoElabora, boolean firmadoAutoriza, boolean firmadoAudita,
                  Instant createdAt, Instant updatedAt, long version) {
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

    public Cuadre changeEstado(EstadoCuadre nuevoEstado, UUID actorId) {
        Objects.requireNonNull(nuevoEstado, "nuevoEstado");
        return new Cuadre(
                this.id,
                this.fecha,
                this.puntoVentaId,
                nuevoEstado,
                this.totalTirilla,
                this.totalBancos,
                this.totalContable,
                this.pdfPath,
                this.checksumPdf,
                this.creadoPor,
                actorId,
                this.firmadoElabora,
                this.firmadoAutoriza,
                this.firmadoAudita,
                this.createdAt,
                this.updatedAt,
                this.version
        );
    }

    public boolean isFirmadoPorElabora() {
        return firmadoElabora;
    }

    public boolean isFirmadoPorAutoriza() {
        return firmadoAutoriza;
    }

    public boolean isFirmadoPorAudita() {
        return firmadoAudita;
    }

    public Optional<String> getPdfPath() {
        return Optional.ofNullable(pdfPath);
    }

    public Optional<String> getChecksumPdf() {
        return Optional.ofNullable(checksumPdf);
    }

    // Getters
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

    public boolean isFirmadoAudita() {
        return firmadoAudita;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }
}
