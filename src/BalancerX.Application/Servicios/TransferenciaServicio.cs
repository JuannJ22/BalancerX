using BalancerX.Application.Contratos;
using BalancerX.Application.DTOs;
using BalancerX.Domain.Constantes;
using BalancerX.Domain.Entidades;

namespace BalancerX.Application.Servicios;

public class TransferenciaServicio
{
    private readonly ITransferenciaRepositorio transferenciaRepositorio;
    private readonly IArchivoSeguroServicio archivoSeguroServicio;
    private readonly IPrintService printService;
    private readonly IUsuarioRepositorio usuarioRepositorio;
    private readonly ICatalogosSyncServicio catalogosSyncServicio;

    public TransferenciaServicio(ITransferenciaRepositorio transferenciaRepositorio, IArchivoSeguroServicio archivoSeguroServicio, IPrintService printService, IUsuarioRepositorio usuarioRepositorio, ICatalogosSyncServicio catalogosSyncServicio)
    {
        this.transferenciaRepositorio = transferenciaRepositorio;
        this.archivoSeguroServicio = archivoSeguroServicio;
        this.printService = printService;
        this.usuarioRepositorio = usuarioRepositorio;
        this.catalogosSyncServicio = catalogosSyncServicio;
    }

    public async Task<TransferenciaResponse> CrearAsync(CrearTransferenciaRequest crearTransferenciaRequest, int usuarioId, CancellationToken cancellationToken)
    {
        var usuario = await usuarioRepositorio.ObtenerPorIdAsync(usuarioId, cancellationToken) ?? throw new UnauthorizedAccessException();
        if (EsAuxiliar(usuario))
            throw new UnauthorizedAccessException("El usuario AUXILIAR no puede crear transferencias.");

        await catalogosSyncServicio.SincronizarAsync(cancellationToken);
        await ValidarReferenciasAsync(crearTransferenciaRequest.PuntoVentaId, crearTransferenciaRequest.VendedorId, crearTransferenciaRequest.BancoId, crearTransferenciaRequest.CuentaContableId, cancellationToken);

        var transferencia = new Transferencia
        {
            Monto = crearTransferenciaRequest.Monto,
            PuntoVentaId = crearTransferenciaRequest.PuntoVentaId,
            VendedorId = crearTransferenciaRequest.VendedorId,
            BancoId = crearTransferenciaRequest.BancoId,
            CuentaContableId = crearTransferenciaRequest.CuentaContableId,
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

        return new TransferenciaResponse(registro.Id, registro.Monto, registro.PuntoVentaId, registro.VendedorId, registro.BancoId, registro.CuentaContableId, registro.Observacion, registro.Estado, registro.CreadoEnUtc, registro.ImpresaEnUtc);
    }

    public async Task<List<TransferenciaResponse>> ListarAsync(FiltroTransferenciaRequest filtroTransferenciaRequest, CancellationToken cancellationToken)
        => (await transferenciaRepositorio.ListarAsync(filtroTransferenciaRequest, cancellationToken))
            .Select(registro => new TransferenciaResponse(registro.Id, registro.Monto, registro.PuntoVentaId, registro.VendedorId, registro.BancoId, registro.CuentaContableId, registro.Observacion, registro.Estado, registro.CreadoEnUtc, registro.ImpresaEnUtc)).ToList();

    public async Task<List<TransferenciaResponse>> ListarPorUsuarioAsync(int usuarioId, FiltroTransferenciaRequest filtroTransferenciaRequest, CancellationToken cancellationToken)
    {
        var usuario = await usuarioRepositorio.ObtenerPorIdAsync(usuarioId, cancellationToken) ?? throw new UnauthorizedAccessException();
        if (EsAuxiliar(usuario))
        {
            if (!usuario.PuntoVentaAsignadoId.HasValue)
                throw new UnauthorizedAccessException("El usuario AUXILIAR no tiene punto de venta asignado.");

            filtroTransferenciaRequest = filtroTransferenciaRequest with { PuntoVentaId = usuario.PuntoVentaAsignadoId.Value };
        }

        return await ListarAsync(filtroTransferenciaRequest, cancellationToken);
    }


    public async Task<TransferenciaResponse> ObtenerPorIdAsync(long transferenciaId, int usuarioId, CancellationToken cancellationToken)
    {
        var transferencia = await transferenciaRepositorio.ObtenerPorIdAsync(transferenciaId, cancellationToken) ?? throw new InvalidOperationException("Transferencia no encontrada.");
        await ValidarAccesoAuxiliarPorPuntoVentaAsync(usuarioId, transferencia.PuntoVentaId, cancellationToken);
        return new TransferenciaResponse(transferencia.Id, transferencia.Monto, transferencia.PuntoVentaId, transferencia.VendedorId, transferencia.BancoId, transferencia.CuentaContableId, transferencia.Observacion, transferencia.Estado, transferencia.CreadoEnUtc, transferencia.ImpresaEnUtc);
    }


    public async Task<TransferenciaResponse> ActualizarAsync(long transferenciaId, ActualizarTransferenciaRequest request, int usuarioId, CancellationToken cancellationToken)
    {
        if (request.Monto <= 0) throw new InvalidOperationException("El monto debe ser mayor a 0.");
        var estadoNormalizado = NormalizarEstado(request.Estado);
        await catalogosSyncServicio.SincronizarAsync(cancellationToken);
        await ValidarReferenciasAsync(request.PuntoVentaId, request.VendedorId, request.BancoId, request.CuentaContableId, cancellationToken);

        var transferencia = await transferenciaRepositorio.ObtenerPorIdAsync(transferenciaId, cancellationToken) ?? throw new InvalidOperationException("Transferencia no encontrada.");

        transferencia.Monto = request.Monto;
        transferencia.PuntoVentaId = request.PuntoVentaId;
        transferencia.VendedorId = request.VendedorId;
        transferencia.BancoId = request.BancoId;
        transferencia.CuentaContableId = request.CuentaContableId;
        transferencia.Observacion = request.Observacion;
        transferencia.Estado = estadoNormalizado;
        transferencia.ImpresaEnUtc = estadoNormalizado switch
        {
            EstadosTransferencia.SinImprimir => null,
            EstadosTransferencia.Impresa when !transferencia.ImpresaEnUtc.HasValue => DateTime.UtcNow,
            _ => transferencia.ImpresaEnUtc
        };

        var actualizada = await transferenciaRepositorio.ActualizarAsync(transferencia, cancellationToken);
        await transferenciaRepositorio.GuardarEventoAuditoriaAsync(new EventoAuditoria
        {
            Accion = AccionesAuditoria.ActualizarTransferencia,
            Entidad = nameof(Transferencia),
            EntidadId = actualizada.Id.ToString(),
            Detalle = "Transferencia actualizada",
            EjecutadoPorUsuarioId = usuarioId
        }, cancellationToken);

        return new TransferenciaResponse(actualizada.Id, actualizada.Monto, actualizada.PuntoVentaId, actualizada.VendedorId, actualizada.BancoId, actualizada.CuentaContableId, actualizada.Observacion, actualizada.Estado, actualizada.CreadoEnUtc, actualizada.ImpresaEnUtc);
    }

    public async Task<SubirPdfResponse> SubirPdfAsync(long transferenciaId, string nombreOriginal, Stream contenidoStream, int usuarioId, CancellationToken cancellationToken)
    {
        if (string.IsNullOrWhiteSpace(nombreOriginal)) throw new InvalidOperationException("El nombre del archivo es obligatorio.");
        if (contenidoStream is null || !contenidoStream.CanRead) throw new InvalidOperationException("El contenido del archivo es inválido.");

        var transferencia = await transferenciaRepositorio.ObtenerPorIdAsync(transferenciaId, cancellationToken) ?? throw new InvalidOperationException("Transferencia no encontrada.");
        await ValidarAccesoAuxiliarPorPuntoVentaAsync(usuarioId, transferencia.PuntoVentaId, cancellationToken);
        var archivoExistente = await transferenciaRepositorio.ObtenerArchivoPorTransferenciaAsync(transferenciaId, cancellationToken);
        if (archivoExistente is not null)
            throw new InvalidOperationException("La transferencia ya tiene un PDF cargado. Elimine el archivo actual antes de subir uno nuevo.");

        var usuario = await usuarioRepositorio.ObtenerPorIdAsync(usuarioId, cancellationToken) ?? throw new UnauthorizedAccessException();
        var firma = usuario.FirmaElectronica;
        var puntoVentaNombre = await transferenciaRepositorio.ObtenerNombrePuntoVentaAsync(transferencia.PuntoVentaId, cancellationToken);
        var vendedorNombre = await transferenciaRepositorio.ObtenerNombreVendedorAsync(transferencia.VendedorId, cancellationToken);

        TransferenciaArchivo archivo;
        try
        {
            archivo = await archivoSeguroServicio.GuardarPdfAsync(
                transferencia.Id,
                nombreOriginal,
                contenidoStream,
                usuarioId,
                firma,
                puntoVentaNombre,
                vendedorNombre,
                transferencia.Observacion,
                cancellationToken);
        }
        catch (Exception ex)
        {
            throw new InvalidOperationException($"No se pudo procesar el PDF. Verifique que el archivo sea válido y no esté protegido. Detalle: {ex.Message}");
        }

        try
        {
            var guardado = await transferenciaRepositorio.GuardarArchivoAsync(archivo, cancellationToken);
            await transferenciaRepositorio.ActualizarEstadoAsync(transferenciaId, DeterminarEstadoDesdeImpresion(false), cancellationToken);
            await transferenciaRepositorio.GuardarEventoAuditoriaAsync(new EventoAuditoria { Accion = AccionesAuditoria.SubirArchivo, Entidad = nameof(Transferencia), EntidadId = transferenciaId.ToString(), Detalle = "PDF subido", EjecutadoPorUsuarioId = usuarioId }, cancellationToken);
            return new SubirPdfResponse(guardado.Id, guardado.TransferenciaId, guardado.NombreOriginal, guardado.TamanoBytes, guardado.SubidoEnUtc);
        }
        catch
        {
            await archivoSeguroServicio.EliminarPdfAsync(archivo.RutaInterna, cancellationToken);
            throw;
        }
    }


    public async Task<EliminarPdfResponse> EliminarPdfAsync(long transferenciaId, int usuarioId, CancellationToken cancellationToken)
    {
        var archivo = await transferenciaRepositorio.ObtenerArchivoPorTransferenciaAsync(transferenciaId, cancellationToken);
        if (archivo is null) return new EliminarPdfResponse(transferenciaId, false);

        await archivoSeguroServicio.EliminarPdfAsync(archivo.RutaInterna, cancellationToken);
        var eliminado = await transferenciaRepositorio.EliminarArchivoPorTransferenciaAsync(transferenciaId, cancellationToken);
        await transferenciaRepositorio.ActualizarEstadoAsync(transferenciaId, DeterminarEstadoDesdeImpresion(false), cancellationToken);
        await transferenciaRepositorio.GuardarEventoAuditoriaAsync(new EventoAuditoria { Accion = AccionesAuditoria.Anular, Entidad = nameof(TransferenciaArchivo), EntidadId = transferenciaId.ToString(), Detalle = "PDF eliminado por ADMIN", EjecutadoPorUsuarioId = usuarioId }, cancellationToken);
        return new EliminarPdfResponse(transferenciaId, eliminado);
    }

    public async Task<bool> EliminarTransferenciaAsync(long transferenciaId, int usuarioId, CancellationToken cancellationToken)
    {
        var archivos = await transferenciaRepositorio.ObtenerArchivosPorTransferenciaAsync(transferenciaId, cancellationToken);
        foreach (var archivo in archivos)
        {
            await archivoSeguroServicio.EliminarPdfAsync(archivo.RutaInterna, cancellationToken);
        }

        var eliminado = await transferenciaRepositorio.EliminarAsync(transferenciaId, cancellationToken);
        if (eliminado)
        {
            await transferenciaRepositorio.GuardarEventoAuditoriaAsync(new EventoAuditoria { Accion = AccionesAuditoria.Anular, Entidad = nameof(Transferencia), EntidadId = transferenciaId.ToString(), Detalle = "Transferencia eliminada por ADMIN", EjecutadoPorUsuarioId = usuarioId }, cancellationToken);
        }
        return eliminado;
    }

    public async Task<(Stream Contenido, string NombreOriginal)> DescargarPdfAsync(long transferenciaId, int usuarioId, CancellationToken cancellationToken)
    {
        var transferencia = await transferenciaRepositorio.ObtenerPorIdAsync(transferenciaId, cancellationToken) ?? throw new InvalidOperationException("Transferencia no encontrada.");
        await ValidarAccesoAuxiliarPorPuntoVentaAsync(usuarioId, transferencia.PuntoVentaId, cancellationToken);
        var archivo = await transferenciaRepositorio.ObtenerArchivoPorTransferenciaAsync(transferenciaId, cancellationToken) ?? throw new InvalidOperationException("No existe PDF para la transferencia.");
        return await archivoSeguroServicio.ObtenerPdfAsync(archivo, cancellationToken);
    }

    public async Task ImprimirAsync(long transferenciaId, int usuarioId, string? terminalId, CancellationToken cancellationToken)
    {
        var transferencia = await transferenciaRepositorio.ObtenerPorIdAsync(transferenciaId, cancellationToken) ?? throw new InvalidOperationException("Transferencia no encontrada.");
        await ValidarAccesoAuxiliarPorPuntoVentaAsync(usuarioId, transferencia.PuntoVentaId, cancellationToken);

        var archivo = await transferenciaRepositorio.ObtenerArchivoPorTransferenciaAsync(transferenciaId, cancellationToken) ?? throw new InvalidOperationException("No existe PDF para imprimir.");
        if (!string.Equals(transferencia.Estado, EstadosTransferencia.SinImprimir, StringComparison.OrdinalIgnoreCase))
            throw new InvalidOperationException("La transferencia ya fue impresa.");

        var contextoImpresion = new PrintRequestContext(usuarioId, transferencia.PuntoVentaId, terminalId);
        var impresion = await printService.ImprimirTransferenciaAsync(transferenciaId, archivo.RutaInterna, contextoImpresion, cancellationToken);
        if (!impresion.Success)
            throw new InvalidOperationException($"No fue posible enviar la transferencia a impresión. {impresion.Detail ?? "Revise la configuración de impresión del servidor."}".Trim());

        if (!transferencia.ImpresaEnUtc.HasValue)
            await transferenciaRepositorio.MarcarImpresaPrimeraVezAsync(transferenciaId, DateTime.UtcNow, cancellationToken);

        await transferenciaRepositorio.ActualizarEstadoAsync(transferenciaId, DeterminarEstadoDesdeImpresion(true), cancellationToken);
        await transferenciaRepositorio.GuardarEventoImpresionAsync(new EventoImpresion { TransferenciaId = transferenciaId, EsReimpresion = false, EjecutadoPorUsuarioId = usuarioId }, cancellationToken);
        await transferenciaRepositorio.GuardarEventoAuditoriaAsync(new EventoAuditoria { Accion = AccionesAuditoria.Imprimir, Entidad = nameof(Transferencia), EntidadId = transferenciaId.ToString(), Detalle = "Impresión inicial", EjecutadoPorUsuarioId = usuarioId }, cancellationToken);
    }

    private async Task ValidarAccesoAuxiliarPorPuntoVentaAsync(int usuarioId, int puntoVentaIdTransferencia, CancellationToken cancellationToken)
    {
        var usuario = await usuarioRepositorio.ObtenerPorIdAsync(usuarioId, cancellationToken) ?? throw new UnauthorizedAccessException();
        if (!EsAuxiliar(usuario)) return;

        if (!usuario.PuntoVentaAsignadoId.HasValue)
            throw new UnauthorizedAccessException("El usuario AUXILIAR no tiene punto de venta asignado.");

        if (usuario.PuntoVentaAsignadoId.Value != puntoVentaIdTransferencia)
            throw new UnauthorizedAccessException("No tiene acceso a transferencias de otro punto de venta.");
    }

    private static bool EsAuxiliar(Usuario usuario)
        => usuario.Roles.Any(r => string.Equals(r.Rol?.Nombre, "AUXILIAR", StringComparison.OrdinalIgnoreCase));

    public async Task ReimprimirAsync(long transferenciaId, ReimpresionRequest reimpresionRequest, int usuarioIdEjecutor, string? terminalId, CancellationToken cancellationToken)
    {
        if (string.IsNullOrWhiteSpace(reimpresionRequest.UsuarioEncargado)) throw new InvalidOperationException("El usuario del encargado es obligatorio.");
        if (string.IsNullOrWhiteSpace(reimpresionRequest.PinEncargado)) throw new InvalidOperationException("El PIN del encargado es obligatorio.");
        if (string.IsNullOrWhiteSpace(reimpresionRequest.Razon)) throw new InvalidOperationException("La razón es obligatoria.");

        var usuarioEjecutor = await usuarioRepositorio.ObtenerPorIdAsync(usuarioIdEjecutor, cancellationToken) ?? throw new UnauthorizedAccessException();
        var usuarioEncargado = await usuarioRepositorio.ObtenerPorUsuarioNombreAsync(reimpresionRequest.UsuarioEncargado.Trim(), cancellationToken)
            ?? throw new UnauthorizedAccessException("Usuario encargado inválido.");

        var encargadoEsAdmin = usuarioEncargado.Roles.Any(r => string.Equals(r.Rol?.Nombre, "ADMIN", StringComparison.OrdinalIgnoreCase));
        if (!encargadoEsAdmin) throw new UnauthorizedAccessException("El usuario indicado no tiene rol de encargado.");

        var pinValido = await usuarioRepositorio.ValidarPinAdminAsync(usuarioEncargado.Id, reimpresionRequest.PinEncargado, cancellationToken);
        if (!pinValido) throw new UnauthorizedAccessException("PIN de encargado inválido.");

        var archivo = await transferenciaRepositorio.ObtenerArchivoPorTransferenciaAsync(transferenciaId, cancellationToken) ?? throw new InvalidOperationException("No existe PDF para imprimir.");

        var transferencia = await transferenciaRepositorio.ObtenerPorIdAsync(transferenciaId, cancellationToken) ?? throw new InvalidOperationException("Transferencia no encontrada.");
        var contextoImpresion = new PrintRequestContext(usuarioEjecutor.Id, transferencia.PuntoVentaId, terminalId);
        var impresion = await printService.ImprimirTransferenciaAsync(transferenciaId, archivo.RutaInterna, contextoImpresion, cancellationToken);
        if (!impresion.Success)
            throw new InvalidOperationException($"No fue posible enviar la reimpresión. {impresion.Detail ?? "Revise la configuración de impresión del servidor."}".Trim());

        await transferenciaRepositorio.GuardarEventoImpresionAsync(new EventoImpresion
        {
            TransferenciaId = transferenciaId,
            EsReimpresion = true,
            EjecutadoPorUsuarioId = usuarioEjecutor.Id,
            AutorizadoPorUsuarioId = usuarioEncargado.Id,
            Razon = reimpresionRequest.Razon
        }, cancellationToken);
        await transferenciaRepositorio.GuardarEventoAuditoriaAsync(new EventoAuditoria { Accion = AccionesAuditoria.Reimprimir, Entidad = nameof(Transferencia), EntidadId = transferenciaId.ToString(), Detalle = reimpresionRequest.Razon, EjecutadoPorUsuarioId = usuarioEjecutor.Id }, cancellationToken);
    }

    private static string NormalizarEstado(string? estado)
    {
        if (string.IsNullOrWhiteSpace(estado))
            throw new InvalidOperationException("El estado es obligatorio.");

        var normalizado = estado.Trim().ToUpperInvariant();
        if (!EstadosTransferencia.Permitidos.Contains(normalizado))
            throw new InvalidOperationException($"Estado inválido. Estados permitidos: {EstadosTransferencia.SinImprimir}, {EstadosTransferencia.Impresa}.");

        return normalizado;
    }

    private static string DeterminarEstadoDesdeImpresion(bool impresa)
        => impresa ? EstadosTransferencia.Impresa : EstadosTransferencia.SinImprimir;

    private async Task ValidarReferenciasAsync(int puntoVentaId, int vendedorId, int bancoId, int cuentaContableId, CancellationToken cancellationToken)
    {
        if (!await transferenciaRepositorio.ExistePuntoVentaAsync(puntoVentaId, cancellationToken))
            throw new InvalidOperationException("Punto de venta inválido. Seleccione un punto de venta del catálogo.");

        if (!await transferenciaRepositorio.ExisteVendedorAsync(vendedorId, cancellationToken))
            throw new InvalidOperationException("Vendedor inválido. Seleccione un vendedor del catálogo.");

        if (!await transferenciaRepositorio.ExisteBancoAsync(bancoId, cancellationToken))
            throw new InvalidOperationException("Banco inválido. Seleccione un banco del catálogo.");

        if (!await transferenciaRepositorio.ExisteCuentaContableEnBancoAsync(cuentaContableId, bancoId, cancellationToken))
            throw new InvalidOperationException("Cuenta contable inválida para el banco seleccionado.");
    }
}
