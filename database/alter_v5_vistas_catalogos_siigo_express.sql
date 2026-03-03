/*
  BALANCERX - ALTER V5 (SQL Express, sin SQL Agent)
  Catálogos en tiempo real desde SiigoCat usando VISTAS.

  Objetivo:
  - Evitar ejecuciones manuales de sincronización.
  - Exponer bancos, cuentas y vendedores siempre actualizados desde tablas origen.

  IMPORTANTE:
  - Estas vistas son de solo lectura para catálogos.
  - Si la app va a consumir "directo" desde Siigo, debe consultar estas vistas
    en lugar de tablas físicas bx.bancos/bx.vendedores/bx.cuentas_contables.
*/
SET NOCOUNT ON;
GO

CREATE OR ALTER VIEW bx.vw_bancos_siigo
AS
WITH bancos_origen AS
(
    SELECT DISTINCT
        NombreBanco = LEFT(
            REPLACE(
                REPLACE(
                    CASE
                        WHEN CHARINDEX(' CTA', UPPER(LTRIM(RTRIM(COALESCE(NombreMae, ''))))) > 0
                            THEN LEFT(UPPER(LTRIM(RTRIM(COALESCE(NombreMae, '')))), CHARINDEX(' CTA', UPPER(LTRIM(RTRIM(COALESCE(NombreMae, ''))))) - 1)
                        WHEN CHARINDEX(' CUENTA', UPPER(LTRIM(RTRIM(COALESCE(NombreMae, ''))))) > 0
                            THEN LEFT(UPPER(LTRIM(RTRIM(COALESCE(NombreMae, '')))), CHARINDEX(' CUENTA', UPPER(LTRIM(RTRIM(COALESCE(NombreMae, ''))))) - 1)
                        ELSE UPPER(LTRIM(RTRIM(COALESCE(NombreMae, ''))))
                    END,
                    '  ', ' '
                ),
                'BANCOS', 'BANCO'
            ),
            150
        )
    FROM SiigoCat.dbo.TABLA_MAESTRO_CONTABLE
    WHERE (CASE
            WHEN RIGHT(LTRIM(RTRIM(COALESCE(CuentasMae, ''))), 2) = '00' THEN LTRIM(RTRIM(COALESCE(CuentasMae, '')))
            ELSE CONCAT(LTRIM(RTRIM(COALESCE(CuentasMae, ''))), '00')
          END) IN ('1110050100', '1110050200', '1110050300', '1110050400', '1110050500', '1110050600', '1110050700', '1110050800', '1120050100', '1120050200')
      AND LTRIM(RTRIM(COALESCE(NombreMae, ''))) <> ''
)
SELECT
    Id = ABS(CHECKSUM(NombreBanco)),
    Nombre = NombreBanco
FROM bancos_origen
WHERE NombreBanco NOT IN ('BANCO', 'BANCOS');
GO

CREATE OR ALTER VIEW bx.vw_cuentas_contables_siigo
AS
WITH cuentas_origen AS
(
    SELECT DISTINCT
        NombreBanco = LEFT(
            REPLACE(
                REPLACE(
                    CASE
                        WHEN CHARINDEX(' CTA', UPPER(LTRIM(RTRIM(COALESCE(NombreMae, ''))))) > 0
                            THEN LEFT(UPPER(LTRIM(RTRIM(COALESCE(NombreMae, '')))), CHARINDEX(' CTA', UPPER(LTRIM(RTRIM(COALESCE(NombreMae, ''))))) - 1)
                        WHEN CHARINDEX(' CUENTA', UPPER(LTRIM(RTRIM(COALESCE(NombreMae, ''))))) > 0
                            THEN LEFT(UPPER(LTRIM(RTRIM(COALESCE(NombreMae, '')))), CHARINDEX(' CUENTA', UPPER(LTRIM(RTRIM(COALESCE(NombreMae, ''))))) - 1)
                        ELSE UPPER(LTRIM(RTRIM(COALESCE(NombreMae, ''))))
                    END,
                    '  ', ' '
                ),
                'BANCOS', 'BANCO'
            ),
            150
        ),
        NumeroCuenta = LEFT(CASE
            WHEN RIGHT(LTRIM(RTRIM(COALESCE(CuentasMae, ''))), 2) = '00' THEN LTRIM(RTRIM(COALESCE(CuentasMae, '')))
            ELSE CONCAT(LTRIM(RTRIM(COALESCE(CuentasMae, ''))), '00')
        END, 80),
        Descripcion = LEFT(UPPER(LTRIM(RTRIM(COALESCE(NombreMae, '')))), 200)
    FROM SiigoCat.dbo.TABLA_MAESTRO_CONTABLE
    WHERE (CASE
            WHEN RIGHT(LTRIM(RTRIM(COALESCE(CuentasMae, ''))), 2) = '00' THEN LTRIM(RTRIM(COALESCE(CuentasMae, '')))
            ELSE CONCAT(LTRIM(RTRIM(COALESCE(CuentasMae, ''))), '00')
          END) IN ('1110050100', '1110050200', '1110050300', '1110050400', '1110050500', '1110050600', '1110050700', '1110050800', '1120050100', '1120050200')
      AND LTRIM(RTRIM(COALESCE(NombreMae, ''))) <> ''
      AND LTRIM(RTRIM(COALESCE(CuentasMae, ''))) <> ''
)
SELECT
    Id = ABS(CHECKSUM(CONCAT(NombreBanco, '|', NumeroCuenta))),
    BancoId = ABS(CHECKSUM(NombreBanco)),
    NumeroCuenta,
    Descripcion
FROM cuentas_origen
WHERE NombreBanco NOT IN ('BANCO', 'BANCOS');
GO

CREATE OR ALTER VIEW bx.vw_vendedores_siigo
AS
SELECT
    Id = TRY_CONVERT(INT, VenVen),
    Nombre = LEFT(LTRIM(RTRIM(COALESCE(NombreVen, ''))), 150)
FROM SiigoCat.dbo.TABLA_DESCRIPCION_VENDEDORES
WHERE TRY_CONVERT(INT, VenVen) BETWEEN 1 AND 99
  AND LTRIM(RTRIM(COALESCE(NombreVen, ''))) <> '';
GO

/*
  Consultas de prueba rápidas:

  SELECT TOP (100) * FROM bx.vw_bancos_siigo ORDER BY Nombre;
  SELECT TOP (100) * FROM bx.vw_cuentas_contables_siigo ORDER BY BancoId, NumeroCuenta;
  SELECT TOP (100) * FROM bx.vw_vendedores_siigo ORDER BY Id;
*/
