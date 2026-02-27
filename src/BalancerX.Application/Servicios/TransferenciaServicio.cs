using BalancerX.Application.Contratos;
using BalancerX.Application.DTOs;
using BalancerX.Domain.Constantes;
using BalancerX.Domain.Entidades;

namespace BalancerX.Application.Servicios;

public class TransferenciaServicio
{
    private readonly ITransferenciaRepositorio transferenciaRepositorio;
    private readonly IArchivoSeguroServicio archivoSeguroServicio;
    private readonly IUsuarioRepositorio usuarioRepositorio;
    private readonly IPrintService servicioImpresion;

    public TransferenciaServicio(ITransferenciaRepositorio transferenciaRepositorio, IArchivoSeguroServicio archivoSeguroServicio, IUsuarioRepositorio usuarioRepositorio, IPrintService servicioImpresion)
    {
        this.transferenciaRepositorio = transferenciaRepositorio;
        this.archivoSeguroServicio = archivoSeguroServicio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.servicioImpresion = servicioImpresion;
    }

    public async Task<TransferenciaResponse> CrearAsync(CrearTransferenciaRequest crearTransferenciaRequest, int usuarioId, CancellationToken cancellationToken)
    {
        var transferencia = new Transferencia
        {
            Monto = crearTransferenciaRequest.Monto,
            PuntoVentaId = crearTransferenciaRequest.PuntoVentaId,
            VendedorId = crearTransferenciaRequest.VendedorId,
            Observacion = crearTransferenciaRequest.Observacion,
            CreadoPorUsuarioId = usuarioId
        };

        var registro = await transferenciaRepositorio.CrearAsync(transferencia, cancellationToken);
        await transferenciaRepositorio.GuardarEventoAuditoriaAsync(new EventoAuditoria
        {
            Accion = AccionesAuditoria.CrearTransferencia,
            Entidad = nameof(Transferencia),
            EntidadId = registro.Id.ToString(),
            Detalle = "Transferencia creada",
            EjecutadoPorUsuarioId = usuarioId
        }, cancellationToken);

        return new TransferenciaResponse(registro.Id, registro.Monto, registro.PuntoVentaId, registro.VendedorId, registro.Observacion, registro.Estado, registro.CreadoEnUtc, registro.ImpresaEnUtc);
    }

    public async Task<List<TransferenciaResponse>> ListarAsync(FiltroTransferenciaRequest filtroTransferenciaRequest, CancellationToken cancellationToken)
        => (await transferenciaRepositorio.ListarAsync(filtroTransferenciaRequest, cancellationToken))
            .Select(registro => new TransferenciaResponse(registro.Id, registro.Monto, registro.PuntoVentaId, registro.VendedorId, registro.Observacion, registro.Estado, registro.CreadoEnUtc, registro.ImpresaEnUtc)).ToList();


    public async Task<TransferenciaResponse> ActualizarAsync(long transferenciaId, ActualizarTransferenciaRequest actualizarTransferenciaRequest, int usuarioId, CancellationToken cancellationToken)
    {
        if (actualizarTransferenciaRequest.Monto <= 0) throw new InvalidOperationException("El monto debe ser mayor a 0.");
        if (string.IsNullOrWhiteSpace(actualizarTransferenciaRequest.Estado)) throw new InvalidOperationException("El estado es obligatorio.");

        var transferencia = await transferenciaRepositorio.ObtenerPorIdAsync(transferenciaId, cancellationToken) ?? throw new InvalidOperationException("Transferencia no encontrada.");

        transferencia.Monto = actualizarTransferenciaRequest.Monto;
        transferencia.PuntoVentaId = actualizarTransferenciaRequest.PuntoVentaId;
        transferencia.VendedorId = actualizarTransferenciaRequest.VendedorId;
        transferencia.Observacion = actualizarTransferenciaRequest.Observacion;
        transferencia.Estado = actualizarTransferenciaRequest.Estado;

        var actualizada = await transferenciaRepositorio.ActualizarAsync(transferencia, cancellationToken);
        await transferenciaRepositorio.GuardarEventoAuditoriaAsync(new EventoAuditoria
        {
            Accion = AccionesAuditoria.ActualizarTransferencia,
            Entidad = nameof(Transferencia),
            EntidadId = actualizada.Id.ToString(),
            Detalle = "Transferencia actualizada por ADMIN",
            EjecutadoPorUsuarioId = usuarioId
        }, cancellationToken);

        return new TransferenciaResponse(actualizada.Id, actualizada.Monto, actualizada.PuntoVentaId, actualizada.VendedorId, actualizada.Observacion, actualizada.Estado, actualizada.CreadoEnUtc, actualizada.ImpresaEnUtc);
    }

