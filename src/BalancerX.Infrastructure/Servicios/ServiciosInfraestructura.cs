using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;
using BalancerX.Application.Contratos;
using BalancerX.Domain.Entidades;
using BalancerX.Infrastructure.Datos;
using BalancerX.Infrastructure.Repositorios;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.IdentityModel.Tokens;

namespace BalancerX.Infrastructure.Servicios;

public static class ServiciosInfraestructura
{
    public static IServiceCollection AgregarInfraestructura(this IServiceCollection servicios, IConfiguration configuracion)
    {
        servicios.AddDbContext<BalancerXDbContext>(opciones => opciones.UseSqlServer(configuracion.GetConnectionString("SqlServer")));
        servicios.AddScoped<ITransferenciaRepositorio, TransferenciaRepositorio>();
        servicios.AddScoped<IUsuarioRepositorio, UsuarioRepositorio>();
        servicios.AddScoped<IJwtTokenServicio, JwtTokenServicio>();
        servicios.AddScoped<IArchivoSeguroServicio, ArchivoSeguroServicio>();
        servicios.AddScoped<IPrintService, StubPrintService>();
        servicios.AddScoped<IAdaptadorImpresionWindows, AdaptadorImpresionWindows>();
        return servicios;
    }
}

public interface IAdaptadorImpresionWindows
{
    Task<bool> ImprimirPdfAsync(string rutaArchivo, CancellationToken cancellationToken);
}

public class AdaptadorImpresionWindows : IAdaptadorImpresionWindows
{
    public Task<bool> ImprimirPdfAsync(string rutaArchivo, CancellationToken cancellationToken)
    {
        // Punto de extensión para invocar motor externo Windows (SumatraPDF, Adobe Reader silent print, etc.)
        return Task.FromResult(true);
    }
}

public class StubPrintService : IPrintService
{
    public Task<bool> ImprimirTransferenciaAsync(long transferenciaId, string rutaArchivo, CancellationToken cancellationToken) => Task.FromResult(true);
}

public class JwtTokenServicio : IJwtTokenServicio
{
    private readonly IConfiguration configuracion;

    public JwtTokenServicio(IConfiguration configuracion) => this.configuracion = configuracion;

    public string GenerarToken(Usuario usuario)
    {
        var clave = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(configuracion["Jwt:Key"]!));
        var credenciales = new SigningCredentials(clave, SecurityAlgorithms.HmacSha256);
        var claims = new List<Claim>
        {
            new(JwtRegisteredClaimNames.Sub, usuario.Id.ToString()),
            new(ClaimTypes.NameIdentifier, usuario.Id.ToString()),
            new(JwtRegisteredClaimNames.UniqueName, usuario.UsuarioNombre)
        };
        claims.AddRange(usuario.Roles.Select(rol => new Claim(ClaimTypes.Role, rol.Rol.Nombre)));

        var token = new JwtSecurityToken(
            issuer: configuracion["Jwt:Issuer"],
            audience: configuracion["Jwt:Audience"],
            claims: claims,
            expires: DateTime.UtcNow.AddHours(8),
            signingCredentials: credenciales);
        return new JwtSecurityTokenHandler().WriteToken(token);
    }
}

public class ArchivoSeguroServicio : IArchivoSeguroServicio
{
    private readonly string rutaRaiz = @"D:\BalancerX_Secure\Transferencias";

    public async Task<TransferenciaArchivo> GuardarPdfAsync(long transferenciaId, string nombreOriginal, Stream contenidoStream, int subidoPorUsuarioId, CancellationToken cancellationToken)
    {
        var ahora = DateTime.UtcNow;
        var carpeta = Path.Combine(rutaRaiz, ahora.Year.ToString(), ahora.Month.ToString("00"));
        Directory.CreateDirectory(carpeta);

        var nombreInterno = $"transferencia_{transferenciaId}_{Guid.NewGuid():N}.pdf";
        var rutaInterna = Path.Combine(carpeta, nombreInterno);

        await using var archivoSalida = File.Create(rutaInterna);
        await contenidoStream.CopyToAsync(archivoSalida, cancellationToken);

        await using var lectura = File.OpenRead(rutaInterna);
        var shaBytes = await SHA256.HashDataAsync(lectura, cancellationToken);
        var sha256 = Convert.ToHexString(shaBytes);
        var tamano = new FileInfo(rutaInterna).Length;

        return new TransferenciaArchivo
        {
            TransferenciaId = transferenciaId,
            NombreOriginal = nombreOriginal,
            RutaInterna = rutaInterna,
            Sha256 = sha256,
            TamanoBytes = tamano,
            SubidoPorUsuarioId = subidoPorUsuarioId,
            SubidoEnUtc = ahora
        };
    }

    public Task<(Stream Contenido, string NombreOriginal)> ObtenerPdfAsync(TransferenciaArchivo transferenciaArchivo, CancellationToken cancellationToken)
    {
        Stream contenido = File.OpenRead(transferenciaArchivo.RutaInterna);
        return Task.FromResult((contenido, transferenciaArchivo.NombreOriginal));
    }
}
