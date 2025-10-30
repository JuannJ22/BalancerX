package com.balancerx.infrastructure.persistence.jpa;

import com.balancerx.domain.valueobject.BancoTransferencia;
import com.balancerx.domain.valueobject.EstadoTransferencia;
import com.balancerx.domain.valueobject.TipoAsignacionTransferencia;
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

@Entity
@Table(name = "transferencias")
public class JpaTransferencia {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BancoTransferencia banco;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "archivo_id")
    private UUID archivoId;

    @Column(name = "cargado_por", nullable = false)
    private UUID cargadoPor;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTransferencia estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_asignacion")
    private TipoAsignacionTransferencia tipoAsignacion;

    @Column(name = "destino_id")
    private UUID destinoId;

    @Column(name = "asignado_por")
    private UUID asignadoPor;

    @Column(name = "asignado_en")
    private Instant asignadoEn;

    @Column(name = "cuenta_contable")
    private String cuentaContable;

    @Column(name = "cuenta_bancaria")
    private String cuentaBancaria;

    @Column(name = "actualizado_por")
    private UUID actualizadoPor;

    @Column(name = "actualizado_en")
    private Instant actualizadoEn;

    @Version
    private long version;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BancoTransferencia getBanco() {
        return banco;
    }

    public void setBanco(BancoTransferencia banco) {
        this.banco = banco;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public UUID getArchivoId() {
        return archivoId;
    }

    public void setArchivoId(UUID archivoId) {
        this.archivoId = archivoId;
    }

    public UUID getCargadoPor() {
        return cargadoPor;
    }

    public void setCargadoPor(UUID cargadoPor) {
        this.cargadoPor = cargadoPor;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public EstadoTransferencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoTransferencia estado) {
        this.estado = estado;
    }

    public TipoAsignacionTransferencia getTipoAsignacion() {
        return tipoAsignacion;
    }

    public void setTipoAsignacion(TipoAsignacionTransferencia tipoAsignacion) {
        this.tipoAsignacion = tipoAsignacion;
    }

    public UUID getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(UUID destinoId) {
        this.destinoId = destinoId;
    }

    public UUID getAsignadoPor() {
        return asignadoPor;
    }

    public void setAsignadoPor(UUID asignadoPor) {
        this.asignadoPor = asignadoPor;
    }

    public Instant getAsignadoEn() {
        return asignadoEn;
    }

    public void setAsignadoEn(Instant asignadoEn) {
        this.asignadoEn = asignadoEn;
    }

    public String getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(String cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public String getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(String cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public UUID getActualizadoPor() {
        return actualizadoPor;
    }

    public void setActualizadoPor(UUID actualizadoPor) {
        this.actualizadoPor = actualizadoPor;
    }

    public Instant getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Instant actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
