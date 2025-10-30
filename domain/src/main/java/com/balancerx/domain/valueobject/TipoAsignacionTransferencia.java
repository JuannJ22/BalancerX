package com.balancerx.domain.valueobject;

public enum TipoAsignacionTransferencia {
    PUNTO_VENTA,
    CARTERA;

    public boolean requiereDestino() {
        return this == PUNTO_VENTA || this == CARTERA;
    }
}
