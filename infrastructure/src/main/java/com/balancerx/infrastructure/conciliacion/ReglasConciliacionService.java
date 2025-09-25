package com.balancerx.infrastructure.conciliacion;

import com.balancerx.domain.model.Cuadre;
import com.balancerx.domain.model.DocumentoContable;
import com.balancerx.domain.model.Match;
import com.balancerx.domain.model.MovimientoBancario;
import com.balancerx.domain.service.ConciliacionService;
import com.balancerx.domain.valueobject.EstrategiaMatch;
import com.balancerx.domain.valueobject.EstadoMatch;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ReglasConciliacionService implements ConciliacionService {
    private static final BigDecimal TOLERANCIA = new BigDecimal("500");

    @Override
    public List<Match> conciliar(
            Cuadre cuadre, List<DocumentoContable> documentos, List<MovimientoBancario> movimientos) {
        List<Match> matches = new ArrayList<>();
        Set<UUID> movimientosUsados = new HashSet<>();
        documentos.sort(Comparator.comparing(DocumentoContable::getValor).reversed());
        for (DocumentoContable documento : documentos) {
            Optional<MovimientoBancario> matchExacto = movimientos.stream()
                    .filter(mov -> !movimientosUsados.contains(mov.getId()))
                    .filter(mov -> mismaFecha(documento, mov))
                    .filter(mov -> montoExacto(documento.getValor(), mov.getValor()))
                    .findFirst();
            if (matchExacto.isPresent()) {
                MovimientoBancario mov = matchExacto.get();
                movimientosUsados.add(mov.getId());
                matches.add(crearMatch(documento, mov, EstrategiaMatch.EXACT_MATCH, BigDecimal.ONE));
                continue;
            }

            Optional<MovimientoBancario> matchTolerancia = movimientos.stream()
                    .filter(mov -> !movimientosUsados.contains(mov.getId()))
                    .filter(mov -> mismaFecha(documento, mov))
                    .filter(mov -> diferenciaDentroTolerancia(documento.getValor(), mov.getValor()))
                    .findFirst();
            if (matchTolerancia.isPresent()) {
                MovimientoBancario mov = matchTolerancia.get();
                movimientosUsados.add(mov.getId());
                matches.add(crearMatch(documento, mov, EstrategiaMatch.TOLERANCE_MATCH, new BigDecimal("0.85")));
                continue;
            }

            Optional<MovimientoBancario> matchReferencia = movimientos.stream()
                    .filter(mov -> !movimientosUsados.contains(mov.getId()))
                    .filter(mov -> StringUtils.equals(normalizar(documento.getReferencia().orElse("")),
                            normalizar(mov.getReferenciaBanco())))
                    .filter(mov -> montoExacto(documento.getValor(), mov.getValor()))
                    .findFirst();
            if (matchReferencia.isPresent()) {
                MovimientoBancario mov = matchReferencia.get();
                movimientosUsados.add(mov.getId());
                matches.add(crearMatch(documento, mov, EstrategiaMatch.REFERENCE_MATCH, new BigDecimal("0.90")));
                continue;
            }

            movimientos.stream()
                    .filter(mov -> !movimientosUsados.contains(mov.getId()))
                    .filter(mov -> diferenciaDentroTolerancia(documento.getValor(), mov.getValor()))
                    .filter(mov -> similitudReferencia(documento.getReferencia().orElse(""),
                                    mov.getReferenciaBanco())
                            > 0.7)
                    .findFirst()
                    .ifPresent(mov -> {
                        movimientosUsados.add(mov.getId());
                        matches.add(crearMatch(
                                documento, mov, EstrategiaMatch.FUZZY_REF_PLUS_TOLERANCE, new BigDecimal("0.75")));
                    });
        }

        // Observaciones para no conciliados
        Set<UUID> documentosConciliados = matches.stream()
                .map(Match::getDocumentoId)
                .collect(Collectors.toSet());
        documentos.stream()
                .filter(doc -> !documentosConciliados.contains(doc.getId()))
                .forEach(doc -> matches.add(Match.builder()
                        .id(UUID.randomUUID())
                        .documentoId(doc.getId())
                        .estrategia(EstrategiaMatch.FUZZY_REF_PLUS_TOLERANCE)
                        .estado(EstadoMatch.PROPUESTO)
                        .score(BigDecimal.ZERO)
                        .razones(Map.of("detalle", "Sin coincidencias automáticas"))
                        .build()));

        return matches;
    }

    private Match crearMatch(DocumentoContable documento, MovimientoBancario movimiento, EstrategiaMatch estrategia, BigDecimal score) {
        return Match.builder()
                .id(UUID.randomUUID())
                .movimientoBancarioId(movimiento.getId())
                .documentoId(documento.getId())
                .estrategia(estrategia)
                .score(score)
                .estado(EstadoMatch.PROPUESTO)
                .razones(Map.of(
                        "documento", documento.getNumero(),
                        "movimiento", movimiento.getReferenciaBanco(),
                        "delta", movimiento.getValor().subtract(documento.getValor()).abs().toPlainString()))
                .build();
    }

    private boolean mismaFecha(DocumentoContable documento, MovimientoBancario movimiento) {
        return ChronoUnit.DAYS.between(documento.getFecha(), movimiento.getFecha()) == 0;
    }

    private boolean montoExacto(BigDecimal montoDoc, BigDecimal montoMov) {
        return montoDoc.compareTo(montoMov) == 0;
    }

    private boolean diferenciaDentroTolerancia(BigDecimal montoDoc, BigDecimal montoMov) {
        return montoDoc.subtract(montoMov).abs().compareTo(TOLERANCIA) <= 0;
    }

    private String normalizar(String referencia) {
        return referencia == null ? "" : referencia.replaceAll("\\s+", "").toUpperCase();
    }

    private double similitudReferencia(String refDoc, String refMov) {
        if (StringUtils.isBlank(refDoc) || StringUtils.isBlank(refMov)) {
            return 0.0;
        }
        String a = normalizar(refDoc);
        String b = normalizar(refMov);
        int maxLength = Math.max(a.length(), b.length());
        if (maxLength == 0) {
            return 1.0;
        }
        int distancia = distanciaLevenshtein(a, b);
        return 1.0 - ((double) distancia / (double) maxLength);
    }

    private int distanciaLevenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }
}
