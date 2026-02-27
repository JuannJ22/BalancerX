using BalancerX.Application.Contratos;
using BalancerX.Application.DTOs;

namespace BalancerX.Application.Servicios;

public class UsuarioPerfilServicio
{
    private readonly IUsuarioRepositorio usuarioRepositorio;
    private readonly IFirmaElectronicaServicio firmaElectronicaServicio;

    public UsuarioPerfilServicio(IUsuarioRepositorio usuarioRepositorio, IFirmaElectronicaServicio firmaElectronicaServicio)
    {
        this.usuarioRepositorio = usuarioRepositorio;
        this.firmaElectronicaServicio = firmaElectronicaServicio;
    }

    public Task<bool> CambiarPasswordAsync(int usuarioId, CambiarPasswordRequest request, CancellationToken cancellationToken)
        => usuarioRepositorio.CambiarPasswordAsync(usuarioId, request.PasswordActual, request.PasswordNueva, cancellationToken);

    public async Task<ActualizarFirmaResponse> ActualizarFirmaAsync(int usuarioId, string nombreArchivo, Stream contenido, CancellationToken cancellationToken)
    {
        var rutaFirma = await firmaElectronicaServicio.GuardarFirmaAsync(usuarioId, nombreArchivo, contenido, cancellationToken);
        var usuario = await usuarioRepositorio.ActualizarFirmaElectronicaAsync(usuarioId, rutaFirma, cancellationToken);
        return new ActualizarFirmaResponse(usuario.Id, usuario.FirmaElectronica);
    }
}
