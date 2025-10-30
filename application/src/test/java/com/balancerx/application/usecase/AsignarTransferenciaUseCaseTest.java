package com.balancerx.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.balancerx.application.command.AsignarTransferenciaCommand;
import com.balancerx.domain.model.Transferencia;
import com.balancerx.domain.repository.TransferenciaHistorialRepository;
import com.balancerx.domain.repository.TransferenciaRepository;
import com.balancerx.domain.valueobject.BancoTransferencia;
import com.balancerx.domain.valueobject.EstadoTransferencia;
import com.balancerx.domain.valueobject.TipoAsignacionTransferencia;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AsignarTransferenciaUseCaseTest {

    @Mock
    private TransferenciaRepository transferenciaRepository;

    @Mock
    private TransferenciaHistorialRepository historialRepository;

    private Clock fixedClock;
    private AsignarTransferenciaUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fixedClock = Clock.fixed(Instant.parse("2024-01-05T08:00:00Z"), ZoneOffset.UTC);
        useCase = new AsignarTransferenciaUseCase(transferenciaRepository, historialRepository, fixedClock);
    }

    @Test
    void asignaTransferenciaAPuntoVenta() {
        UUID transferenciaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID puntoVentaId = UUID.randomUUID();
        Transferencia transferencia = Transferencia.builder()
                .id(transferenciaId)
                .banco(BancoTransferencia.BBVA)
                .fecha(LocalDate.of(2024, 1, 4))
                .valor(BigDecimal.valueOf(120000))
                .comentario("Venta turno mañana")
                .archivoId(UUID.randomUUID())
                .cargadoPor(usuarioId)
                .createdAt(fixedClock.instant())
                .estado(EstadoTransferencia.REGISTRADA)
                .build();

        when(transferenciaRepository.findById(transferenciaId)).thenReturn(Optional.of(transferencia));
        when(transferenciaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AsignarTransferenciaCommand command = new AsignarTransferenciaCommand(
                transferenciaId,
                TipoAsignacionTransferencia.PUNTO_VENTA,
                puntoVentaId,
                usuarioId,
                null
        );

        Transferencia asignada = useCase.handle(command);

        assertThat(asignada.getEstado()).isEqualTo(EstadoTransferencia.ASIGNADA);
        assertThat(asignada.getDestinoId()).contains(puntoVentaId);
        assertThat(asignada.getAsignadoPor()).contains(usuarioId);
        assertThat(asignada.getAsignadoEn()).contains(fixedClock.instant());

        verify(historialRepository).save(any());
    }

    @Test
    void fallaCuandoFaltaDestinoParaAsignacion() {
        UUID transferenciaId = UUID.randomUUID();
        Transferencia transferencia = Transferencia.builder()
                .id(transferenciaId)
                .banco(BancoTransferencia.ITAU)
                .fecha(LocalDate.now())
                .valor(BigDecimal.valueOf(50000))
                .archivoId(UUID.randomUUID())
                .cargadoPor(UUID.randomUUID())
                .createdAt(fixedClock.instant())
                .estado(EstadoTransferencia.REGISTRADA)
                .build();

        when(transferenciaRepository.findById(transferenciaId)).thenReturn(Optional.of(transferencia));

        AsignarTransferenciaCommand command = new AsignarTransferenciaCommand(
                transferenciaId,
                TipoAsignacionTransferencia.CARTERA,
                null,
                UUID.randomUUID(),
                null
        );

        assertThatThrownBy(() -> useCase.handle(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("requiere un destino");
    }
}
