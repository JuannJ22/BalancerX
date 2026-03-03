/*
  BALANCERX - ALTER V4
  Sincroniza catálogos operativos de BalancerX desde SiigoCat.

  Reglas solicitadas:
  - Bancos: tomar registros de TABLA_MAESTRO_CONTABLE por cuentas específicas.
  - Vendedores: tomar TABLA_DESCRIPCION_VENDEDORES con VenVen <= 99.

  Notas:
  - Es idempotente: se puede ejecutar múltiples veces.
  - No elimina registros existentes para evitar romper históricos de transferencias.
  - Para vendedores se conserva el id = VenVen (con IDENTITY_INSERT).
*/
SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

CREATE OR ALTER PROCEDURE bx.sp_sincronizar_catalogos_desde_siigo
    @BaseOrigen SYSNAME = N'SiigoCat'
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    IF OBJECT_ID(N'bx.bancos', N'U') IS NULL
        THROW 50001, 'No existe la tabla bx.bancos.', 1;

    IF OBJECT_ID(N'bx.vendedores', N'U') IS NULL
        THROW 50002, 'No existe la tabla bx.vendedores.', 1;

    IF OBJECT_ID(N'bx.cuentas_contables', N'U') IS NULL
        THROW 50003, 'No existe la tabla bx.cuentas_contables.', 1;

    CREATE TABLE #BancosFuente
    (
        Nombre NVARCHAR(150) NOT NULL PRIMARY KEY
    );

    CREATE TABLE #CuentasFuente
    (
        NombreBanco NVARCHAR(150) NOT NULL,
        NumeroCuenta NVARCHAR(80) NOT NULL,
        Descripcion NVARCHAR(200) NOT NULL,
        PRIMARY KEY (NombreBanco, NumeroCuenta)
    );

    CREATE TABLE #VendedoresFuente
    (
        Id INT NOT NULL PRIMARY KEY,
        Nombre NVARCHAR(150) NOT NULL
    );

    DECLARE @sql NVARCHAR(MAX);

    SET @sql = N'
        WITH cuentas_origen AS
        (
            SELECT
                NombreMae = UPPER(LTRIM(RTRIM(COALESCE(NombreMae, '''')))),
                CuentasMae = CASE
                    WHEN RIGHT(LTRIM(RTRIM(COALESCE(CuentasMae, ''''))), 2) = ''00'' THEN LTRIM(RTRIM(COALESCE(CuentasMae, '''')))
                    ELSE CONCAT(LTRIM(RTRIM(COALESCE(CuentasMae, ''''))), ''00'')
                END
            FROM ' + QUOTENAME(@BaseOrigen) + N'.dbo.TABLA_MAESTRO_CONTABLE
            WHERE (CASE
                    WHEN RIGHT(LTRIM(RTRIM(COALESCE(CuentasMae, ''''))), 2) = ''00'' THEN LTRIM(RTRIM(COALESCE(CuentasMae, '''')))
                    ELSE CONCAT(LTRIM(RTRIM(COALESCE(CuentasMae, ''''))), ''00'')
                END) IN (''1110050100'', ''1110050200'', ''1110050300'', ''1110050400'', ''1110050500'', ''1110050600'', ''1110050700'', ''1110050800'', ''1120050100'', ''1120050200'')
              AND LTRIM(RTRIM(COALESCE(NombreMae, ''''))) <> ''''
        )
        INSERT INTO #BancosFuente (Nombre)
        SELECT DISTINCT TOP (10000)
            LEFT(
                REPLACE(
                    REPLACE(
                        CASE
                            WHEN CHARINDEX('' CTA'', NombreMae) > 0 THEN LEFT(NombreMae, CHARINDEX('' CTA'', NombreMae) - 1)
                            WHEN CHARINDEX('' CUENTA'', NombreMae) > 0 THEN LEFT(NombreMae, CHARINDEX('' CUENTA'', NombreMae) - 1)
                            ELSE NombreMae
                        END,
                        ''  '', '' ''
                    ),
                    ''BANCOS'', ''BANCO''
                ),
                150
            )
        FROM cuentas_origen
        WHERE NombreMae NOT IN (''BANCOS'', ''BANCO'');

        INSERT INTO #CuentasFuente (NombreBanco, NumeroCuenta, Descripcion)
        SELECT DISTINCT
            LEFT(
                REPLACE(
                    REPLACE(
                        CASE
                            WHEN CHARINDEX('' CTA'', NombreMae) > 0 THEN LEFT(NombreMae, CHARINDEX('' CTA'', NombreMae) - 1)
                            WHEN CHARINDEX('' CUENTA'', NombreMae) > 0 THEN LEFT(NombreMae, CHARINDEX('' CUENTA'', NombreMae) - 1)
                            ELSE NombreMae
                        END,
                        ''  '', '' ''
                    ),
                    ''BANCOS'', ''BANCO''
                ),
                150
            ) AS NombreBanco,
            LEFT(CuentasMae, 80) AS NumeroCuenta,
            LEFT(NombreMae, 200) AS Descripcion
        FROM cuentas_origen
        WHERE NombreMae NOT IN (''BANCOS'', ''BANCO'')
          AND CuentasMae <> '''';

        INSERT INTO #VendedoresFuente (Id, Nombre)
        SELECT
            TRY_CONVERT(INT, VenVen) AS Id,
            LEFT(LTRIM(RTRIM(COALESCE(NombreVen, ''''))), 150) AS Nombre
        FROM ' + QUOTENAME(@BaseOrigen) + N'.dbo.TABLA_DESCRIPCION_VENDEDORES
        WHERE TRY_CONVERT(INT, VenVen) IS NOT NULL
          AND TRY_CONVERT(INT, VenVen) BETWEEN 1 AND 99
          AND LTRIM(RTRIM(COALESCE(NombreVen, ''''))) <> '''';';

    EXEC sp_executesql @sql;

    BEGIN TRAN;

    MERGE bx.bancos AS target
    USING #BancosFuente AS source
       ON target.nombre = source.Nombre
    WHEN MATCHED AND target.nombre <> source.Nombre THEN
        UPDATE SET target.nombre = source.Nombre
    WHEN NOT MATCHED BY TARGET THEN
        INSERT (nombre) VALUES (source.Nombre);

    MERGE bx.cuentas_contables AS target
    USING
    (
        SELECT
            b.id AS BancoId,
            c.NumeroCuenta,
            c.Descripcion
        FROM #CuentasFuente c
        INNER JOIN bx.bancos b
            ON b.nombre = c.NombreBanco
    ) AS source
       ON target.banco_id = source.BancoId
      AND target.numero_cuenta = source.NumeroCuenta
    WHEN MATCHED AND target.descripcion <> source.Descripcion THEN
        UPDATE SET target.descripcion = source.Descripcion
    WHEN NOT MATCHED BY TARGET THEN
        INSERT (banco_id, numero_cuenta, descripcion)
        VALUES (source.BancoId, source.NumeroCuenta, source.Descripcion);

    SET IDENTITY_INSERT bx.vendedores ON;

    MERGE bx.vendedores AS target
    USING #VendedoresFuente AS source
       ON target.id = source.Id
    WHEN MATCHED AND target.nombre <> source.Nombre THEN
        UPDATE SET target.nombre = source.Nombre
    WHEN NOT MATCHED BY TARGET THEN
        INSERT (id, nombre) VALUES (source.Id, source.Nombre);

    SET IDENTITY_INSERT bx.vendedores OFF;

    DECLARE @maxVendedorId INT = (SELECT ISNULL(MAX(Id), 0) FROM bx.vendedores);
    DBCC CHECKIDENT ('bx.vendedores', RESEED, @maxVendedorId) WITH NO_INFOMSGS;

    COMMIT;
END;
GO

-- Ejecución inicial
EXEC bx.sp_sincronizar_catalogos_desde_siigo @BaseOrigen = N'SiigoCat';
GO
