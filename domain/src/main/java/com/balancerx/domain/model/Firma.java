package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.MetodoFirma;
import com.balancerx.domain.valueobject.RolUsuario;
import java.time.Instant;
import java.util.UUID;

public class Firma {
    private final UUID id;
    private final UUID cuadreId;
    private final RolUsuario rol;
    private final UUID firmanteId;
    private final MetodoFirma metodo;
    private final String hash;
    private final Instant timestamp;

    public Firma(UUID id, UUID cuadreId, RolUsuario rol, UUID firmanteId, 
                 MetodoFirma metodo, String hash, Instant timestamp) {
        this.id = id;
        this.cuadreId = cuadreId;
        this.rol = rol;
        this.firmanteId = firmanteId;
        this.metodo = metodo;
        this.hash = hash;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCuadreId() {
        return cuadreId;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public UUID getFirmanteId() {
        return firmanteId;
    }

    public MetodoFirma getMetodo() {
        return metodo;
    }

    public String getHash() {
        return hash;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Firma firma = (Firma) o;
        return java.util.Objects.equals(id, firma.id) &&
               java.util.Objects.equals(cuadreId, firma.cuadreId) &&
               rol == firma.rol &&
               java.util.Objects.equals(firmanteId, firma.firmanteId) &&
               metodo == firma.metodo &&
               java.util.Objects.equals(hash, firma.hash) &&
               java.util.Objects.equals(timestamp, firma.timestamp);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, cuadreId, rol, firmanteId, metodo, hash, timestamp);
    }

    @Override
    public String toString() {
        return "Firma{" +
                "id=" + id +
                ", cuadreId=" + cuadreId +
                ", rol=" + rol +
                ", firmanteId=" + firmanteId +
                ", metodo=" + metodo +
                ", hash='" + hash + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
