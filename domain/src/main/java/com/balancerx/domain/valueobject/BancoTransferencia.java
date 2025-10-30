package com.balancerx.domain.valueobject;

import java.util.Locale;

public enum BancoTransferencia {
    BANCOLOMBIA,
    BBVA,
    DAVIVIENDA,
    ITAU,
    BANCOLOMBIA_QR,
    BOGOTA;

    public static BancoTransferencia fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("El banco de la transferencia es requerido");
        }
        String normalized = raw.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        if (!normalized.contains("_QR") && normalized.endsWith("QR")) {
            normalized = normalized.substring(0, normalized.length() - 2) + "_QR";
        }
        for (BancoTransferencia value : values()) {
            if (value.name().equals(normalized)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Banco de transferencia no soportado: " + raw);
    }
}