    public async Task<SubirPdfResponse> SubirPdfAsync(long transferenciaId, string nombreOriginal, Stream contenidoStream, int usuarioId, CancellationToken cancellationToken)
    {
        var transferencia = await transferenciaRepositorio.ObtenerPorIdAsync(transferenciaId, cancellationToken) ?? throw new InvalidOperationException("Transferencia no encontrada.");
        var archivo = await archivoSeguroServicio.GuardarPdfAsync(transferencia.Id, nombreOriginal, contenidoStream, usuarioId, cancellationToken);
        var guardado = await transferenciaRepositorio.GuardarArchivoAsync(archivo, cancellationToken);
        await transferenciaRepositorio.GuardarEventoAuditoriaAsync(new EventoAuditoria { Accion = AccionesAuditoria.SubirArchivo, Entidad = nameof(Transferencia), EntidadId = transferenciaId.ToString(), Detalle = "PDF subido", EjecutadoPorUsuarioId = usuarioId }, cancellationToken);
        return new SubirPdfResponse(guardado.Id, guardado.TransferenciaId, guardado.NombreOriginal, guardado.TamanoBytes, guardado.SubidoEnUtc);
    }

    public async Task<(Stream Contenido, string NombreOriginal)> DescargarPdfAsync(long transferenciaId, CancellationToken cancellationToken)
    {
        var archivo = await transferenciaRepositorio.ObtenerArchivoPorTransferenciaAsync(transferenciaId, cancellationToken) ?? throw new InvalidOperationException("No existe PDF para la transferencia.");
        return await archivoSeguroServicio.ObtenerPdfAsync(archivo, cancellationToken);
    }

    public async Task ImprimirAsync(long transferenciaId, int usuarioId, CancellationToken cancellationToken)
    {
        var archivo = await transferenciaRepositorio.ObtenerArchivoPorTransferenciaAsync(transferenciaId, cancellationToken) ?? throw new InvalidOperationException("No existe PDF para imprimir.");
        var pudoMarcar = await transferenciaRepositorio.MarcarImpresaPrimeraVezAsync(transferenciaId, DateTime.UtcNow, cancellationToken);
        if (!pudoMarcar) throw new InvalidOperationException("La transferencia ya fue impresa.");

        var resultadoImpresion = await servicioImpresion.ImprimirTransferenciaAsync(transferenciaId, archivo.RutaInterna, cancellationToken);
        if (!resultadoImpresion) throw new InvalidOperationException("Error al imprimir.");

        await transferenciaRepositorio.GuardarEventoImpresionAsync(new EventoImpresion { TransferenciaId = transferenciaId, EsReimpresion = false, EjecutadoPorUsuarioId = usuarioId }, cancellationToken);
        await transferenciaRepositorio.GuardarEventoAuditoriaAsync(new EventoAuditoria { Accion = AccionesAuditoria.Imprimir, Entidad = nameof(Transferencia), EntidadId = transferenciaId.ToString(), Detalle = "Impresión inicial", EjecutadoPorUsuarioId = usuarioId }, cancellationToken);
    }

    public async Task ReimprimirAsync(long transferenciaId, ReimpresionRequest reimpresionRequest, int usuarioIdEjecutor, CancellationToken cancellationToken)
    {
        var usuarioAdmin = await usuarioRepositorio.ObtenerPorIdAsync(usuarioIdEjecutor, cancellationToken) ?? throw new UnauthorizedAccessException();
        var pinValido = await usuarioRepositorio.ValidarPinAdminAsync(usuarioAdmin.Id, reimpresionRequest.PinAdmin, cancellationToken);
        if (!pinValido) throw new UnauthorizedAccessException("PIN admin inválido.");
        if (string.IsNullOrWhiteSpace(reimpresionRequest.Razon)) throw new InvalidOperationException("La razón es obligatoria.");

        var archivo = await transferenciaRepositorio.ObtenerArchivoPorTransferenciaAsync(transferenciaId, cancellationToken) ?? throw new InvalidOperationException("No existe PDF para imprimir.");
        var resultadoImpresion = await servicioImpresion.ImprimirTransferenciaAsync(transferenciaId, archivo.RutaInterna, cancellationToken);
        if (!resultadoImpresion) throw new InvalidOperationException("Error al imprimir.");

        await transferenciaRepositorio.GuardarEventoImpresionAsync(new EventoImpresion
        {
            TransferenciaId = transferenciaId,
            EsReimpresion = true,
            EjecutadoPorUsuarioId = usuarioIdEjecutor,
            AutorizadoPorUsuarioId = usuarioAdmin.Id,
            Razon = reimpresionRequest.Razon
        }, cancellationToken);
        await transferenciaRepositorio.GuardarEventoAuditoriaAsync(new EventoAuditoria { Accion = AccionesAuditoria.Reimprimir, Entidad = nameof(Transferencia), EntidadId = transferenciaId.ToString(), Detalle = reimpresionRequest.Razon, EjecutadoPorUsuarioId = usuarioIdEjecutor }, cancellationToken);
    }
}
