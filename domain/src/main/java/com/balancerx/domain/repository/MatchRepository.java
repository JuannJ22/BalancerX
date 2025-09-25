package com.balancerx.domain.repository;

import com.balancerx.domain.model.Match;
import java.util.List;
import java.util.UUID;

public interface MatchRepository {
    Match save(Match match);

    void saveAll(List<Match> matches);

    List<Match> findByCuadreId(UUID cuadreId);
}
