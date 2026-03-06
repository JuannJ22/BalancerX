using BalancerX.Application.DTOs;
using BalancerX.Domain.Entidades;

namespace BalancerX.Application.Contratos;

public interface ITransferenciaRepositorio
{
    Task<Transferencia> CrearAsync(Transferencia transferencia, CancellationToken cancellationToken);
    Task<Transferencia?> ObtenerPorIdAsync(long transferenciaId, CancellationToken cancellationToken);
    Task<Transferencia> ActualizarAsync(Transferencia transferencia, CancellationToken cancellationToken);
    Task<List<Transferencia>> ListarAsync(FiltroTransferenciaRequest filtroTransferenciaRequest, CancellationToken cancellationToken);
    Task<bool> MarcarImpresaPrimeraVezAsync(long transferenciaId, DateTime fechaUtc, CancellationToken cancellationToken);
    Task GuardarEventoImpresionAsync(EventoImpresion eventoImpresion, CancellationToken cancellationToken);
    Task GuardarEventoAuditoriaAsync(EventoAuditoria eventoAuditoria, CancellationToken cancellationToken);
    Task<TransferenciaArchivo> GuardarArchivoAsync(TransferenciaArchivo transferenciaArchivo, CancellationToken cancellationToken);
    Task ActualizarEstadoAsync(long transferenciaId, string estado, CancellationToken cancellationToken);
    Task<TransferenciaArchivo?> ObtenerArchivoPorTransferenciaAsync(long transferenciaId, CancellationToken cancellationToken);
    Task<List<TransferenciaArchivo>> ObtenerArchivosPorTransferenciaAsync(long transferenciaId, CancellationToken cancellationToken);
    Task<bool> EliminarArchivoPorTransferenciaAsync(long transferenciaId, CancellationToken cancellationToken);
    Task<bool> EliminarAsync(long transferenciaId, CancellationToken cancellationToken);
    Task<bool> ExistePuntoVentaAsync(int puntoVentaId, CancellationToken cancellationToken);
    Task<bool> ExisteVendedorAsync(int vendedorId, CancellationToken cancellationToken);
    Task<bool> ExisteBancoAsync(int bancoId, CancellationToken cancellationToken);
    Task<bool> ExisteCuentaContableEnBancoAsync(int cuentaContableId, int bancoId, CancellationToken cancellationToken);
    Task<string?> ObtenerNombrePuntoVentaAsync(int puntoVentaId, CancellationToken cancellationToken);
    Task<string?> ObtenerNombreVendedorAsync(int vendedorId, CancellationToken cancellationToken);
}



public interface ICatalogosSyncServicio
{
    Task SincronizarAsync(CancellationToken cancellationToken);
}

public interface IUsuarioRepositorio
{
    Task<Usuario?> ObtenerPorUsuarioNombreAsync(string usuarioNombre, CancellationToken cancellationToken);
    Task<Usuario?> ObtenerPorIdAsync(int usuarioId, CancellationToken cancellationToken);
    Task<bool> ValidarPinAdminAsync(int usuarioId, string pinAdminPlano, CancellationToken cancellationToken);
    Task<List<Usuario>> ListarUsuariosAsync(CancellationToken cancellationToken);
    Task<Usuario> CrearUsuarioAsync(Usuario usuario, int rolId, CancellationToken cancellationToken);
    Task<Usuario> ActualizarRolUsuarioAsync(int usuarioId, int rolId, CancellationToken cancellationToken);
    Task<Rol?> ObtenerRolPorIdAsync(int rolId, CancellationToken cancellationToken);
    Task<bool> EliminarUsuarioAsync(int usuarioId, CancellationToken cancellationToken);
    Task<bool> CambiarPasswordAsync(int usuarioId, string passwordActual, string passwordNueva, CancellationToken cancellationToken);
    Task<Usuario> ActualizarFirmaElectronicaAsync(int usuarioId, string firmaElectronica, CancellationToken cancellationToken);
}

public interface IJwtTokenServicio
{
    string GenerarToken(Usuario usuario);
}

public interface IArchivoSeguroServicio
{
    Task<TransferenciaArchivo> GuardarPdfAsync(long transferenciaId, string nombreOriginal, Stream contenidoStream, int subidoPorUsuarioId, string? firmaElectronica, string? puntoVentaNombre, string? vendedorNombre, CancellationToken cancellationToken);
    Task<(Stream Contenido, string NombreOriginal)> ObtenerPdfAsync(TransferenciaArchivo transferenciaArchivo, CancellationToken cancellationToken);
    Task EliminarPdfAsync(string rutaInterna, CancellationToken cancellationToken);
}

public interface IPrintService
{
    Task<bool> ImprimirTransferenciaAsync(long transferenciaId, string rutaArchivo, CancellationToken cancellationToken);
}

public interface IFirmaElectronicaServicio
{
    Task<string> GuardarFirmaAsync(int usuarioId, string nombreArchivo, Stream contenido, CancellationToken cancellationToken);
}
