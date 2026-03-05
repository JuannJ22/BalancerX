using BalancerX.Application.Contratos;
using BalancerX.Domain.Entidades;
using BalancerX.Infrastructure.Datos;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Memory;

namespace BalancerX.Api.Controllers;

[ApiController]
[Authorize(Roles = "ADMIN,TESORERIA,AUXILIAR")]
[Route("api/catalogos")]
public class CatalogosController : ControllerBase
{
    private readonly BalancerXDbContext contexto;
    private readonly ICatalogosSyncServicio catalogosSyncServicio;
    private readonly IMemoryCache cache;

    private static readonly TimeSpan CatalogoCacheTtl = TimeSpan.FromMinutes(5);

    public CatalogosController(BalancerXDbContext contexto, ICatalogosSyncServicio catalogosSyncServicio, IMemoryCache cache)
    {
        this.contexto = contexto;
        this.catalogosSyncServicio = catalogosSyncServicio;
        this.cache = cache;
    }

    [HttpGet("bancos")]
    public async Task<IActionResult> ListarBancos(CancellationToken cancellationToken)
    {
        await catalogosSyncServicio.SincronizarAsync(cancellationToken);
        var bancos = await cache.GetOrCreateAsync(CacheKeys.Bancos, async entrada =>
        {
            entrada.AbsoluteExpirationRelativeToNow = CatalogoCacheTtl;
            return await ConsultarItemsConFallbackAsync(SqlCatalogos.Bancos, cancellationToken);
        }) ?? [];

        var bancos = await contexto.Bancos
            .OrderBy(x => x.Nombre)
            .Select(x => new ItemCatalogoResponse { Id = x.Id, Nombre = x.Nombre })
            .ToListAsync(cancellationToken);

        return Ok(bancos);
    }

    [HttpGet("bancos/{bancoId:int}/cuentas-contables")]
    public async Task<IActionResult> ListarCuentasPorBanco([FromRoute] int bancoId, CancellationToken cancellationToken)
    {
        await catalogosSyncServicio.SincronizarAsync(cancellationToken);
        var cuentas = await cache.GetOrCreateAsync(CacheKeys.Cuentas, async entrada =>
        {
            entrada.AbsoluteExpirationRelativeToNow = CatalogoCacheTtl;
            return await ConsultarCuentasConFallbackAsync(SqlCatalogos.Cuentas, cancellationToken);
        }) ?? [];

        var cuentas = await contexto.CuentasContables
            .Where(x => x.BancoId == bancoId)
            .OrderBy(x => x.NumeroCuenta)
            .Select(x => new CuentaContableCatalogoResponse
            {
                Id = x.Id,
                BancoId = x.BancoId,
                NumeroCuenta = x.NumeroCuenta,
                Descripcion = x.Descripcion
            })
            .ToListAsync(cancellationToken);

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
        var vendedores = await cache.GetOrCreateAsync(CacheKeys.Vendedores, async entrada =>
        {
            entrada.AbsoluteExpirationRelativeToNow = CatalogoCacheTtl;
            return await ConsultarItemsConFallbackAsync(SqlCatalogos.Vendedores, cancellationToken);
        }) ?? [];

        var vendedores = await contexto.Vendedores
            .OrderBy(x => x.Nombre)
            .Select(x => new ItemCatalogoResponse { Id = x.Id, Nombre = x.Nombre })
            .ToListAsync(cancellationToken);

    private async Task<List<ItemCatalogoResponse>> ConsultarItemsConFallbackAsync(string[] sqlCandidates, CancellationToken cancellationToken)
    {
        foreach (var sql in sqlCandidates)
        {
            var result = await EjecutarItemsAsync(sql, cancellationToken);
            if (result.Count > 0) return result;
        }

        return [];
    }

    private async Task<List<CuentaContableCatalogoResponse>> ConsultarCuentasConFallbackAsync(string[] sqlCandidates, CancellationToken cancellationToken)
    {
        foreach (var sql in sqlCandidates)
        {
            var result = await EjecutarCuentasAsync(sql, cancellationToken);
            if (result.Count > 0) return result;
        }

        return [];
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
        public static readonly string[] Vendedores =
        {
            "EXEC bx.sp_catalogo_vendedores",
            "SELECT [Id] AS [id], [Nombre] AS [nombre] FROM [bx].[vw_vendedores_siigo]",
            "SELECT [id], [nombre] FROM [bx].[vendedores]"
        };

        public static readonly string[] Bancos =
        {
            "EXEC bx.sp_catalogo_bancos",
            "SELECT [Id] AS [id], [Nombre] AS [nombre] FROM [bx].[vw_bancos_siigo]",
            "SELECT [id], [nombre] FROM [bx].[bancos]"
        };

        public static readonly string[] Cuentas =
        {
            "EXEC bx.sp_catalogo_cuentas_contables",
            "SELECT [Id] AS [id], [BancoId] AS [banco_id], [NumeroCuenta] AS [numero_cuenta], [Descripcion] AS [descripcion] FROM [bx].[vw_cuentas_contables_siigo]",
            "SELECT [id], [banco_id], [numero_cuenta], [descripcion] FROM [bx].[cuentas_contables]"
        };
    }
}
