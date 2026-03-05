/*
  BALANCERX - ALTER V7
  Procedimientos de catálogos con EXECUTE AS OWNER para evitar
  errores de permisos cruzados sobre SiigoCat al consultar vistas.

  Requiere ejecutar como usuario con permisos DDL (ej. sa).
*/
SET NOCOUNT ON;
GO

CREATE OR ALTER PROCEDURE bx.sp_catalogo_vendedores
WITH EXECUTE AS OWNER
AS
BEGIN
    SET NOCOUNT ON;

    SELECT [Id] AS [id], [Nombre] AS [nombre]
    FROM [bx].[vw_vendedores_siigo]
    ORDER BY [Nombre];
END;
GO

CREATE OR ALTER PROCEDURE bx.sp_catalogo_bancos
WITH EXECUTE AS OWNER
AS
BEGIN
    SET NOCOUNT ON;

    SELECT [Id] AS [id], [Nombre] AS [nombre]
    FROM [bx].[vw_bancos_siigo]
    ORDER BY [Nombre];
END;
GO

CREATE OR ALTER PROCEDURE bx.sp_catalogo_cuentas_contables
WITH EXECUTE AS OWNER
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        [Id] AS [id],
        [BancoId] AS [banco_id],
        [NumeroCuenta] AS [numero_cuenta],
        [Descripcion] AS [descripcion]
    FROM [bx].[vw_cuentas_contables_siigo]
    ORDER BY [BancoId], [NumeroCuenta];
END;
GO

GRANT EXECUTE ON OBJECT::bx.sp_catalogo_vendedores TO PUBLIC;
GRANT EXECUTE ON OBJECT::bx.sp_catalogo_bancos TO PUBLIC;
GRANT EXECUTE ON OBJECT::bx.sp_catalogo_cuentas_contables TO PUBLIC;
GO
