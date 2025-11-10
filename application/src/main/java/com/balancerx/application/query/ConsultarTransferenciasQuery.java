package com.balancerx.application.query;

import com.balancerx.domain.valueobject.BancoTransferencia;
import com.balancerx.domain.valueobject.EstadoTransferencia;
import com.balancerx.domain.valueobject.TipoAsignacionTransferencia;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class ConsultarTransferenciasQuery {
    private final BancoTransferencia banco;
    private final LocalDate fechaDesde;
    private final LocalDate fechaHasta;
    private final BigDecimal valorMin;
    private final BigDecimal valorMax;
    private final TipoAsignacionTransferencia tipoAsignacion;
    private final UUID destinoId;
    private final EstadoTransferencia estado;

    public ConsultarTransferenciasQuery(BancoTransferencia banco, LocalDate fechaDesde, LocalDate fechaHasta,
                                        BigDecimal valorMin, BigDecimal valorMax,
                                        TipoAsignacionTransferencia tipoAsignacion, UUID destinoId,
                                        EstadoTransferencia estado) {
        this.banco = banco;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.valorMin = valorMin;
        this.valorMax = valorMax;
        this.tipoAsignacion = tipoAsignacion;
        this.destinoId = destinoId;
        this.estado = estado;
    }

    public Optional<BancoTransferencia> getBanco() {
        return Optional.ofNullable(banco);
    }

    public Optional<LocalDate> getFechaDesde() {
        return Optional.ofNullable(fechaDesde);
    }

    public Optional<LocalDate> getFechaHasta() {
        return Optional.ofNullable(fechaHasta);
    }

    public Optional<BigDecimal> getValorMin() {
        return Optional.ofNullable(valorMin);
    }

    public Optional<BigDecimal> getValorMax() {
        return Optional.ofNullable(valorMax);
    }

    public Optional<TipoAsignacionTransferencia> getTipoAsignacion() {
        return Optional.ofNullable(tipoAsignacion);
    }

    public Optional<UUID> getDestinoId() {
        return Optional.ofNullable(destinoId);
    }

    public Optional<EstadoTransferencia> getEstado() {
        return Optional.ofNullable(estado);
    }
}
