El motor aplica estrategias encadenadas:

| Estrategia | Condiciones | Score |
| --- | --- | --- |
| EXACT_MATCH | Mismo día, monto exacto | 1.00 |
| TOLERANCE_MATCH | Mismo día, delta ≤ 500 COP | 0.85 |
| REFERENCE_MATCH | Referencia exacta, monto exacto | 0.90 |
| FUZZY_REF_PLUS_TOLERANCE | Similaridad referencia > 0.7, delta ≤ 500 | 0.75 |

Las coincidencias se almacenan con `razonesJson` para trazabilidad. Documentos sin match generan entradas con score 0 para revisión manual.
