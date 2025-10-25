package com.balancerx.infrastructure.conciliacion;

import static org.assertj.core.api.Assertions.assertThat;

import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.model.DocumentoContable;
import com.balancerx.domain.model.Match;
import com.balancerx.domain.model.MovimientoBancario;
import com.balancerx.domain.valueobject.EstadoCuadre;
import com.balancerx.domain.valueobject.FuenteMovimiento;
import com.balancerx.domain.valueobject.TipoDocumentoContable;
import com.balancerx.domain.valueobject.TipoMovimientoBancario;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReglasConciliacionServiceTest {
    private final ReglasConciliacionService service = new ReglasConciliacionService();

    @Test
    void conciliaExacto() {
        Cuadre cuadre = new Cuadre(
                UUID.randomUUID(),
                LocalDate.now(),
                UUID.randomUUID(),
                EstadoCuadre.BORRADOR,
                new BigDecimal("100000"),
                new BigDecimal("100000"),
                new BigDecimal("100000"),
                null,
                null,
                UUID.randomUUID(),
                UUID.randomUUID(),
                false,
                false,
                false,
                Instant.now(),
                Instant.now(),
                0L
        );
        DocumentoContable documento = DocumentoContable.builder()
                .id(UUID.randomUUID())
                .tipo(TipoDocumentoContable.FACTURA)
                .numero("FAC-1")
                .fecha(LocalDate.now())
                .valor(new BigDecimal("100000"))
                .cuadreId(cuadre.getId())
                .build();
        MovimientoBancario movimiento = MovimientoBancario.builder()
                .id(UUID.randomUUID())
                .tipo(TipoMovimientoBancario.TRANSFERENCIA)
                .banco("BANCO_TEST")
                .fecha(LocalDate.now())
                .valor(new BigDecimal("100000"))
                .referenciaBanco(null)
                .fuente(FuenteMovimiento.CSV)
                .asignadoPor(null)
                .puntoVentaId(UUID.randomUUID())
                .cuadreId(cuadre.getId())
                .createdAt(Instant.now())
                .version(0L)
                .build();

        List<Match> matches = service.conciliar(cuadre, List.of(documento), List.of(movimiento));

        assertThat(matches).hasSize(1);
        assertThat(matches.get(0).getDocumentoId()).isEqualTo(documento.getId());
        assertThat(matches.get(0).getMovimientoBancarioId()).isEqualTo(movimiento.getId());
    }
}
