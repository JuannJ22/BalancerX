using BalancerX.Application.Contratos;
using BalancerX.Infrastructure.Datos;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BalancerX.Api.Controllers;

[ApiController]
[Authorize(Roles = "ADMIN,TESORERIA,AUXILIAR")]
[Route("api/catalogos")]
public class CatalogosController : ControllerBase
{
    private readonly BalancerXDbContext contexto;
    private readonly ICatalogosSyncServicio catalogosSyncServicio;

    public CatalogosController(BalancerXDbContext contexto, ICatalogosSyncServicio catalogosSyncServicio)
    {
        this.contexto = contexto;
        this.catalogosSyncServicio = catalogosSyncServicio;
    }

    [HttpGet("bancos")]
    public async Task<IActionResult> ListarBancos(CancellationToken cancellationToken)
    {
        await catalogosSyncServicio.SincronizarAsync(cancellationToken);

        var bancosOrigen = await contexto.Bancos
            .FromSqlRaw(SqlCatalogos.BancosConFallback)
            .ToListAsync(cancellationToken);

        var bancos = bancosOrigen
            .Select(x => new BancoCatalogoResponse { Id = x.Id, Nombre = x.Nombre })
            .OrderBy(x => x.Nombre)
            .ToList();

        return Ok(bancos);
    }

    [HttpGet("bancos/{bancoId:int}/cuentas-contables")]
    public async Task<IActionResult> ListarCuentasPorBanco([FromRoute] int bancoId, CancellationToken cancellationToken)
    {
        await catalogosSyncServicio.SincronizarAsync(cancellationToken);

        var cuentasOrigen = await contexto.CuentasContables
            .FromSqlRaw(SqlCatalogos.CuentasConFallback)
            .ToListAsync(cancellationToken);

        var cuentas = cuentasOrigen
            .Where(x => x.BancoId == bancoId)
            .OrderBy(x => x.NumeroCuenta)
            .Select(x => new CuentaContableCatalogoResponse
            {
                Id = x.Id,
                BancoId = x.BancoId,
                NumeroCuenta = x.NumeroCuenta,
                Descripcion = x.Descripcion
            })
            .ToList();

        return Ok(cuentas);
    }

    [HttpGet("puntos-venta")]
    public async Task<IActionResult> ListarPuntosVenta(CancellationToken cancellationToken)
    {
        await catalogosSyncServicio.SincronizarAsync(cancellationToken);

        var puntos = await contexto.PuntosVenta
            .OrderBy(x => x.Id)
            .Select(x => new ItemCatalogoResponse { Id = x.Id, Nombre = x.Nombre })
            .ToListAsync(cancellationToken);

        return Ok(puntos);
    }

    [HttpGet("vendedores")]
    public async Task<IActionResult> ListarVendedores(CancellationToken cancellationToken)
    {
        await catalogosSyncServicio.SincronizarAsync(cancellationToken);

        var vendedoresOrigen = await contexto.Vendedores
            .FromSqlRaw(SqlCatalogos.VendedoresConFallback)
            .ToListAsync(cancellationToken);

        var vendedores = vendedoresOrigen
            .Select(x => new ItemCatalogoResponse { Id = x.Id, Nombre = x.Nombre })
            .OrderBy(x => x.Nombre)
            .ToList();

        return Ok(vendedores);
    }

    public class ItemCatalogoResponse
    {
        public int Id { get; set; }
        public string Nombre { get; set; } = string.Empty;
    }

    public class BancoCatalogoResponse
    {
        public int Id { get; set; }
        public string Nombre { get; set; } = string.Empty;
    }

    public class CuentaContableCatalogoResponse
    {
        public int Id { get; set; }
        public int BancoId { get; set; }
        public string NumeroCuenta { get; set; } = string.Empty;
        public string Descripcion { get; set; } = string.Empty;
    }

    private static class SqlCatalogos
    {
        public const string VendedoresConFallback = @"
BEGIN TRY
    IF OBJECT_ID(N'bx.sp_catalogo_vendedores', N'P') IS NOT NULL
    BEGIN
        CREATE TABLE #tmp_vendedores ([id] INT NOT NULL, [nombre] NVARCHAR(150) NOT NULL);
        INSERT INTO #tmp_vendedores ([id], [nombre]) EXEC [bx].[sp_catalogo_vendedores];

