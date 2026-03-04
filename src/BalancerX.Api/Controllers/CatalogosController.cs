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
        var bancos = await contexto.Database
            .SqlQueryRaw<BancoCatalogoResponse>("SELECT Id, Nombre FROM bx.vw_bancos_siigo")
            .OrderBy(x => x.Nombre)
            .ToListAsync(cancellationToken);

        return Ok(bancos);
    }

    [HttpGet("bancos/{bancoId:int}/cuentas-contables")]
    public async Task<IActionResult> ListarCuentasPorBanco([FromRoute] int bancoId, CancellationToken cancellationToken)
    {
        var cuentas = await contexto.Database
            .SqlQueryRaw<CuentaContableCatalogoResponse>("SELECT Id, BancoId, NumeroCuenta, Descripcion FROM bx.vw_cuentas_contables_siigo")
            .Where(x => x.BancoId == bancoId)
            .OrderBy(x => x.NumeroCuenta)
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
        var vendedores = await contexto.Database
            .SqlQueryRaw<ItemCatalogoResponse>("SELECT Id, Nombre FROM bx.vw_vendedores_siigo")
            .OrderBy(x => x.Nombre)
            .ToListAsync(cancellationToken);

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
}
