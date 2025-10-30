package com.balancerx.domain.repository;

import com.balancerx.domain.model.Transferencia;
import com.balancerx.domain.valueobject.BancoTransferencia;
import com.balancerx.domain.valueobject.EstadoTransferencia;
import com.balancerx.domain.valueobject.TipoAsignacionTransferencia;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransferenciaRepository {
    Transferencia save(Transferencia transferencia);

    Optional<Transferencia> findById(UUID id);

    List<Transferencia> findAll();

    List<Transferencia> search(Optional<BancoTransferencia> banco,
                               Optional<LocalDate> fechaDesde,
                               Optional<LocalDate> fechaHasta,
                               Optional<TipoAsignacionTransferencia> tipoAsignacion,
                               Optional<UUID> destinoId,
                               Optional<EstadoTransferencia> estado);
}
