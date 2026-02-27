using BalancerX.Application.DTOs;
using BalancerX.Application.Servicios;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace BalancerX.Api.Controllers;

[ApiController]
[Authorize(Roles = "ADMIN")]
[Route("api/usuarios")]
public class UsuariosController : ControllerBase
{
    private readonly UsuarioAdminServicio usuarioAdminServicio;

    public UsuariosController(UsuarioAdminServicio usuarioAdminServicio)
    {
        this.usuarioAdminServicio = usuarioAdminServicio;
    }

    [HttpGet]
    public async Task<IActionResult> Listar(CancellationToken cancellationToken)
        => Ok(await usuarioAdminServicio.ListarAsync(cancellationToken));

    [HttpPost]
    public async Task<IActionResult> Crear([FromBody] CrearUsuarioRequest request, CancellationToken cancellationToken)
    {
        var usuario = await usuarioAdminServicio.CrearAsync(request, cancellationToken);
        return Ok(usuario);
    }

    [HttpDelete("{id:int}")]
    public async Task<IActionResult> Eliminar([FromRoute] int id, CancellationToken cancellationToken)
    {
        var eliminado = await usuarioAdminServicio.EliminarAsync(id, cancellationToken);
        if (!eliminado) return NotFound(new ProblemDetails { Title = "Usuario no encontrado", Status = 404 });
        return Ok(new { usuarioId = id, eliminado = true });
    }
}
