USE [BalancerX]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF OBJECT_ID(N'[bx].[vw_bancos_siigo]', N'V') IS NOT NULL
    DROP VIEW [bx].[vw_bancos_siigo];
GO

CREATE VIEW [bx].[vw_bancos_siigo]
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
          END) IN ('1110050500', '1110050600', '1110050700', '1110050800', '1120050100', '1120050200')
      AND LTRIM(RTRIM(COALESCE(NombreMae, ''))) <> ''
)
SELECT
    Id = ABS(CHECKSUM(NombreBanco)),
    Nombre = NombreBanco
FROM bancos_origen
WHERE NombreBanco NOT IN ('BANCO', 'BANCOS');
GO

IF OBJECT_ID(N'[bx].[vw_cuentas_contables_siigo]', N'V') IS NOT NULL
    DROP VIEW [bx].[vw_cuentas_contables_siigo];
GO

CREATE VIEW [bx].[vw_cuentas_contables_siigo]
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
          END) IN ('1110050500', '1110050600', '1110050700', '1110050800', '1120050100', '1120050200')
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

IF OBJECT_ID(N'[bx].[vw_vendedores_siigo]', N'V') IS NOT NULL
    DROP VIEW [bx].[vw_vendedores_siigo];
GO

CREATE VIEW [bx].[vw_vendedores_siigo]
AS
SELECT
    Id = TRY_CONVERT(INT, VenVen),
    Nombre = LEFT(LTRIM(RTRIM(COALESCE(NombreVen, ''))), 150)
FROM SiigoCat.dbo.TABLA_DESCRIPCION_VENDEDORES
WHERE TRY_CONVERT(INT, VenVen) BETWEEN 1 AND 99
  AND LTRIM(RTRIM(COALESCE(NombreVen, ''))) <> '';
GO
