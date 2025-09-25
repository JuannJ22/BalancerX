package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.EstadoCuadre;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

@Getter
@Builder(toBuilder = true)
@With
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

    public Cuadre changeEstado(EstadoCuadre nuevoEstado, UUID actorId) {
        Objects.requireNonNull(nuevoEstado, "nuevoEstado");
        return toBuilder()
                .estado(nuevoEstado)
                .actualizadoPor(actorId)
                .build();
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
}
