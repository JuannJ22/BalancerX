package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.FuenteMovimiento;
import com.balancerx.domain.valueobject.TipoMovimientoBancario;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class MovimientoBancario {
    private final UUID id;
    private final TipoMovimientoBancario tipo;
    private final String banco;
    private final LocalDate fecha;
    private final BigDecimal valor;
    private final String referenciaBanco;
    private final FuenteMovimiento fuente;
    private final UUID asignadoPor;
    private final UUID puntoVentaId;
    private final UUID cuadreId;
    private final Instant createdAt;
    private final long version;

    public MovimientoBancario(UUID id, TipoMovimientoBancario tipo, String banco, LocalDate fecha,
                              BigDecimal valor, String referenciaBanco, FuenteMovimiento fuente,
                              UUID asignadoPor, UUID puntoVentaId, UUID cuadreId, 
                              Instant createdAt, long version) {
        this.id = id;
        this.tipo = tipo;
        this.banco = banco;
        this.fecha = fecha;
        this.valor = valor;
        this.referenciaBanco = referenciaBanco;
        this.fuente = fuente;
        this.asignadoPor = asignadoPor;
        this.puntoVentaId = puntoVentaId;
        this.cuadreId = cuadreId;
        this.createdAt = createdAt;
        this.version = version;
    }

    // Método estático para crear un builder
    public static MovimientoBancarioBuilder builder() {
        return new MovimientoBancarioBuilder();
    }

    // Clase Builder estática
    public static class MovimientoBancarioBuilder {
        private UUID id;
        private TipoMovimientoBancario tipo;
        private String banco;
        private LocalDate fecha;
        private BigDecimal valor;
        private String referenciaBanco;
        private FuenteMovimiento fuente;
        private UUID asignadoPor;
        private UUID puntoVentaId;
        private UUID cuadreId;
        private Instant createdAt;
        private long version;

        private MovimientoBancarioBuilder() {
            // Constructor privado para forzar el uso del método builder()
        }

        public MovimientoBancarioBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public MovimientoBancarioBuilder tipo(TipoMovimientoBancario tipo) {
            this.tipo = tipo;
            return this;
        }

        public MovimientoBancarioBuilder banco(String banco) {
            this.banco = banco;
            return this;
        }

        public MovimientoBancarioBuilder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public MovimientoBancarioBuilder valor(BigDecimal valor) {
            this.valor = valor;
            return this;
        }

        public MovimientoBancarioBuilder referenciaBanco(String referenciaBanco) {
            this.referenciaBanco = referenciaBanco;
            return this;
        }

        public MovimientoBancarioBuilder fuente(FuenteMovimiento fuente) {
            this.fuente = fuente;
            return this;
        }

        public MovimientoBancarioBuilder asignadoPor(UUID asignadoPor) {
            this.asignadoPor = asignadoPor;
            return this;
        }

        public MovimientoBancarioBuilder puntoVentaId(UUID puntoVentaId) {
            this.puntoVentaId = puntoVentaId;
            return this;
        }

        public MovimientoBancarioBuilder cuadreId(UUID cuadreId) {
            this.cuadreId = cuadreId;
            return this;
        }

        public MovimientoBancarioBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public MovimientoBancarioBuilder version(long version) {
            this.version = version;
            return this;
        }

        public MovimientoBancario build() {
            // Validaciones básicas
            if (id == null) {
                throw new IllegalArgumentException("El ID es requerido");
            }
            if (tipo == null) {
                throw new IllegalArgumentException("El tipo de movimiento es requerido");
            }
            if (banco == null || banco.trim().isEmpty()) {
                throw new IllegalArgumentException("El banco es requerido");
            }
            if (fecha == null) {
                throw new IllegalArgumentException("La fecha es requerida");
            }
            if (valor == null) {
                throw new IllegalArgumentException("El valor es requerido");
            }
            if (fuente == null) {
                throw new IllegalArgumentException("La fuente del movimiento es requerida");
            }

            return new MovimientoBancario(id, tipo, banco, fecha, valor, referenciaBanco, 
                                          fuente, asignadoPor, puntoVentaId, cuadreId, 
                                          createdAt, version);
        }
    }

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getVersion() {
        return version;
    }

    public Optional<UUID> getAsignadoPor() {
        return Optional.ofNullable(asignadoPor);
    }

    public Optional<UUID> getPuntoVentaId() {
        return Optional.ofNullable(puntoVentaId);
    }

    public Optional<UUID> getCuadreId() {
        return Optional.ofNullable(cuadreId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovimientoBancario that = (MovimientoBancario) o;
        return version == that.version &&
               java.util.Objects.equals(id, that.id) &&
               tipo == that.tipo &&
               java.util.Objects.equals(banco, that.banco) &&
               java.util.Objects.equals(fecha, that.fecha) &&
               java.util.Objects.equals(valor, that.valor) &&
               java.util.Objects.equals(referenciaBanco, that.referenciaBanco) &&
               fuente == that.fuente &&
               java.util.Objects.equals(asignadoPor, that.asignadoPor) &&
               java.util.Objects.equals(puntoVentaId, that.puntoVentaId) &&
               java.util.Objects.equals(cuadreId, that.cuadreId) &&
               java.util.Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, tipo, banco, fecha, valor, referenciaBanco, fuente, 
                                      asignadoPor, puntoVentaId, cuadreId, createdAt, version);
    }

    @Override
    public String toString() {
        return "MovimientoBancario{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", banco='" + banco + '\'' +
                ", fecha=" + fecha +
                ", valor=" + valor +
                ", referenciaBanco='" + referenciaBanco + '\'' +
                ", fuente=" + fuente +
                ", asignadoPor=" + asignadoPor +
                ", puntoVentaId=" + puntoVentaId +
                ", cuadreId=" + cuadreId +
                ", createdAt=" + createdAt +
                ", version=" + version +
                '}';
    }
}
