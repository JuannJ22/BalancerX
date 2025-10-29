package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.TipoDocumentoContable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class DocumentoContable {
    private final UUID id;
    private final TipoDocumentoContable tipo;
    private final String numero;
    private final LocalDate fecha;
    private final BigDecimal valor;
    private final String referencia;
    private final UUID cuadreId;
    private final String observacion;

    public DocumentoContable(UUID id, TipoDocumentoContable tipo, String numero, LocalDate fecha,
                             BigDecimal valor, String referencia, UUID cuadreId, String observacion) {
        this.id = id;
        this.tipo = tipo;
        this.numero = numero;
        this.fecha = fecha;
        this.valor = valor;
        this.referencia = referencia;
        this.cuadreId = cuadreId;
        this.observacion = observacion;
    }

    // Método estático para crear un builder
    public static DocumentoContableBuilder builder() {
        return new DocumentoContableBuilder();
    }

    // Clase Builder estática
    public static class DocumentoContableBuilder {
        private UUID id;
        private TipoDocumentoContable tipo;
        private String numero;
        private LocalDate fecha;
        private BigDecimal valor;
        private String referencia;
        private UUID cuadreId;
        private String observacion;

        private DocumentoContableBuilder() {
            // Constructor privado para forzar el uso del método builder()
        }

        public DocumentoContableBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public DocumentoContableBuilder tipo(TipoDocumentoContable tipo) {
            this.tipo = tipo;
            return this;
        }

        public DocumentoContableBuilder numero(String numero) {
            this.numero = numero;
            return this;
        }

        public DocumentoContableBuilder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public DocumentoContableBuilder valor(BigDecimal valor) {
            this.valor = valor;
            return this;
        }

        public DocumentoContableBuilder referencia(String referencia) {
            this.referencia = referencia;
            return this;
        }

        public DocumentoContableBuilder cuadreId(UUID cuadreId) {
            this.cuadreId = cuadreId;
            return this;
        }

        public DocumentoContableBuilder observacion(String observacion) {
            this.observacion = observacion;
            return this;
        }

        public DocumentoContable build() {
            // Validaciones básicas
            if (id == null) {
                throw new IllegalArgumentException("El ID es requerido");
            }
            if (tipo == null) {
                throw new IllegalArgumentException("El tipo de documento es requerido");
            }
            if (numero == null || numero.trim().isEmpty()) {
                throw new IllegalArgumentException("El número de documento es requerido");
            }
            if (fecha == null) {
                throw new IllegalArgumentException("La fecha es requerida");
            }
            if (valor == null) {
                throw new IllegalArgumentException("El valor es requerido");
            }

            return new DocumentoContable(id, tipo, numero, fecha, valor, referencia, cuadreId, observacion);
        }
    }

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

    public Optional<UUID> getCuadreId() {
        return Optional.ofNullable(cuadreId);
    }

    public Optional<String> getReferencia() {
        return Optional.ofNullable(referencia);
    }

    public Optional<String> getObservacion() {
        return Optional.ofNullable(observacion);
    }

    // Método with para crear nueva instancia con cuadreId modificado
    public DocumentoContable withCuadreId(UUID cuadreId) {
        return new DocumentoContable(id, tipo, numero, fecha, valor, referencia, cuadreId, observacion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentoContable that = (DocumentoContable) o;
        return java.util.Objects.equals(id, that.id) &&
               tipo == that.tipo &&
               java.util.Objects.equals(numero, that.numero) &&
               java.util.Objects.equals(fecha, that.fecha) &&
               java.util.Objects.equals(valor, that.valor) &&
               java.util.Objects.equals(referencia, that.referencia) &&
               java.util.Objects.equals(cuadreId, that.cuadreId) &&
               java.util.Objects.equals(observacion, that.observacion);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, tipo, numero, fecha, valor, referencia, cuadreId, observacion);
    }

    @Override
    public String toString() {
        return "DocumentoContable{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", numero='" + numero + '\'' +
                ", fecha=" + fecha +
                ", valor=" + valor +
                ", referencia='" + referencia + '\'' +
                ", cuadreId=" + cuadreId +
                ", observacion='" + observacion + '\'' +
                '}';
    }
}
