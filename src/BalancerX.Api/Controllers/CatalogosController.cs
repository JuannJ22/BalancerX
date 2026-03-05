using BalancerX.Application.Contratos;
using BalancerX.Domain.Entidades;
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

        var bancosOrigen = await CargarBancosAsync(cancellationToken);
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

        var cuentasOrigen = await CargarCuentasAsync(cancellationToken);
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

        var vendedoresOrigen = await CargarVendedoresAsync(cancellationToken);
        var vendedores = vendedoresOrigen
            .Select(x => new ItemCatalogoResponse { Id = x.Id, Nombre = x.Nombre })
            .OrderBy(x => x.Nombre)
            .ToList();

        return Ok(vendedores);
    }

    private async Task<List<Vendedor>> CargarVendedoresAsync(CancellationToken cancellationToken)
    {
        var desdeSp = await IntentarConsultaAsync(
            contexto.Vendedores,
            SqlCatalogos.VendedoresDesdeSp,
            cancellationToken);
        if (desdeSp.Count > 0) return desdeSp;

        var desdeVista = await IntentarConsultaAsync(
            contexto.Vendedores,
            SqlCatalogos.VendedoresDesdeVista,
            cancellationToken);
        if (desdeVista.Count > 0) return desdeVista;

        return await IntentarConsultaAsync(
            contexto.Vendedores,
            SqlCatalogos.VendedoresDesdeTabla,
            cancellationToken);
    }

    private async Task<List<Banco>> CargarBancosAsync(CancellationToken cancellationToken)
    {
        var desdeSp = await IntentarConsultaAsync(
            contexto.Bancos,
            SqlCatalogos.BancosDesdeSp,
            cancellationToken);
        if (desdeSp.Count > 0) return desdeSp;

        var desdeVista = await IntentarConsultaAsync(
            contexto.Bancos,
            SqlCatalogos.BancosDesdeVista,
            cancellationToken);
        if (desdeVista.Count > 0) return desdeVista;

        return await IntentarConsultaAsync(
            contexto.Bancos,
            SqlCatalogos.BancosDesdeTabla,
            cancellationToken);
    }

    private async Task<List<CuentaContable>> CargarCuentasAsync(CancellationToken cancellationToken)
    {
        var desdeSp = await IntentarConsultaAsync(
            contexto.CuentasContables,
            SqlCatalogos.CuentasDesdeSp,
            cancellationToken);
        if (desdeSp.Count > 0) return desdeSp;

        var desdeVista = await IntentarConsultaAsync(
            contexto.CuentasContables,
            SqlCatalogos.CuentasDesdeVista,
            cancellationToken);
        if (desdeVista.Count > 0) return desdeVista;

        return await IntentarConsultaAsync(
            contexto.CuentasContables,
            SqlCatalogos.CuentasDesdeTabla,
            cancellationToken);
    }

    private static async Task<List<T>> IntentarConsultaAsync<T>(DbSet<T> set, string sql, CancellationToken cancellationToken)
        where T : class
    {
        try
        {
            return await set.FromSqlRaw(sql).ToListAsync(cancellationToken);
        }
        catch
        {
            return [];
        }
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
        public const string VendedoresDesdeSp = @"
IF OBJECT_ID(N'bx.sp_catalogo_vendedores', N'P') IS NOT NULL
    EXEC [bx].[sp_catalogo_vendedores];
ELSE
    SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS NVARCHAR(150)) AS [nombre] WHERE 1 = 0;";

        public const string VendedoresDesdeVista = @"
IF OBJECT_ID(N'bx.vw_vendedores_siigo', N'V') IS NOT NULL
    SELECT [Id] AS [id], [Nombre] AS [nombre]
    FROM [bx].[vw_vendedores_siigo];
ELSE
    SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS NVARCHAR(150)) AS [nombre] WHERE 1 = 0;";

        public const string VendedoresDesdeTabla = @"
IF OBJECT_ID(N'bx.vendedores', N'U') IS NOT NULL
    SELECT [id], [nombre]
    FROM [bx].[vendedores];
ELSE
    SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS NVARCHAR(150)) AS [nombre] WHERE 1 = 0;";

        public const string BancosDesdeSp = @"
IF OBJECT_ID(N'bx.sp_catalogo_bancos', N'P') IS NOT NULL
    EXEC [bx].[sp_catalogo_bancos];
ELSE
    SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS NVARCHAR(150)) AS [nombre] WHERE 1 = 0;";

        public const string BancosDesdeVista = @"
IF OBJECT_ID(N'bx.vw_bancos_siigo', N'V') IS NOT NULL
    SELECT [Id] AS [id], [Nombre] AS [nombre]
    FROM [bx].[vw_bancos_siigo];
ELSE
    SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS NVARCHAR(150)) AS [nombre] WHERE 1 = 0;";

        public const string BancosDesdeTabla = @"
IF OBJECT_ID(N'bx.bancos', N'U') IS NOT NULL
    SELECT [id], [nombre]
    FROM [bx].[bancos];
ELSE
    SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS NVARCHAR(150)) AS [nombre] WHERE 1 = 0;";

        public const string CuentasDesdeSp = @"
IF OBJECT_ID(N'bx.sp_catalogo_cuentas_contables', N'P') IS NOT NULL
    EXEC [bx].[sp_catalogo_cuentas_contables];
ELSE
    SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS INT) AS [banco_id], CAST(NULL AS NVARCHAR(80)) AS [numero_cuenta], CAST(NULL AS NVARCHAR(200)) AS [descripcion] WHERE 1 = 0;";

        public const string CuentasDesdeVista = @"
IF OBJECT_ID(N'bx.vw_cuentas_contables_siigo', N'V') IS NOT NULL
    SELECT [Id] AS [id], [BancoId] AS [banco_id], [NumeroCuenta] AS [numero_cuenta], [Descripcion] AS [descripcion]
    FROM [bx].[vw_cuentas_contables_siigo];
ELSE
    SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS INT) AS [banco_id], CAST(NULL AS NVARCHAR(80)) AS [numero_cuenta], CAST(NULL AS NVARCHAR(200)) AS [descripcion] WHERE 1 = 0;";

        public const string CuentasDesdeTabla = @"
IF OBJECT_ID(N'bx.cuentas_contables', N'U') IS NOT NULL
    SELECT [id], [banco_id], [numero_cuenta], [descripcion]
    FROM [bx].[cuentas_contables];
ELSE
    SELECT CAST(NULL AS INT) AS [id], CAST(NULL AS INT) AS [banco_id], CAST(NULL AS NVARCHAR(80)) AS [numero_cuenta], CAST(NULL AS NVARCHAR(200)) AS [descripcion] WHERE 1 = 0;";
    }
}
