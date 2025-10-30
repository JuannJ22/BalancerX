package com.balancerx.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.balancerx.application.command.RegistrarTransferenciaCommand;
import com.balancerx.application.service.FileStoragePort;
import com.balancerx.domain.model.Transferencia;
import com.balancerx.domain.repository.ArchivoRepository;
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
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RegistrarTransferenciaUseCaseTest {

    @Mock
    private TransferenciaRepository transferenciaRepository;

    @Mock
    private TransferenciaHistorialRepository historialRepository;

    @Mock
    private ArchivoRepository archivoRepository;

    @Mock
    private FileStoragePort fileStoragePort;

    private Clock fixedClock;

    private RegistrarTransferenciaUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fixedClock = Clock.fixed(Instant.parse("2024-01-01T10:15:30Z"), ZoneOffset.UTC);
        useCase = new RegistrarTransferenciaUseCase(
                transferenciaRepository,
                historialRepository,
                archivoRepository,
                fileStoragePort,
                fixedClock
        );
        when(fileStoragePort.storePdf(any(), any())).thenReturn(new FileStoragePort.StoredFile("/tmp/file.pdf", "abc123"));
        when(transferenciaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void registraTransferenciaSinAsignacion() {
        RegistrarTransferenciaCommand command = new RegistrarTransferenciaCommand(
                UUID.randomUUID(),
                "banco.pdf",
                "hola".getBytes(),
                BancoTransferencia.BANCOLOMBIA,
                LocalDate.of(2024, 1, 1),
                BigDecimal.valueOf(150000),
                "Venta Juan",
                null,
                null
        );

        Transferencia transferencia = useCase.handle(command);

        assertThat(transferencia.getEstado()).isEqualTo(EstadoTransferencia.REGISTRADA);
        assertThat(transferencia.getComentario()).isEqualTo("Venta Juan");
        assertThat(transferencia.getArchivoId()).isPresent();

        verify(archivoRepository).save(any());
        verify(transferenciaRepository).save(any());
        ArgumentCaptor<List<?>> historialCaptor = ArgumentCaptor.forClass(List.class);
        verify(historialRepository).saveAll(historialCaptor.capture());
        assertThat(historialCaptor.getValue()).hasSize(1);
    }

    @Test
    void registraTransferenciaAsignadaAPuntoVenta() {
        UUID puntoVentaId = UUID.randomUUID();
        RegistrarTransferenciaCommand command = new RegistrarTransferenciaCommand(
                UUID.randomUUID(),
                "bbva.pdf",
                "contenido".getBytes(),
                BancoTransferencia.BBVA,
                LocalDate.of(2024, 1, 2),
                BigDecimal.valueOf(200000),
                "Venta Maria",
                TipoAsignacionTransferencia.PUNTO_VENTA,
                puntoVentaId
        );

        Transferencia transferencia = useCase.handle(command);

        assertThat(transferencia.getEstado()).isEqualTo(EstadoTransferencia.ASIGNADA);
        assertThat(transferencia.getDestinoId()).contains(puntoVentaId);
        assertThat(transferencia.getAsignadoEn()).contains(fixedClock.instant());

        ArgumentCaptor<List<?>> historialCaptor = ArgumentCaptor.forClass(List.class);
        verify(historialRepository).saveAll(historialCaptor.capture());
        assertThat(historialCaptor.getValue()).hasSize(2);
    }
}
