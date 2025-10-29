package com.balancerx.infrastructure.persistence.repository;

import com.balancerx.domain.model.Match;
import com.balancerx.domain.repository.MatchRepository;
import com.balancerx.infrastructure.persistence.mapper.MatchMapper;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository

public class MatchRepositoryAdapter implements MatchRepository {
    private final MatchJpaRepository jpaRepository;
    private final DocumentoContableJpaRepository documentoContableJpaRepository;
    private final MatchMapper mapper;

    public MatchRepositoryAdapter(MatchJpaRepository jpaRepository, DocumentoContableJpaRepository documentoContableJpaRepository, MatchMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.documentoContableJpaRepository = documentoContableJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Match save(Match match) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(match)));
    }

    @Override
    public void saveAll(List<Match> matches) {
        jpaRepository.saveAll(matches.stream().map(mapper::toEntity).toList());
    }

    @Override
    public List<Match> findByCuadreId(UUID cuadreId) {
        List<UUID> documentoIds = documentoContableJpaRepository.findByCuadreId(cuadreId).stream()
                .map(doc -> doc.getId())
                .toList();
        if (documentoIds.isEmpty()) {
            return Collections.emptyList();
        }
        return jpaRepository.findByDocumentoIdIn(documentoIds).stream().map(mapper::toDomain).toList();
    }
}
