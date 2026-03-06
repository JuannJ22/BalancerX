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
        return usuarios.Select(x => new UsuarioAdminResponse(
            x.Id,
            x.UsuarioNombre,
            x.Roles.FirstOrDefault()?.RolId ?? 0,
            x.Roles.FirstOrDefault()?.Rol?.Nombre ?? string.Empty,
            x.Activo,
            x.FirmaElectronica ?? string.Empty,
            x.PuntoVentaAsignadoId)).ToList();
    }

    public async Task<UsuarioAdminResponse> CrearAsync(CrearUsuarioRequest request, CancellationToken cancellationToken)
    {
        if (string.IsNullOrWhiteSpace(request.Usuario) || string.IsNullOrWhiteSpace(request.Password) || request.RolId <= 0)
            throw new InvalidOperationException("Usuario, password y rolId son obligatorios.");

        var rol = await usuarioRepositorio.ObtenerRolPorIdAsync(request.RolId, cancellationToken)
            ?? throw new InvalidOperationException("Rol no encontrado.");

        var rolNormalizado = rol.Nombre.Trim().ToUpperInvariant();
        if (rolNormalizado == "AUXILIAR" && (!request.PuntoVentaId.HasValue || request.PuntoVentaId.Value <= 0))
            throw new InvalidOperationException("Para usuarios AUXILIAR debe asignar un punto de venta.");

        var usuario = new Usuario
        {
            UsuarioNombre = request.Usuario,
            PasswordHash = request.Password,
            PinAdminHash = request.PinAdmin ?? string.Empty,
            Activo = true,
            FirmaElectronica = request.FirmaElectronica ?? string.Empty,
            PuntoVentaAsignadoId = request.PuntoVentaId
        };

        var creado = await usuarioRepositorio.CrearUsuarioAsync(usuario, request.RolId, cancellationToken);
        return new UsuarioAdminResponse(
            creado.Id,
            creado.UsuarioNombre,
            creado.Roles.FirstOrDefault()?.RolId ?? request.RolId,
            creado.Roles.FirstOrDefault()?.Rol?.Nombre ?? rol.Nombre,
            creado.Activo,
            creado.FirmaElectronica ?? string.Empty,
            creado.PuntoVentaAsignadoId);
    }


    public async Task<UsuarioAdminResponse> ActualizarRolAsync(int usuarioId, ActualizarRolUsuarioRequest request, CancellationToken cancellationToken)
    {
        if (request.RolId <= 0)
            throw new InvalidOperationException("rolId es obligatorio.");

        var actualizado = await usuarioRepositorio.ActualizarRolUsuarioAsync(usuarioId, request.RolId, cancellationToken);
        return new UsuarioAdminResponse(
            actualizado.Id,
            actualizado.UsuarioNombre,
            actualizado.Roles.FirstOrDefault()?.RolId ?? request.RolId,
            actualizado.Roles.FirstOrDefault()?.Rol?.Nombre ?? string.Empty,
            actualizado.Activo,
            actualizado.FirmaElectronica ?? string.Empty,
            actualizado.PuntoVentaAsignadoId);
    }

    public Task<bool> EliminarAsync(int usuarioId, CancellationToken cancellationToken)
        => usuarioRepositorio.EliminarUsuarioAsync(usuarioId, cancellationToken);
}
