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
    private final String puntoVentaTexto;
    private final String valorTexto;
    private final String fechaTexto;
    private final UUID actualizadoPor;
    private final Instant actualizadoEn;
    private final UUID receptorId;
    private final UUID firmadaPor;
    private final Instant firmadaEn;
    private final UUID recibidaPor;
    private final Instant recibidaEn;
    private final UUID impresaPor;
    private final Instant impresaEn;
    private final String comentarioRecepcion;
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
        this.puntoVentaTexto = builder.puntoVentaTexto;
        this.valorTexto = builder.valorTexto;
        this.fechaTexto = builder.fechaTexto;
        this.actualizadoPor = builder.actualizadoPor;
        this.actualizadoEn = builder.actualizadoEn;
        this.receptorId = builder.receptorId;
        this.firmadaPor = builder.firmadaPor;
        this.firmadaEn = builder.firmadaEn;
        this.recibidaPor = builder.recibidaPor;
        this.recibidaEn = builder.recibidaEn;
        this.impresaPor = builder.impresaPor;
        this.impresaEn = builder.impresaEn;
        this.comentarioRecepcion = builder.comentarioRecepcion;
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
                .puntoVentaTexto(puntoVentaTexto)
                .valorTexto(valorTexto)
                .fechaTexto(fechaTexto)
                .actualizadoPor(usuarioId)
                .actualizadoEn(instante)
                .receptorId(receptorId)
                .firmadaPor(firmadaPor)
                .firmadaEn(firmadaEn)
                .recibidaPor(recibidaPor)
                .recibidaEn(recibidaEn)
                .impresaPor(impresaPor)
                .impresaEn(impresaEn)
                .comentarioRecepcion(comentarioRecepcion)
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
                .puntoVentaTexto(puntoVentaTexto)
                .valorTexto(valorTexto)
                .fechaTexto(fechaTexto)
                .actualizadoPor(usuarioId)
                .actualizadoEn(instante)
                .receptorId(receptorId)
                .firmadaPor(firmadaPor)
                .firmadaEn(firmadaEn)
                .recibidaPor(recibidaPor)
                .recibidaEn(recibidaEn)
                .impresaPor(impresaPor)
                .impresaEn(impresaEn)
                .comentarioRecepcion(comentarioRecepcion)
                .version(version)
                .build();
    }

    public Transferencia marcarFirmada(UUID usuarioId, Instant instante) {
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
                .puntoVentaTexto(puntoVentaTexto)
                .valorTexto(valorTexto)
                .fechaTexto(fechaTexto)
                .actualizadoPor(usuarioId)
                .actualizadoEn(instante)
                .receptorId(receptorId)
                .firmadaPor(usuarioId)
                .firmadaEn(instante)
                .recibidaPor(recibidaPor)
                .recibidaEn(recibidaEn)
                .impresaPor(impresaPor)
                .impresaEn(impresaEn)
                .comentarioRecepcion(comentarioRecepcion)
                .version(version)
                .build();
    }

    public Transferencia registrarRecepcion(UUID usuarioId, Instant instante, String comentarioUso) {
        if (impresaEn != null) {
            throw new IllegalStateException("La transferencia ya fue impresa");
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
                .estado(estado)
                .tipoAsignacion(tipoAsignacion)
                .destinoId(destinoId)
                .asignadoPor(asignadoPor)
                .asignadoEn(asignadoEn)
                .cuentaContable(cuentaContable)
                .cuentaBancaria(cuentaBancaria)
                .puntoVentaTexto(puntoVentaTexto)
                .valorTexto(valorTexto)
                .fechaTexto(fechaTexto)
                .actualizadoPor(usuarioId)
                .actualizadoEn(instante)
                .receptorId(receptorId)
                .firmadaPor(firmadaPor)
                .firmadaEn(firmadaEn)
                .recibidaPor(usuarioId)
                .recibidaEn(instante)
                .impresaPor(usuarioId)
                .impresaEn(instante)
                .comentarioRecepcion(comentarioUso)
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

    public Optional<String> getPuntoVentaTexto() {
        return Optional.ofNullable(puntoVentaTexto);
    }

    public Optional<String> getValorTexto() {
        return Optional.ofNullable(valorTexto);
    }

    public Optional<String> getFechaTexto() {
        return Optional.ofNullable(fechaTexto);
    }

    public Optional<UUID> getActualizadoPor() {
        return Optional.ofNullable(actualizadoPor);
    }

    public Optional<Instant> getActualizadoEn() {
        return Optional.ofNullable(actualizadoEn);
    }

    public Optional<UUID> getReceptorId() {
        return Optional.ofNullable(receptorId);
    }

    public Optional<UUID> getFirmadaPor() {
        return Optional.ofNullable(firmadaPor);
    }

    public Optional<Instant> getFirmadaEn() {
        return Optional.ofNullable(firmadaEn);
    }

    public Optional<UUID> getRecibidaPor() {
        return Optional.ofNullable(recibidaPor);
    }

    public Optional<Instant> getRecibidaEn() {
        return Optional.ofNullable(recibidaEn);
    }

    public Optional<UUID> getImpresaPor() {
        return Optional.ofNullable(impresaPor);
    }

    public Optional<Instant> getImpresaEn() {
        return Optional.ofNullable(impresaEn);
    }

    public Optional<String> getComentarioRecepcion() {
        return Optional.ofNullable(comentarioRecepcion);
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
                ", receptorId=" + receptorId +
                ", puntoVentaTexto='" + puntoVentaTexto + '\'' +
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
        private String puntoVentaTexto;
        private String valorTexto;
        private String fechaTexto;
        private UUID actualizadoPor;
        private Instant actualizadoEn;
        private UUID receptorId;
        private UUID firmadaPor;
        private Instant firmadaEn;
        private UUID recibidaPor;
        private Instant recibidaEn;
        private UUID impresaPor;
        private Instant impresaEn;
        private String comentarioRecepcion;
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

        public Builder puntoVentaTexto(String puntoVentaTexto) {
            this.puntoVentaTexto = puntoVentaTexto != null ? puntoVentaTexto.trim() : null;
            return this;
        }

        public Builder valorTexto(String valorTexto) {
            this.valorTexto = valorTexto != null ? valorTexto.trim() : null;
            return this;
        }

        public Builder fechaTexto(String fechaTexto) {
            this.fechaTexto = fechaTexto != null ? fechaTexto.trim() : null;
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

        public Builder receptorId(UUID receptorId) {
            this.receptorId = receptorId;
            return this;
        }

        public Builder firmadaPor(UUID firmadaPor) {
            this.firmadaPor = firmadaPor;
            return this;
        }

        public Builder firmadaEn(Instant firmadaEn) {
            this.firmadaEn = firmadaEn;
            return this;
        }

        public Builder recibidaPor(UUID recibidaPor) {
            this.recibidaPor = recibidaPor;
            return this;
        }

        public Builder recibidaEn(Instant recibidaEn) {
            this.recibidaEn = recibidaEn;
            return this;
        }

        public Builder impresaPor(UUID impresaPor) {
            this.impresaPor = impresaPor;
            return this;
        }

        public Builder impresaEn(Instant impresaEn) {
            this.impresaEn = impresaEn;
            return this;
        }

        public Builder comentarioRecepcion(String comentarioRecepcion) {
            this.comentarioRecepcion = comentarioRecepcion != null ? comentarioRecepcion.trim() : null;
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
