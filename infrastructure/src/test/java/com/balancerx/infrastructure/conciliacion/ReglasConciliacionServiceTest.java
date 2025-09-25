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
        Cuadre cuadre = Cuadre.builder()
                .id(UUID.randomUUID())
                .fecha(LocalDate.now())
                .puntoVentaId(UUID.randomUUID())
                .estado(EstadoCuadre.BORRADOR)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .version(0L)
                .build();
        DocumentoContable documento = DocumentoContable.builder()
                .id(UUID.randomUUID())
                .tipo(TipoDocumentoContable.FACTURA)
                .numero("FAC-1")
                .fecha(LocalDate.now())
                .valor(new BigDecimal("100000"))
                .build();
        MovimientoBancario movimiento = MovimientoBancario.builder()
                .id(UUID.randomUUID())
                .tipo(TipoMovimientoBancario.TRANSFERENCIA)
                .fecha(LocalDate.now())
                .valor(new BigDecimal("100000"))
                .fuente(FuenteMovimiento.CSV)
                .createdAt(Instant.now())
                .version(0L)
                .build();

        List<Match> matches = service.conciliar(cuadre, List.of(documento), List.of(movimiento));

        assertThat(matches).hasSize(1);
        assertThat(matches.get(0).getDocumentoId()).isEqualTo(documento.getId());
        assertThat(matches.get(0).getMovimientoBancarioId()).isEqualTo(movimiento.getId());
    }
}