        IF EXISTS (SELECT 1 FROM #tmp_vendedores)
            SELECT [id], [nombre] FROM #tmp_vendedores;
        ELSE IF OBJECT_ID(N'bx.vendedores', N'U') IS NOT NULL
            SELECT [id], [nombre] FROM [bx].[vendedores];
        ELSE
            SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS NVARCHAR(150)) AS [nombre] WHERE 1 = 0;
    END
    ELSE IF OBJECT_ID(N'bx.vw_vendedores_siigo', N'V') IS NOT NULL
    BEGIN
        CREATE TABLE #tmp_vendedores_view ([id] INT NOT NULL, [nombre] NVARCHAR(150) NOT NULL);

        DECLARE @imp_vendedores BIT = 0;
        BEGIN TRY
            EXECUTE AS OWNER;
            SET @imp_vendedores = 1;

            INSERT INTO #tmp_vendedores_view ([id], [nombre])
            SELECT [Id] AS [id], [Nombre] AS [nombre]
            FROM [bx].[vw_vendedores_siigo];

            REVERT;
            SET @imp_vendedores = 0;
        END TRY
        BEGIN CATCH
            IF @imp_vendedores = 1 REVERT;
            THROW;
        END CATCH

        IF EXISTS (SELECT 1 FROM #tmp_vendedores_view)
            SELECT [id], [nombre] FROM #tmp_vendedores_view;
        ELSE IF OBJECT_ID(N'bx.vendedores', N'U') IS NOT NULL
            SELECT [id], [nombre] FROM [bx].[vendedores];
        ELSE
            SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS NVARCHAR(150)) AS [nombre] WHERE 1 = 0;
    END
    ELSE IF OBJECT_ID(N'bx.vendedores', N'U') IS NOT NULL
        SELECT [id], [nombre] FROM [bx].[vendedores];
    ELSE
        SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS NVARCHAR(150)) AS [nombre] WHERE 1 = 0;
END TRY
BEGIN CATCH
    IF OBJECT_ID(N'bx.vendedores', N'U') IS NOT NULL
        SELECT [id], [nombre] FROM [bx].[vendedores];
    ELSE
        SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS NVARCHAR(150)) AS [nombre] WHERE 1 = 0;
END CATCH";



        public const string CuentasConFallback = @"
BEGIN TRY
    IF OBJECT_ID(N'bx.sp_catalogo_cuentas_contables', N'P') IS NOT NULL
    BEGIN
        CREATE TABLE #tmp_cuentas (
            [id] INT NOT NULL,
            [banco_id] INT NOT NULL,
            [numero_cuenta] NVARCHAR(80) NOT NULL,
            [descripcion] NVARCHAR(200) NOT NULL
        );
        INSERT INTO #tmp_cuentas ([id], [banco_id], [numero_cuenta], [descripcion]) EXEC [bx].[sp_catalogo_cuentas_contables];

        IF EXISTS (SELECT 1 FROM #tmp_cuentas)
            SELECT [id], [banco_id], [numero_cuenta], [descripcion] FROM #tmp_cuentas;
        ELSE IF OBJECT_ID(N'bx.cuentas_contables', N'U') IS NOT NULL
            SELECT [id], [banco_id], [numero_cuenta], [descripcion] FROM [bx].[cuentas_contables];
        ELSE
            SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS INT) AS [banco_id], CAST(NULL AS NVARCHAR(80)) AS [numero_cuenta], CAST(NULL AS NVARCHAR(200)) AS [descripcion] WHERE 1 = 0;
    END
    ELSE IF OBJECT_ID(N'bx.vw_cuentas_contables_siigo', N'V') IS NOT NULL
    BEGIN
        CREATE TABLE #tmp_cuentas_view (
            [id] INT NOT NULL,
            [banco_id] INT NOT NULL,
            [numero_cuenta] NVARCHAR(80) NOT NULL,
            [descripcion] NVARCHAR(200) NOT NULL
        );

        DECLARE @imp_cuentas BIT = 0;
        BEGIN TRY
            EXECUTE AS OWNER;
            SET @imp_cuentas = 1;

            INSERT INTO #tmp_cuentas_view ([id], [banco_id], [numero_cuenta], [descripcion])
            SELECT [Id] AS [id], [BancoId] AS [banco_id], [NumeroCuenta] AS [numero_cuenta], [Descripcion] AS [descripcion]
            FROM [bx].[vw_cuentas_contables_siigo];

            REVERT;
            SET @imp_cuentas = 0;
        END TRY
        BEGIN CATCH
            IF @imp_cuentas = 1 REVERT;
            THROW;
        END CATCH

        IF EXISTS (SELECT 1 FROM #tmp_cuentas_view)
            SELECT [id], [banco_id], [numero_cuenta], [descripcion] FROM #tmp_cuentas_view;
        ELSE IF OBJECT_ID(N'bx.cuentas_contables', N'U') IS NOT NULL
            SELECT [id], [banco_id], [numero_cuenta], [descripcion] FROM [bx].[cuentas_contables];
        ELSE
            SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS INT) AS [banco_id], CAST(NULL AS NVARCHAR(80)) AS [numero_cuenta], CAST(NULL AS NVARCHAR(200)) AS [descripcion] WHERE 1 = 0;
    END
    ELSE IF OBJECT_ID(N'bx.cuentas_contables', N'U') IS NOT NULL
        SELECT [id], [banco_id], [numero_cuenta], [descripcion] FROM [bx].[cuentas_contables];
    ELSE
        SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS INT) AS [banco_id], CAST(NULL AS NVARCHAR(80)) AS [numero_cuenta], CAST(NULL AS NVARCHAR(200)) AS [descripcion] WHERE 1 = 0;
