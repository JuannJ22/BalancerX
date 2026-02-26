using System;
using BalancerX.Application.Contratos;
using BalancerX.Domain.Entidades;
using BalancerX.Infrastructure.Datos;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;

namespace BalancerX.Infrastructure.Repositorios;

public class UsuarioRepositorio : IUsuarioRepositorio
{
    private readonly BalancerXDbContext contexto;
    private readonly PasswordHasher<string> passwordHasher = new();

    public UsuarioRepositorio(BalancerXDbContext contexto) => this.contexto = contexto;

    public Task<Usuario?> ObtenerPorUsuarioNombreAsync(string usuarioNombre, CancellationToken cancellationToken)
        => contexto.Usuarios.Include(x => x.Roles).ThenInclude(x => x.Rol).FirstOrDefaultAsync(x => x.UsuarioNombre == usuarioNombre && x.Activo, cancellationToken);

    public Task<Usuario?> ObtenerPorIdAsync(int usuarioId, CancellationToken cancellationToken)
        => contexto.Usuarios.Include(x => x.Roles).ThenInclude(x => x.Rol).FirstOrDefaultAsync(x => x.Id == usuarioId && x.Activo, cancellationToken);

    public Task<bool> ValidarPinAdminAsync(int usuarioId, string pinAdminPlano, CancellationToken cancellationToken)
    {
        var hash = contexto.Usuarios.Where(x => x.Id == usuarioId).Select(x => x.PinAdminHash).FirstOrDefault();
        var resultado = false;
        if (!string.IsNullOrWhiteSpace(hash))
        {
            resultado = hash.StartsWith("{PLAIN}", StringComparison.Ordinal)
                ? string.Equals(hash[7..], pinAdminPlano, StringComparison.Ordinal)
                : passwordHasher.VerifyHashedPassword("PIN", hash, pinAdminPlano) != PasswordVerificationResult.Failed;
        }
        return Task.FromResult(resultado);
    }
}
