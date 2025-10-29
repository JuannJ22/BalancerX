package com.balancerx.infrastructure.persistence.jpa;

import com.balancerx.domain.valueobject.FuenteMovimiento;
import com.balancerx.domain.valueobject.TipoMovimientoBancario;
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
@Table(name = "movimientos_bancarios")
@Getter
@Setter
public class JpaMovimientoBancario {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimientoBancario tipo;

    private String banco;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "referencia_banco")
    private String referenciaBanco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuenteMovimiento fuente;

    @Column(name = "asignado_por")
    private UUID asignadoPor;

    @Column(name = "punto_venta_id")
    private UUID puntoVentaId;

    @Column(name = "cuadre_id")
    private UUID cuadreId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Version
    private long version;

    // Métodos getter manuales para resolver errores de compilación
    public UUID getId() {
        return id;
    }

    public TipoMovimientoBancario getTipo() {
        return tipo;
    }

    public String getBanco() {
        return banco;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getReferenciaBanco() {
        return referenciaBanco;
    }

    public FuenteMovimiento getFuente() {
        return fuente;
    }

    public UUID getAsignadoPor() {
        return asignadoPor;
    }

    public UUID getPuntoVentaId() {
        return puntoVentaId;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getVersion() {
        return version;
    }

    // Métodos setter manuales para resolver errores de compilación
    public void setId(UUID id) {
        this.id = id;
    }

    public void setTipo(TipoMovimientoBancario tipo) {
        this.tipo = tipo;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setReferenciaBanco(String referenciaBanco) {
        this.referenciaBanco = referenciaBanco;
    }

    public void setFuente(FuenteMovimiento fuente) {
        this.fuente = fuente;
    }

    public void setAsignadoPor(UUID asignadoPor) {
        this.asignadoPor = asignadoPor;
    }

    public void setPuntoVentaId(UUID puntoVentaId) {
        this.puntoVentaId = puntoVentaId;
    }

    public void setCuadreId(UUID cuadreId) {
        this.cuadreId = cuadreId;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
