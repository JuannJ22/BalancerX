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
    Task<TransferenciaArchivo?> ObtenerArchivoPorTransferenciaAsync(long transferenciaId, CancellationToken cancellationToken);
}

public interface IUsuarioRepositorio
{
    Task<Usuario?> ObtenerPorUsuarioNombreAsync(string usuarioNombre, CancellationToken cancellationToken);
    Task<Usuario?> ObtenerPorIdAsync(int usuarioId, CancellationToken cancellationToken);
    Task<bool> ValidarPinAdminAsync(int usuarioId, string pinAdminPlano, CancellationToken cancellationToken);
}

public interface IJwtTokenServicio
{
    string GenerarToken(Usuario usuario);
}

public interface IArchivoSeguroServicio
{
    Task<TransferenciaArchivo> GuardarPdfAsync(long transferenciaId, string nombreOriginal, Stream contenidoStream, int subidoPorUsuarioId, CancellationToken cancellationToken);
    Task<(Stream Contenido, string NombreOriginal)> ObtenerPdfAsync(TransferenciaArchivo transferenciaArchivo, CancellationToken cancellationToken);
}

public interface IPrintService
{
    Task<bool> ImprimirTransferenciaAsync(long transferenciaId, string rutaArchivo, CancellationToken cancellationToken);
}
