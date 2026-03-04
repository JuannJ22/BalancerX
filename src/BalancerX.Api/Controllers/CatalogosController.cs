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

        var bancos = await contexto.Bancos
            .Select(x => new BancoCatalogoResponse { Id = x.Id, Nombre = x.Nombre })
            .OrderBy(x => x.Nombre)
            .ToListAsync(cancellationToken);

        return Ok(bancos);
    }

    [HttpGet("bancos/{bancoId:int}/cuentas-contables")]
    public async Task<IActionResult> ListarCuentasPorBanco([FromRoute] int bancoId, CancellationToken cancellationToken)
    {
        await catalogosSyncServicio.SincronizarAsync(cancellationToken);

        var cuentas = await contexto.CuentasContables
            .Select(x => new CuentaContableCatalogoResponse
            {
                Id = x.Id,
                BancoId = x.BancoId,
                NumeroCuenta = x.NumeroCuenta,
                Descripcion = x.Descripcion
            })
            .Where(x => x.BancoId == bancoId)
            .OrderBy(x => x.NumeroCuenta)
            .ToListAsync(cancellationToken);

        return Ok(cuentas);
    }

    [HttpGet("puntos-venta")]
    public async Task<IActionResult> ListarPuntosVenta(CancellationToken cancellationToken)
    {
        await catalogosSyncServicio.SincronizarAsync(cancellationToken);

        var puntos = await contexto.PuntosVenta
            .OrderBy(x => x.Nombre)
            .Select(x => new ItemCatalogoResponse { Id = x.Id, Nombre = x.Nombre })
            .ToListAsync(cancellationToken);

        return Ok(puntos);
    }

    [HttpGet("vendedores")]
    public async Task<IActionResult> ListarVendedores(CancellationToken cancellationToken)
    {
        await catalogosSyncServicio.SincronizarAsync(cancellationToken);

        var vendedores = await contexto.Vendedores
            .Select(x => new ItemCatalogoResponse { Id = x.Id, Nombre = x.Nombre })
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
