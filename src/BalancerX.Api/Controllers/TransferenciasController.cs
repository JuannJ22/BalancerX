using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using BalancerX.Application.DTOs;
using BalancerX.Application.Servicios;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace BalancerX.Api.Controllers;

[ApiController]
[Authorize]
[Route("api/transferencias")]
public class TransferenciasController : ControllerBase
{
    private readonly TransferenciaServicio transferenciaServicio;

    public TransferenciasController(TransferenciaServicio transferenciaServicio) => this.transferenciaServicio = transferenciaServicio;

    [HttpPost]
    [Authorize(Roles = "ADMIN,TESORERIA,AUXILIAR")]
    public async Task<IActionResult> Crear([FromBody] CrearTransferenciaRequest crearTransferenciaRequest, CancellationToken cancellationToken)
    {
        if (crearTransferenciaRequest.Monto <= 0) return BadRequest(new ProblemDetails { Title = "El monto debe ser mayor a 0", Status = 400 });
        var respuesta = await transferenciaServicio.CrearAsync(crearTransferenciaRequest, ObtenerUsuarioId(), cancellationToken);
        return CreatedAtAction(nameof(ObtenerPdf), new { id = respuesta.Id }, respuesta);
    }

    [HttpGet]
    [Authorize(Roles = "ADMIN,TESORERIA,AUXILIAR")]
    public async Task<IActionResult> Listar([FromQuery] FiltroTransferenciaRequest filtroTransferenciaRequest, CancellationToken cancellationToken)
        => Ok(await transferenciaServicio.ListarAsync(filtroTransferenciaRequest, cancellationToken));

    [HttpPost("{id:long}/archivo")]
    [Authorize(Roles = "ADMIN,TESORERIA,AUXILIAR")]
    public async Task<IActionResult> SubirPdf([FromRoute] long id, IFormFile archivo, CancellationToken cancellationToken)
    {
        if (archivo is null || archivo.Length == 0) return BadRequest(new ProblemDetails { Title = "Archivo vacío", Status = 400 });
        if (!archivo.ContentType.Contains("pdf") && !archivo.FileName.EndsWith(".pdf", StringComparison.OrdinalIgnoreCase))
            return BadRequest(new ProblemDetails { Title = "Solo se permite PDF", Status = 400 });

        await using var stream = archivo.OpenReadStream();
        var respuesta = await transferenciaServicio.SubirPdfAsync(id, archivo.FileName, stream, ObtenerUsuarioId(), cancellationToken);
        return Ok(respuesta);
    }

    [HttpGet("{id:long}/archivo")]
    [Authorize(Roles = "ADMIN,TESORERIA,AUXILIAR")]
    public async Task<IActionResult> ObtenerPdf([FromRoute] long id, CancellationToken cancellationToken)
    {
        var resultado = await transferenciaServicio.DescargarPdfAsync(id, cancellationToken);
        return File(resultado.Contenido, "application/pdf", resultado.NombreOriginal);
    }

    [HttpPost("{id:long}/print")]
    [Authorize(Roles = "ADMIN,TESORERIA")]
    public async Task<IActionResult> Imprimir([FromRoute] long id, CancellationToken cancellationToken)
    {
        await transferenciaServicio.ImprimirAsync(id, ObtenerUsuarioId(), cancellationToken);
        return Ok(new { mensaje = "Impresión completada" });
    }

    [HttpPost("{id:long}/reprint")]
    [Authorize(Roles = "ADMIN")]
    public async Task<IActionResult> Reimprimir([FromRoute] long id, [FromBody] ReimpresionRequest reimpresionRequest, CancellationToken cancellationToken)
    {
        await transferenciaServicio.ReimprimirAsync(id, reimpresionRequest, ObtenerUsuarioId(), cancellationToken);
        return Ok(new { mensaje = "Reimpresión completada" });
    }

    private int ObtenerUsuarioId()
    {
        var claim = User.FindFirstValue(ClaimTypes.NameIdentifier) ?? User.FindFirstValue(ClaimTypes.Name) ?? User.FindFirstValue(ClaimTypes.Sid) ?? User.FindFirstValue(JwtRegisteredClaimNames.Sub);
        return int.Parse(claim!);
    }
}
