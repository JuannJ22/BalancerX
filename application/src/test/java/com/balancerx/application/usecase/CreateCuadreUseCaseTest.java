package com.balancerx.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.balancerx.application.command.CreateCuadreCommand;
import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.model.PuntoVenta;
import com.balancerx.domain.repository.CuadreRepository;
import com.balancerx.domain.repository.PuntoVentaRepository;
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

class CreateCuadreUseCaseTest {
    @Mock
    private CuadreRepository cuadreRepository;

    @Mock
    private PuntoVentaRepository puntoVentaRepository;

    private CreateCuadreUseCase useCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        useCase = new CreateCuadreUseCase(cuadreRepository, puntoVentaRepository, fixedClock);
    }

    @Test
    void creaCuadreEnEstadoBorrador() {
        UUID pvId = UUID.randomUUID();
        when(puntoVentaRepository.findById(pvId))
                .thenReturn(Optional.of(PuntoVenta.builder().id(pvId).nombre("Principal").activo(true).build()));
        when(cuadreRepository.existsAprobadoByFechaAndPuntoVenta(any(), any())).thenReturn(false);
        when(cuadreRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0, Cuadre.class));

        CreateCuadreCommand command = CreateCuadreCommand.builder()
                .fecha(LocalDate.of(2024, 1, 2))
                .puntoVentaId(pvId)
                .totalTirilla(new BigDecimal("100000"))
                .totalBancos(new BigDecimal("100000"))
                .totalContable(new BigDecimal("100000"))
                .creadoPor(UUID.randomUUID())
                .build();

        Cuadre resultado = useCase.handle(command);

        assertThat(resultado.getEstado()).isNotNull();
        assertThat(resultado.getFecha()).isEqualTo(command.getFecha());
    }

    @Test
    void validaCuadreAprobadoExistente() {
        UUID pvId = UUID.randomUUID();
        when(puntoVentaRepository.findById(pvId))
                .thenReturn(Optional.of(PuntoVenta.builder().id(pvId).nombre("Principal").activo(true).build()));
        when(cuadreRepository.existsAprobadoByFechaAndPuntoVenta(any(), any())).thenReturn(true);

        CreateCuadreCommand command = CreateCuadreCommand.builder()
                .fecha(LocalDate.of(2024, 1, 2))
                .puntoVentaId(pvId)
                .totalTirilla(BigDecimal.ZERO)
                .totalBancos(BigDecimal.ZERO)
                .totalContable(BigDecimal.ZERO)
                .creadoPor(UUID.randomUUID())
                .build();

        assertThatThrownBy(() -> useCase.handle(command)).isInstanceOf(IllegalStateException.class);
    }
}
