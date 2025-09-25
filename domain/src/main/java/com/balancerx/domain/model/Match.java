package com.balancerx.domain.model;

import com.balancerx.domain.valueobject.EstrategiaMatch;
import com.balancerx.domain.valueobject.EstadoMatch;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder(toBuilder = true)
@With
public class Match {
    UUID id;
    UUID movimientoBancarioId;
    UUID documentoId;
    EstrategiaMatch estrategia;
    BigDecimal score;
    EstadoMatch estado;
    Map<String, Object> razones;
    UUID decidedBy;
    Instant decidedAt;
}
