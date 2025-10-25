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
}