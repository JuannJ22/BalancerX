package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.BancoTransferencia;
import com.balancerx.domain.valueobject.EstadoTransferencia;
import com.balancerx.domain.valueobject.TipoAsignacionTransferencia;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Transferencia {
    private final UUID id;
    private final BancoTransferencia banco;
    private final LocalDate fecha;
    private final BigDecimal valor;
    private final String comentario;
    private final UUID archivoId;
    private final UUID cargadoPor;
    private final Instant createdAt;
    private final EstadoTransferencia estado;
    private final TipoAsignacionTransferencia tipoAsignacion;
    private final UUID destinoId;
    private final UUID asignadoPor;
    private final Instant asignadoEn;
    private final String cuentaContable;
    private final String cuentaBancaria;
    private final UUID actualizadoPor;
    private final Instant actualizadoEn;
    private final long version;

    private Transferencia(Builder builder) {
        this.id = builder.id;
        this.banco = builder.banco;
        this.fecha = builder.fecha;
        this.valor = builder.valor;
        this.comentario = builder.comentario;
        this.archivoId = builder.archivoId;
        this.cargadoPor = builder.cargadoPor;
        this.createdAt = builder.createdAt;
        this.estado = builder.estado;
        this.tipoAsignacion = builder.tipoAsignacion;
        this.destinoId = builder.destinoId;
        this.asignadoPor = builder.asignadoPor;
        this.asignadoEn = builder.asignadoEn;
        this.cuentaContable = builder.cuentaContable;
        this.cuentaBancaria = builder.cuentaBancaria;
        this.actualizadoPor = builder.actualizadoPor;
        this.actualizadoEn = builder.actualizadoEn;
        this.version = builder.version;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Transferencia asignar(TipoAsignacionTransferencia tipoAsignacion, UUID destinoId,
                                 UUID usuarioId, Instant instante) {
        if (tipoAsignacion == null) {
            throw new IllegalArgumentException("El tipo de asignación es requerido");
        }
        if (tipoAsignacion.requiereDestino() && destinoId == null) {
            throw new IllegalArgumentException("La asignación requiere un destino");
        }
        return Transferencia.builder()
                .id(id)
                .banco(banco)
                .fecha(fecha)
                .valor(valor)
                .comentario(comentario)
                .archivoId(archivoId)
                .cargadoPor(cargadoPor)
                .createdAt(createdAt)
                .estado(EstadoTransferencia.ASIGNADA)
                .tipoAsignacion(tipoAsignacion)
                .destinoId(destinoId)
                .asignadoPor(usuarioId)
                .asignadoEn(instante)
                .cuentaContable(cuentaContable)
                .cuentaBancaria(cuentaBancaria)
                .actualizadoPor(usuarioId)
                .actualizadoEn(instante)
                .version(version)
                .build();
    }

    public Transferencia actualizarCuentas(String cuentaContable, String cuentaBancaria, UUID usuarioId,
                                           Instant instante) {
        return Transferencia.builder()
                .id(id)
                .banco(banco)
                .fecha(fecha)
                .valor(valor)
                .comentario(comentario)
                .archivoId(archivoId)
                .cargadoPor(cargadoPor)
                .createdAt(createdAt)
                .estado(estado)
                .tipoAsignacion(tipoAsignacion)
                .destinoId(destinoId)
                .asignadoPor(asignadoPor)
                .asignadoEn(asignadoEn)
                .cuentaContable(cuentaContable)
                .cuentaBancaria(cuentaBancaria)
                .actualizadoPor(usuarioId)
                .actualizadoEn(instante)
                .version(version)
                .build();
    }

    public UUID getId() {
        return id;
    }

    public BancoTransferencia getBanco() {
        return banco;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getComentario() {
        return comentario;
    }

    public Optional<UUID> getArchivoId() {
        return Optional.ofNullable(archivoId);
    }

    public UUID getCargadoPor() {
        return cargadoPor;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public EstadoTransferencia getEstado() {
        return estado;
    }

    public Optional<TipoAsignacionTransferencia> getTipoAsignacion() {
        return Optional.ofNullable(tipoAsignacion);
    }

    public Optional<UUID> getDestinoId() {
        return Optional.ofNullable(destinoId);
    }

    public Optional<UUID> getAsignadoPor() {
        return Optional.ofNullable(asignadoPor);
    }

    public Optional<Instant> getAsignadoEn() {
        return Optional.ofNullable(asignadoEn);
    }

    public Optional<String> getCuentaContable() {
        return Optional.ofNullable(cuentaContable);
    }

    public Optional<String> getCuentaBancaria() {
        return Optional.ofNullable(cuentaBancaria);
    }

    public Optional<UUID> getActualizadoPor() {
        return Optional.ofNullable(actualizadoPor);
    }

    public Optional<Instant> getActualizadoEn() {
        return Optional.ofNullable(actualizadoEn);
    }

    public long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transferencia that = (Transferencia) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transferencia{" +
                "id=" + id +
                ", banco=" + banco +
                ", fecha=" + fecha +
                ", valor=" + valor +
                ", estado=" + estado +
                ", tipoAsignacion=" + tipoAsignacion +
                ", destinoId=" + destinoId +
                '}';
    }

    public static class Builder {
        private UUID id;
        private BancoTransferencia banco;
        private LocalDate fecha;
        private BigDecimal valor;
        private String comentario;
        private UUID archivoId;
        private UUID cargadoPor;
        private Instant createdAt;
        private EstadoTransferencia estado = EstadoTransferencia.REGISTRADA;
        private TipoAsignacionTransferencia tipoAsignacion;
        private UUID destinoId;
        private UUID asignadoPor;
        private Instant asignadoEn;
        private String cuentaContable;
        private String cuentaBancaria;
        private UUID actualizadoPor;
        private Instant actualizadoEn;
        private long version;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder banco(BancoTransferencia banco) {
            this.banco = banco;
            return this;
        }

        public Builder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public Builder valor(BigDecimal valor) {
            this.valor = valor;
            return this;
        }

        public Builder comentario(String comentario) {
            this.comentario = comentario != null ? comentario.trim() : null;
            return this;
        }

        public Builder archivoId(UUID archivoId) {
            this.archivoId = archivoId;
            return this;
        }

        public Builder cargadoPor(UUID cargadoPor) {
            this.cargadoPor = cargadoPor;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder estado(EstadoTransferencia estado) {
            this.estado = estado;
            return this;
        }

        public Builder tipoAsignacion(TipoAsignacionTransferencia tipoAsignacion) {
            this.tipoAsignacion = tipoAsignacion;
            return this;
        }

        public Builder destinoId(UUID destinoId) {
            this.destinoId = destinoId;
            return this;
        }

        public Builder asignadoPor(UUID asignadoPor) {
            this.asignadoPor = asignadoPor;
            return this;
        }

        public Builder asignadoEn(Instant asignadoEn) {
            this.asignadoEn = asignadoEn;
            return this;
        }

        public Builder cuentaContable(String cuentaContable) {
            this.cuentaContable = cuentaContable;
            return this;
        }

        public Builder cuentaBancaria(String cuentaBancaria) {
            this.cuentaBancaria = cuentaBancaria;
            return this;
        }

        public Builder actualizadoPor(UUID actualizadoPor) {
            this.actualizadoPor = actualizadoPor;
            return this;
        }

        public Builder actualizadoEn(Instant actualizadoEn) {
            this.actualizadoEn = actualizadoEn;
            return this;
        }

        public Builder version(long version) {
            this.version = version;
            return this;
        }

        public Transferencia build() {
            if (id == null) {
                throw new IllegalArgumentException("El id de la transferencia es obligatorio");
            }
            if (banco == null) {
                throw new IllegalArgumentException("El banco de la transferencia es obligatorio");
            }
            if (fecha == null) {
                throw new IllegalArgumentException("La fecha de la transferencia es obligatoria");
            }
            if (valor == null) {
                throw new IllegalArgumentException("El valor de la transferencia es obligatorio");
            }
            if (cargadoPor == null) {
                throw new IllegalArgumentException("El usuario que registra la transferencia es obligatorio");
            }
            if (createdAt == null) {
                throw new IllegalArgumentException("La fecha de creación es obligatoria");
            }
            return new Transferencia(this);
        }
    }
}
