package com.balancerx.infrastructure.persistence.mapper;

import com.balancerx.domain.model.Match;
import com.balancerx.infrastructure.persistence.jpa.JpaMatch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchMapper {
    private final ObjectMapper objectMapper;

    public JpaMatch toEntity(Match match) {
        JpaMatch entity = new JpaMatch();
        entity.setId(match.getId() != null ? match.getId() : UUID.randomUUID());
        entity.setMovimientoBancarioId(match.getMovimientoBancarioId());
        entity.setDocumentoId(match.getDocumentoId());
        entity.setEstrategia(match.getEstrategia());
        entity.setScore(match.getScore());
        entity.setEstado(match.getEstado());
        entity.setRazonesJson(toJson(match.getRazones()));
        entity.setDecidedBy(match.getDecidedBy());
        entity.setDecidedAt(match.getDecidedAt());
        return entity;
    }

    public Match toDomain(JpaMatch entity) {
        return Match.builder()
                .id(entity.getId())
                .movimientoBancarioId(entity.getMovimientoBancarioId())
                .documentoId(entity.getDocumentoId())
                .estrategia(entity.getEstrategia())
                .score(entity.getScore())
                .estado(entity.getEstado())
                .razones(fromJson(entity.getRazonesJson()))
                .decidedBy(entity.getDecidedBy())
                .decidedAt(entity.getDecidedAt())
                .build();
    }

    private Map<String, Object> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error al parsear razonesJson", e);
        }
    }

    private String toJson(Map<String, Object> razones) {
        try {
            return objectMapper.writeValueAsString(razones != null ? razones : Map.of());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error al serializar razones", e);
        }
    }
}
