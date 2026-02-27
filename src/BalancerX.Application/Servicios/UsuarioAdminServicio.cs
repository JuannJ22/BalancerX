using BalancerX.Application.Contratos;
using BalancerX.Application.DTOs;
using BalancerX.Domain.Entidades;

namespace BalancerX.Application.Servicios;

public class UsuarioAdminServicio
{
    private readonly IUsuarioRepositorio usuarioRepositorio;

    public UsuarioAdminServicio(IUsuarioRepositorio usuarioRepositorio)
    {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public async Task<List<UsuarioAdminResponse>> ListarAsync(CancellationToken cancellationToken)
    {
        var usuarios = await usuarioRepositorio.ListarUsuariosAsync(cancellationToken);
        return usuarios.Select(x => new UsuarioAdminResponse(x.Id, x.UsuarioNombre, x.Roles.FirstOrDefault()?.Rol?.Nombre ?? string.Empty, x.Activo, x.FirmaElectronica)).ToList();
    }

    public async Task<UsuarioAdminResponse> CrearAsync(CrearUsuarioRequest request, CancellationToken cancellationToken)
    {
        if (string.IsNullOrWhiteSpace(request.Usuario) || string.IsNullOrWhiteSpace(request.Password) || string.IsNullOrWhiteSpace(request.Rol))
            throw new InvalidOperationException("Usuario, password y rol son obligatorios.");

        var usuario = new Usuario
        {
            UsuarioNombre = request.Usuario,
            PasswordHash = request.Password,
            PinAdminHash = request.PinAdmin,
            Activo = true,
            FirmaElectronica = request.FirmaElectronica
        };

        var creado = await usuarioRepositorio.CrearUsuarioAsync(usuario, request.Rol, cancellationToken);
        return new UsuarioAdminResponse(creado.Id, creado.UsuarioNombre, creado.Roles.FirstOrDefault()?.Rol?.Nombre ?? request.Rol, creado.Activo, creado.FirmaElectronica);
    }

    public Task<bool> EliminarAsync(int usuarioId, CancellationToken cancellationToken)
        => usuarioRepositorio.EliminarUsuarioAsync(usuarioId, cancellationToken);
}
