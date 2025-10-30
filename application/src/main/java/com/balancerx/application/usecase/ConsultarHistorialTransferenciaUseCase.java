package com.balancerx.application.usecase;

import com.balancerx.domain.model.TransferenciaHistorialEntry;
import com.balancerx.domain.repository.TransferenciaHistorialRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ConsultarHistorialTransferenciaUseCase {
    private final TransferenciaHistorialRepository historialRepository;

    public ConsultarHistorialTransferenciaUseCase(TransferenciaHistorialRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    public List<TransferenciaHistorialEntry> handle(UUID transferenciaId) {
        return historialRepository.findByTransferenciaId(transferenciaId);
    }
}
