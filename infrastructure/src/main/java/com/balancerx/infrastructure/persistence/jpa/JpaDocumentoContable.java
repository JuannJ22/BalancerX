package com.balancerx.infrastructure.persistence.jpa;

import com.balancerx.domain.valueobject.TipoDocumentoContable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documentos_contables")
@Getter
@Setter
public class JpaDocumentoContable {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDocumentoContable tipo;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    private String referencia;

    @Column(name = "cuadre_id")
    private UUID cuadreId;

    private String observacion;

    // Métodos setter manuales para resolver errores de compilación
    public void setId(UUID id) {
        this.id = id;
    }

    public void setTipo(TipoDocumentoContable tipo) {
        this.tipo = tipo;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public void setCuadreId(UUID cuadreId) {
        this.cuadreId = cuadreId;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    // Métodos getter manuales para resolver errores de compilación
    public UUID getId() {
        return id;
    }

    public TipoDocumentoContable getTipo() {
        return tipo;
    }

    public String getNumero() {
        return numero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getReferencia() {
        return referencia;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public String getObservacion() {
        return observacion;
    }
}