END TRY
BEGIN CATCH
    IF OBJECT_ID(N'bx.cuentas_contables', N'U') IS NOT NULL
        SELECT [id], [banco_id], [numero_cuenta], [descripcion] FROM [bx].[cuentas_contables];
    ELSE
        SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS INT) AS [banco_id], CAST(NULL AS NVARCHAR(80)) AS [numero_cuenta], CAST(NULL AS NVARCHAR(200)) AS [descripcion] WHERE 1 = 0;
END CATCH";
        public const string BancosConFallback = @"
BEGIN TRY
    IF OBJECT_ID(N'bx.sp_catalogo_bancos', N'P') IS NOT NULL
    BEGIN
        CREATE TABLE #tmp_bancos ([id] INT NOT NULL, [nombre] NVARCHAR(150) NOT NULL);
        INSERT INTO #tmp_bancos ([id], [nombre]) EXEC [bx].[sp_catalogo_bancos];

        IF EXISTS (SELECT 1 FROM #tmp_bancos)
            SELECT [id], [nombre] FROM #tmp_bancos;
        ELSE IF OBJECT_ID(N'bx.bancos', N'U') IS NOT NULL
            SELECT [id], [nombre] FROM [bx].[bancos];
        ELSE
            SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS NVARCHAR(150)) AS [nombre] WHERE 1 = 0;
    END
    ELSE IF OBJECT_ID(N'bx.vw_bancos_siigo', N'V') IS NOT NULL
    BEGIN
        CREATE TABLE #tmp_bancos_view ([id] INT NOT NULL, [nombre] NVARCHAR(150) NOT NULL);

        DECLARE @imp_bancos BIT = 0;
        BEGIN TRY
            EXECUTE AS OWNER;
            SET @imp_bancos = 1;

            INSERT INTO #tmp_bancos_view ([id], [nombre])
            SELECT [Id] AS [id], [Nombre] AS [nombre]
            FROM [bx].[vw_bancos_siigo];

            REVERT;
            SET @imp_bancos = 0;
        END TRY
        BEGIN CATCH
            IF @imp_bancos = 1 REVERT;
            THROW;
        END CATCH

        IF EXISTS (SELECT 1 FROM #tmp_bancos_view)
            SELECT [id], [nombre] FROM #tmp_bancos_view;
        ELSE IF OBJECT_ID(N'bx.bancos', N'U') IS NOT NULL
            SELECT [id], [nombre] FROM [bx].[bancos];
        ELSE
            SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS NVARCHAR(150)) AS [nombre] WHERE 1 = 0;
    END
    ELSE IF OBJECT_ID(N'bx.bancos', N'U') IS NOT NULL
        SELECT [id], [nombre] FROM [bx].[bancos];
    ELSE
        SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS NVARCHAR(150)) AS [nombre] WHERE 1 = 0;
END TRY
BEGIN CATCH
    IF OBJECT_ID(N'bx.bancos', N'U') IS NOT NULL
        SELECT [id], [nombre] FROM [bx].[bancos];
    ELSE
        SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS NVARCHAR(150)) AS [nombre] WHERE 1 = 0;
END CATCH";
    }
}
