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
            // Fallback para ambientes donde el SP no existe: sincroniza desde vistas bx.vw_*.
            const string sqlFallback = @"
                INSERT INTO bx.bancos (nombre)
                SELECT vb.Nombre
                FROM bx.vw_bancos_siigo vb
                LEFT JOIN bx.bancos b ON b.nombre = vb.Nombre
                WHERE b.id IS NULL;

                UPDATE b
                SET b.nombre = vb.Nombre
                FROM bx.bancos b
                INNER JOIN bx.vw_bancos_siigo vb ON vb.Nombre = b.nombre;

                INSERT INTO bx.cuentas_contables (banco_id, numero_cuenta, descripcion)
                SELECT b.id, vc.NumeroCuenta, vc.Descripcion
                FROM bx.vw_cuentas_contables_siigo vc
                INNER JOIN bx.vw_bancos_siigo vb ON vb.Id = vc.BancoId
                INNER JOIN bx.bancos b ON b.nombre = vb.Nombre
                LEFT JOIN bx.cuentas_contables cc
                    ON cc.banco_id = b.id
                   AND cc.numero_cuenta = vc.NumeroCuenta
                WHERE cc.id IS NULL;

                UPDATE cc
                SET cc.descripcion = vc.Descripcion
                FROM bx.cuentas_contables cc
                INNER JOIN bx.bancos b ON b.id = cc.banco_id
                INNER JOIN bx.vw_bancos_siigo vb ON vb.Nombre = b.nombre
                INNER JOIN bx.vw_cuentas_contables_siigo vc
                    ON vc.BancoId = vb.Id
                   AND vc.NumeroCuenta = cc.numero_cuenta
                WHERE ISNULL(cc.descripcion, '') <> ISNULL(vc.Descripcion, '');

                SET IDENTITY_INSERT bx.vendedores ON;

                INSERT INTO bx.vendedores (id, nombre)
                SELECT vv.Id, vv.Nombre
                FROM bx.vw_vendedores_siigo vv
                LEFT JOIN bx.vendedores v ON v.id = vv.Id
                WHERE v.id IS NULL;

                SET IDENTITY_INSERT bx.vendedores OFF;

                UPDATE v
                SET v.nombre = vv.Nombre
                FROM bx.vendedores v
                INNER JOIN bx.vw_vendedores_siigo vv ON vv.Id = v.id
                WHERE ISNULL(v.nombre, '') <> ISNULL(vv.Nombre, '');
            ";

            await contexto.Database.ExecuteSqlRawAsync(sqlFallback, cancellationToken);
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
