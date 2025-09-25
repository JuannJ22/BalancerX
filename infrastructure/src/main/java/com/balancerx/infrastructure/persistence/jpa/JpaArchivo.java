package com.balancerx.infrastructure.persistence.jpa;

import com.balancerx.domain.valueobject.TipoArchivo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "archivos")
@Getter
@Setter
public class JpaArchivo {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoArchivo tipo;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String checksum;

    @Lob
    @Column(name = "metadata_json")
    private String metadataJson;

    @Column(name = "subido_por", nullable = false)
    private UUID subidoPor;

    @Column(name = "cuadre_id")
    private UUID cuadreId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
