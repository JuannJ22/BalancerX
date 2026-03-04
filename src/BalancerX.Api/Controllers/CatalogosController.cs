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

    public CatalogosController(BalancerXDbContext contexto)
    {
        this.contexto = contexto;
    }

    [HttpGet("bancos")]
    public async Task<IActionResult> ListarBancos(CancellationToken cancellationToken)
    {
        await SincronizarCatalogosSiigoAsync(cancellationToken);

        var bancos = await contexto.Bancos
            .OrderBy(x => x.Nombre)
            .Select(x => new BancoCatalogoResponse(x.Id, x.Nombre))
            .ToListAsync(cancellationToken);

        return Ok(bancos);
    }

    [HttpGet("bancos/{bancoId:int}/cuentas-contables")]
    public async Task<IActionResult> ListarCuentasPorBanco([FromRoute] int bancoId, CancellationToken cancellationToken)
    {
        await SincronizarCatalogosSiigoAsync(cancellationToken);

        var cuentas = await contexto.CuentasContables
            .Where(x => x.BancoId == bancoId)
            .OrderBy(x => x.NumeroCuenta)
            .Select(x => new CuentaContableCatalogoResponse(x.Id, x.BancoId, x.NumeroCuenta, x.Descripcion))
            .ToListAsync(cancellationToken);

        return Ok(cuentas);
    }

    [HttpGet("puntos-venta")]
    public async Task<IActionResult> ListarPuntosVenta(CancellationToken cancellationToken)
    {
        var puntos = await contexto.PuntosVenta
            .OrderBy(x => x.Nombre)
            .Select(x => new ItemCatalogoResponse(x.Id, x.Nombre))
            .ToListAsync(cancellationToken);

        return Ok(puntos);
    }

    [HttpGet("vendedores")]
    public async Task<IActionResult> ListarVendedores(CancellationToken cancellationToken)
    {
        await SincronizarCatalogosSiigoAsync(cancellationToken);

        var vendedores = await contexto.Vendedores
            .OrderBy(x => x.Nombre)
            .Select(x => new ItemCatalogoResponse(x.Id, x.Nombre))
            .ToListAsync(cancellationToken);

        return Ok(vendedores);
    }

    private async Task SincronizarCatalogosSiigoAsync(CancellationToken cancellationToken)
    {
        try
        {
            await contexto.Database.ExecuteSqlRawAsync("EXEC bx.sp_sincronizar_catalogos_desde_siigo @BaseOrigen = N'SiigoCat'", cancellationToken);
        }
        catch (Exception)
        {
            // Fallback silencioso para ambientes donde el procedimiento no exista todavía.
        }
    }

    public record ItemCatalogoResponse(int Id, string Nombre);
    public record BancoCatalogoResponse(int Id, string Nombre);
    public record CuentaContableCatalogoResponse(int Id, int BancoId, string NumeroCuenta, string Descripcion);
}
