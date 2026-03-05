using BalancerX.Application.Contratos;
using System.Data;
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
            .OrderBy(x => x.Nombre)
            .Select(x => new ItemCatalogoResponse { Id = x.Id, Nombre = x.Nombre })
            .ToListAsync(cancellationToken);

        return Ok(bancos);
    }

    [HttpGet("bancos/{bancoId:int}/cuentas-contables")]
    public async Task<IActionResult> ListarCuentasPorBanco([FromRoute] int bancoId, CancellationToken cancellationToken)
    {
        await catalogosSyncServicio.SincronizarAsync(cancellationToken);

        var cuentas = await ConsultarCuentasPorBancoResilienteAsync(bancoId, cancellationToken);
        return Ok(cuentas);
    }

    private async Task<List<CuentaContableCatalogoResponse>> ConsultarCuentasPorBancoResilienteAsync(int bancoId, CancellationToken cancellationToken)
    {
        const string sql = @"
BEGIN TRY
    SELECT
        [Id] AS [id],
        [BancoId] AS [banco_id],
        [NumeroCuenta] AS [numero_cuenta],
        [Descripcion] AS [descripcion]
    FROM [bx].[vw_cuentas_contables_siigo]
    WHERE [BancoId] = @bancoId
    ORDER BY [NumeroCuenta];
END TRY
BEGIN CATCH
    IF OBJECT_ID(N'bx.cuentas_contables', N'U') IS NOT NULL
        SELECT [id], [banco_id], [numero_cuenta], [descripcion]
        FROM [bx].[cuentas_contables]
        WHERE [banco_id] = @bancoId
        ORDER BY [numero_cuenta];
    ELSE
        SELECT
            CAST(NULL AS INT) AS [id],
            CAST(NULL AS INT) AS [banco_id],
            CAST(NULL AS NVARCHAR(80)) AS [numero_cuenta],
            CAST(NULL AS NVARCHAR(200)) AS [descripcion]
        WHERE 1 = 0;
END CATCH";

        try
        {
            await using var connection = contexto.Database.GetDbConnection();
            if (connection.State != ConnectionState.Open)
            {
                await connection.OpenAsync(cancellationToken);
            }

            await using var command = connection.CreateCommand();
            command.CommandText = sql;
            command.CommandType = CommandType.Text;
            command.CommandTimeout = 30;

            var param = command.CreateParameter();
            param.ParameterName = "@bancoId";
            param.Value = bancoId;
            command.Parameters.Add(param);

            var cuentas = new List<CuentaContableCatalogoResponse>();
            await using var reader = await command.ExecuteReaderAsync(cancellationToken);
            while (await reader.ReadAsync(cancellationToken))
            {
                cuentas.Add(new CuentaContableCatalogoResponse
                {
                    Id = reader.GetInt32(reader.GetOrdinal("id")),
                    BancoId = reader.GetInt32(reader.GetOrdinal("banco_id")),
                    NumeroCuenta = reader.GetString(reader.GetOrdinal("numero_cuenta")),
                    Descripcion = reader.GetString(reader.GetOrdinal("descripcion"))
                });
            }

            return cuentas;
        }
        catch
        {
            return [];
        }
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

        var vendedores = await contexto.Vendedores
            .OrderBy(x => x.Nombre)
            .Select(x => new ItemCatalogoResponse { Id = x.Id, Nombre = x.Nombre })
            .ToListAsync(cancellationToken);

        return Ok(vendedores);
    }

    public class ItemCatalogoResponse
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
