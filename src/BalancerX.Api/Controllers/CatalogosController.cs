using BalancerX.Infrastructure.Datos;
using Microsoft.Data.SqlClient;
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
            .Select(x => new BancoCatalogoResponse { Id = x.Id, Nombre = x.Nombre })
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
            .Select(x => new CuentaContableCatalogoResponse { Id = x.Id, BancoId = x.BancoId, NumeroCuenta = x.NumeroCuenta, Descripcion = x.Descripcion })
            .ToListAsync(cancellationToken);

        return Ok(cuentas);
    }

    [HttpGet("puntos-venta")]
    public async Task<IActionResult> ListarPuntosVenta(CancellationToken cancellationToken)
    {
        var puntos = await contexto.PuntosVenta
            .OrderBy(x => x.Nombre)
            .Select(x => new ItemCatalogoResponse { Id = x.Id, Nombre = x.Nombre })
            .ToListAsync(cancellationToken);

        return Ok(puntos);
    }

    [HttpGet("vendedores")]
    public async Task<IActionResult> ListarVendedores(CancellationToken cancellationToken)
    {
        await SincronizarCatalogosSiigoAsync(cancellationToken);

        var vendedores = await contexto.Vendedores
            .OrderBy(x => x.Nombre)
            .Select(x => new ItemCatalogoResponse { Id = x.Id, Nombre = x.Nombre })
            .ToListAsync(cancellationToken);

        return Ok(vendedores);
    }

    private async Task SincronizarCatalogosSiigoAsync(CancellationToken cancellationToken)
    {
        try
        {
            await contexto.Database.ExecuteSqlRawAsync("EXEC bx.sp_sincronizar_catalogos_desde_siigo @BaseOrigen = N'SiigoCat'", cancellationToken);
        }
        catch (SqlException ex) when (ex.Number == 2812)
        {
            // Fallback solo cuando no existe el procedimiento en el ambiente.
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
}
