using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using BalancerX.Application.DTOs;
using BalancerX.Application.Servicios;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace BalancerX.Api.Controllers;

[ApiController]
[Authorize]
[Route("api/perfil")]
public class PerfilController : ControllerBase
{
    private readonly UsuarioPerfilServicio usuarioPerfilServicio;

    public PerfilController(UsuarioPerfilServicio usuarioPerfilServicio)
    {
        this.usuarioPerfilServicio = usuarioPerfilServicio;
    }

    [HttpPut("password")]
    public async Task<IActionResult> CambiarPassword([FromBody] CambiarPasswordRequest request, CancellationToken cancellationToken)
    {
        var ok = await usuarioPerfilServicio.CambiarPasswordAsync(ObtenerUsuarioId(), request, cancellationToken);
        if (!ok) return BadRequest(new ProblemDetails { Title = "Password actual inválido", Status = 400 });
        return Ok(new { mensaje = "Password actualizado" });
    }

    [HttpPut("firma")]
    public async Task<IActionResult> ActualizarFirma(IFormFile firma, CancellationToken cancellationToken)
    {
        if (firma is null || firma.Length == 0) return BadRequest(new ProblemDetails { Title = "Firma vacía", Status = 400 });
        await using var stream = firma.OpenReadStream();
        var respuesta = await usuarioPerfilServicio.ActualizarFirmaAsync(ObtenerUsuarioId(), firma.FileName, stream, cancellationToken);
        return Ok(respuesta);
    }

    private int ObtenerUsuarioId()
    {
        var claim = User.FindFirstValue(ClaimTypes.NameIdentifier) ?? User.FindFirstValue(ClaimTypes.Name) ?? User.FindFirstValue(ClaimTypes.Sid) ?? User.FindFirstValue(JwtRegisteredClaimNames.Sub);
        return int.Parse(claim!);
    }
}
