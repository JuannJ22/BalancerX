using BalancerX.Application.Contratos;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;

namespace BalancerX.Api.Controllers;

[ApiController]
[Route("api/auth")]
public class AuthController : ControllerBase
{
    private readonly IUsuarioRepositorio usuarioRepositorio;
    private readonly IJwtTokenServicio jwtTokenServicio;
    private readonly PasswordHasher<string> passwordHasher = new();

    public AuthController(IUsuarioRepositorio usuarioRepositorio, IJwtTokenServicio jwtTokenServicio)
    {
        this.usuarioRepositorio = usuarioRepositorio;
        this.jwtTokenServicio = jwtTokenServicio;
    }

    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginRequest loginRequest, CancellationToken cancellationToken)
    {
        var usuario = await usuarioRepositorio.ObtenerPorUsuarioNombreAsync(loginRequest.Usuario, cancellationToken);
        if (usuario is null) return Unauthorized(new ProblemDetails { Title = "Credenciales inválidas", Status = 401 });

        var valido = passwordHasher.VerifyHashedPassword("PWD", usuario.PasswordHash, loginRequest.Password) != PasswordVerificationResult.Failed;
        if (!valido) return Unauthorized(new ProblemDetails { Title = "Credenciales inválidas", Status = 401 });

        return Ok(new { token = jwtTokenServicio.GenerarToken(usuario) });
    }
}

public record LoginRequest(string Usuario, string Password);
