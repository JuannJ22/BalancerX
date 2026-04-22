using System.Diagnostics;
using System.IdentityModel.Tokens.Jwt;
using System.Runtime.InteropServices;
using System.Security.AccessControl;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Security.Principal;
using System.Text;
using BalancerX.Application.Contratos;
using iText.Kernel.Colors;
using iText.Kernel.Exceptions;
using iText.Kernel.Font;
using iText.Kernel.Pdf;
using iText.Kernel.Pdf.Extgstate;
using iText.Kernel.Pdf.Canvas;
using iText.Layout;
using iText.Layout.Properties;
using iText.Layout.Element;
using iText.IO.Font.Constants;
using BalancerX.Domain.Entidades;
using BalancerX.Infrastructure.Datos;
using BalancerX.Infrastructure.Repositorios;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.IdentityModel.Tokens;

namespace BalancerX.Infrastructure.Servicios;

public static class ServiciosInfraestructura
{
    public static IServiceCollection AgregarInfraestructura(this IServiceCollection servicios, IConfiguration configuracion)
    {
        var connectionString = ObtenerConnectionStringSqlServer(configuracion);
        servicios.AddDbContext<BalancerXDbContext>(opciones => opciones.UseSqlServer(connectionString));
        servicios.AddScoped<ITransferenciaRepositorio, TransferenciaRepositorio>();
        servicios.AddScoped<ICatalogosSyncServicio, CatalogosSyncServicio>();
        servicios.AddScoped<IUsuarioRepositorio, UsuarioRepositorio>();
        servicios.AddScoped<IJwtTokenServicio, JwtTokenServicio>();
        servicios.AddScoped<IArchivoSeguroServicio, ArchivoSeguroServicio>();
        servicios.AddScoped<IFirmaElectronicaServicio, FirmaElectronicaServicio>();
        servicios.AddScoped<IAdaptadorImpresionWindows, AdaptadorImpresionWindows>();
        servicios.AddScoped<IPrintService, PrintService>();
        servicios.AddScoped<BalancerX.Application.Servicios.UsuarioAdminServicio>();
        servicios.AddScoped<BalancerX.Application.Servicios.UsuarioPerfilServicio>();
        return servicios;
    }

    private static string ObtenerConnectionStringSqlServer(IConfiguration configuracion)
    {
        var connectionString = configuracion.GetConnectionString("SqlServer");

        if (string.IsNullOrWhiteSpace(connectionString))
            throw new InvalidOperationException("No se encontró la cadena de conexión 'ConnectionStrings:SqlServer'. Configure appsettings o la variable de entorno 'ConnectionStrings__SqlServer'.");

        var placeholders = new[] { "TU_SERVIDOR_SQL", "TU_USUARIO", "TU_PASSWORD" };
        if (placeholders.Any(ph => connectionString.Contains(ph, StringComparison.OrdinalIgnoreCase)))
            throw new InvalidOperationException("La cadena de conexión 'ConnectionStrings:SqlServer' contiene valores de ejemplo. Reemplace TU_SERVIDOR_SQL, TU_USUARIO y TU_PASSWORD por credenciales reales.");

        return connectionString;
    }
}


public interface IAdaptadorImpresionWindows
{
    Task<bool> ImprimirPdfAsync(string rutaArchivo, CancellationToken cancellationToken);
}

public sealed class PrintingOptions
{
    public string? PrinterName { get; init; }
    public string? CommandTemplate { get; init; }
    public bool CloseViewerAfterPrint { get; init; }
    public int ViewerCloseDelayMs { get; init; } = 5000;
    public bool ForceKillViewerOnTimeout { get; init; }
    public bool AggressiveViewerProcessCleanup { get; init; }
    public string[] ViewerProcessNames { get; init; } = [];

    public static PrintingOptions Desde(IConfiguration configuracion)
    {
        var delay = 5000;
        var delayConfigurado = configuracion["Printing:ViewerCloseDelayMs"];

        if (int.TryParse(delayConfigurado, out var parsedDelay) && parsedDelay > 0)
            delay = parsedDelay;

        return new PrintingOptions
        {
            PrinterName = configuracion["Printing:PrinterName"],
            CommandTemplate = configuracion["Printing:CommandTemplate"],
            CloseViewerAfterPrint = bool.TryParse(configuracion["Printing:CloseViewerAfterPrint"], out var closeViewerAfterPrint) && closeViewerAfterPrint,
            ViewerCloseDelayMs = delay,
            ForceKillViewerOnTimeout = bool.TryParse(configuracion["Printing:ForceKillViewerOnTimeout"], out var forceKillViewerOnTimeout) && forceKillViewerOnTimeout,
            AggressiveViewerProcessCleanup = bool.TryParse(configuracion["Printing:AggressiveViewerProcessCleanup"], out var aggressiveCleanup) && aggressiveCleanup,
            ViewerProcessNames = ObtenerViewerProcessNames(configuracion["Printing:ViewerProcessNames"])
        };
    }

    private static string[] ObtenerViewerProcessNames(string? value)
        => string.IsNullOrWhiteSpace(value)
            ? ["AcroRd32", "Acrobat"]
            : value
                .Split(',', StringSplitOptions.RemoveEmptyEntries | StringSplitOptions.TrimEntries)
                .Where(static x => !string.IsNullOrWhiteSpace(x))
                .Distinct(StringComparer.OrdinalIgnoreCase)
                .ToArray();
}

public class AdaptadorImpresionWindows : IAdaptadorImpresionWindows
{
    private readonly PrintingOptions opciones;
    private readonly ILogger<AdaptadorImpresionWindows> logger;

    public AdaptadorImpresionWindows(IConfiguration configuracion, ILogger<AdaptadorImpresionWindows> logger)
    {
        opciones = PrintingOptions.Desde(configuracion);
        this.logger = logger;
    }

    public async Task<bool> ImprimirPdfAsync(string rutaArchivo, CancellationToken cancellationToken)
    {
        if (!File.Exists(rutaArchivo)) return false;

        if (!string.IsNullOrWhiteSpace(opciones.CommandTemplate))
            return await EjecutarComandoAsync(opciones.CommandTemplate, rutaArchivo, opciones.PrinterName, cancellationToken);

        if (RuntimeInformation.IsOSPlatform(OSPlatform.Windows))
        {
            logger.LogWarning("No se configuró Printing:CommandTemplate en Windows. Se usará el visor predeterminado y su cierre automático dependerá de la configuración.");
            return await EjecutarImpresionWindowsPredeterminadaAsync(rutaArchivo, opciones, cancellationToken);
        }

        var comandoLinux = string.IsNullOrWhiteSpace(opciones.PrinterName)
            ? "lp \"{file}\""
            : "lp -d \"{printer}\" \"{file}\"";

        return await EjecutarComandoAsync(comandoLinux, rutaArchivo, opciones.PrinterName, cancellationToken);
    }

    private Task<bool> EjecutarImpresionWindowsPredeterminadaAsync(string rutaArchivo, PrintingOptions opcionesImpresion, CancellationToken cancellationToken)
    {
        if (cancellationToken.IsCancellationRequested)
            return Task.FromCanceled<bool>(cancellationToken);

        try
        {
            var inicio = new ProcessStartInfo
            {
                FileName = rutaArchivo,
                UseShellExecute = true,
                Verb = string.IsNullOrWhiteSpace(opcionesImpresion.PrinterName) ? "Print" : "PrintTo",
                Arguments = string.IsNullOrWhiteSpace(opcionesImpresion.PrinterName) ? string.Empty : $"\"{opcionesImpresion.PrinterName}\"",
                CreateNoWindow = true,
                WindowStyle = ProcessWindowStyle.Hidden
            };

            var proceso = Process.Start(inicio);
            if (proceso is null) return Task.FromResult(false);

            logger.LogInformation(
                "Se lanzó la impresión del archivo {RutaArchivo} usando el visor predeterminado de Windows{SufijoImpresora}.",
                rutaArchivo,
                string.IsNullOrWhiteSpace(opcionesImpresion.PrinterName) ? string.Empty : $" en la impresora '{opcionesImpresion.PrinterName}'");

            if (opcionesImpresion.CloseViewerAfterPrint)
                _ = CerrarVisorDespuesDeImprimirAsync(proceso, rutaArchivo, opcionesImpresion);
            else
                proceso.Dispose();

            return Task.FromResult(true);
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "No fue posible imprimir usando el visor predeterminado de Windows.");
            return Task.FromResult(false);
        }
    }

    private async Task CerrarVisorDespuesDeImprimirAsync(Process proceso, string rutaArchivo, PrintingOptions opcionesImpresion)
    {
        var intentoInicioUtc = DateTime.UtcNow;

        try
        {
            await Task.Delay(opcionesImpresion.ViewerCloseDelayMs);

            if (proceso.HasExited)
                return;

            if (proceso.CloseMainWindow())
            {
                logger.LogInformation(
                    "Se solicitó cerrar el visor PDF tras imprimir el archivo {RutaArchivo}. Delay aplicado: {DelayMs} ms.",
                    rutaArchivo,
                    opcionesImpresion.ViewerCloseDelayMs);

                await proceso.WaitForExitAsync(CancellationToken.None);
                return;
            }

            if (!opcionesImpresion.ForceKillViewerOnTimeout)
            {
                logger.LogInformation(
                    "El visor PDF no expuso una ventana principal para cerrarse automáticamente tras imprimir {RutaArchivo}.",
                    rutaArchivo);

                if (opcionesImpresion.AggressiveViewerProcessCleanup)
                    CerrarProcesosVisorPorNombre(rutaArchivo, opcionesImpresion.ViewerProcessNames, intentoInicioUtc);

                return;
            }

            proceso.Kill(entireProcessTree: false);
            logger.LogWarning(
                "Se forzó el cierre del visor PDF tras imprimir el archivo {RutaArchivo} porque CloseMainWindow no estuvo disponible.",
                rutaArchivo);

            if (opcionesImpresion.AggressiveViewerProcessCleanup)
                CerrarProcesosVisorPorNombre(rutaArchivo, opcionesImpresion.ViewerProcessNames, intentoInicioUtc);
        }
        catch (Exception ex)
        {
            logger.LogWarning(ex, "No fue posible cerrar automáticamente el visor PDF tras imprimir el archivo {RutaArchivo}.", rutaArchivo);
        }
        finally
        {
            proceso.Dispose();
        }
    }

    private void CerrarProcesosVisorPorNombre(string rutaArchivo, string[] processNames, DateTime intentoInicioUtc)
    {
        foreach (var processName in processNames)
        {
            foreach (var candidato in Process.GetProcessesByName(processName))
            {
                try
                {
                    if (candidato.HasExited)
                        continue;

                    if (candidato.StartTime.ToUniversalTime() < intentoInicioUtc.AddMinutes(-1))
                        continue;

                    candidato.Kill(entireProcessTree: false);
                    logger.LogWarning(
                        "Se cerró proceso de visor {ProcessName} posterior a la impresión del archivo {RutaArchivo}.",
                        processName,
                        rutaArchivo);
                }
                catch (Exception ex)
                {
                    logger.LogDebug(
                        ex,
                        "No fue posible cerrar proceso de visor {ProcessName} posterior a imprimir {RutaArchivo}.",
                        processName,
                        rutaArchivo);
                }
                finally
                {
                    candidato.Dispose();
                }
            }
        }
    }

    private async Task<bool> EjecutarComandoAsync(string plantilla, string rutaArchivo, string? impresora, CancellationToken cancellationToken)
    {
        var comando = plantilla.Replace("{file}", rutaArchivo).Replace("{printer}", impresora ?? string.Empty);
        var inicio = new ProcessStartInfo
        {
            FileName = RuntimeInformation.IsOSPlatform(OSPlatform.Windows) ? "cmd.exe" : "/bin/bash",
            Arguments = RuntimeInformation.IsOSPlatform(OSPlatform.Windows) ? $"/c {comando}" : $"-lc \"{comando.Replace("\"", "\\\"")}\"",
            RedirectStandardOutput = true,
            RedirectStandardError = true,
            UseShellExecute = false,
            CreateNoWindow = true
        };

        using var proceso = Process.Start(inicio);
        if (proceso is null) return false;

        await proceso.WaitForExitAsync(cancellationToken);
        if (proceso.ExitCode == 0) return true;

        var error = await proceso.StandardError.ReadToEndAsync(cancellationToken);
        logger.LogError("Error ejecutando comando de impresión: {Error}", error);
        return false;
    }
}

public class PrintService : IPrintService
{
    private readonly IAdaptadorImpresionWindows adaptadorImpresion;

    public PrintService(IAdaptadorImpresionWindows adaptadorImpresion)
    {
        this.adaptadorImpresion = adaptadorImpresion;
    }

    public Task<bool> ImprimirTransferenciaAsync(long transferenciaId, string rutaArchivo, CancellationToken cancellationToken)
        => adaptadorImpresion.ImprimirPdfAsync(rutaArchivo, cancellationToken);
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
    private sealed class StorageSecurityOptions
    {
        public bool EnforceWindowsAcl { get; init; }

        public static StorageSecurityOptions Desde(IConfiguration configuracion)
            => new()
            {
                EnforceWindowsAcl = bool.TryParse(configuracion["Storage:EnforceWindowsAcl"], out var enforceWindowsAcl) && enforceWindowsAcl
            };
    }

    private static class MarcaAguaLayout
    {
        public const float PosicionFirmaXRatio = 0.58f;
        public const float PosicionFirmaYRatio = 0.10f;
        public const float MaximoAnchoFirmaRatio = 0.22f;
        public const float MaximoAltoFirmaRatio = 0.09f;
        public const float DesplazamientoTextoFirmaX = 5f;
        public const float DesplazamientoTextoFirmaY = 15f;
        public const float OpacidadInfo = 1.00f;
        public const float TamanoFuenteInfo = 12f;
        public const float MargenInfoIzquierdo = 42f;
        public const float MargenInfoInferior = 46f;
        public const float SeparacionHorizontalInfo = 135f;
        public const float AnchoBloqueInfo = 125f;
        public const int MaximoCaracteresInfo = 60;
    }

    private readonly string rutaRaiz;
    private readonly ILogger<ArchivoSeguroServicio> logger;
    private readonly StorageSecurityOptions opcionesSeguridad;

    public ArchivoSeguroServicio(IConfiguration configuracion, ILogger<ArchivoSeguroServicio> logger)
    {
        this.logger = logger;
        opcionesSeguridad = StorageSecurityOptions.Desde(configuracion);
        rutaRaiz = configuracion["Storage:TransferenciasPath"]
            ?? Path.Combine(AppContext.BaseDirectory, "storage", "transferencias");
    }

    public async Task<TransferenciaArchivo> GuardarPdfAsync(long transferenciaId, string nombreOriginal, Stream contenidoStream, int subidoPorUsuarioId, string? firmaElectronica, string? puntoVentaNombre, string? vendedorNombre, string? observacion, CancellationToken cancellationToken)
    {
        var ahora = DateTime.UtcNow;
        var carpeta = Path.Combine(rutaRaiz, ahora.Year.ToString(), ahora.Month.ToString("00"));
        Directory.CreateDirectory(carpeta);
        AplicarSeguridadWindows(carpeta, esDirectorio: true);

        var nombreInterno = $"transferencia_{transferenciaId}_{Guid.NewGuid():N}.pdf";
        var rutaInterna = Path.Combine(carpeta, nombreInterno);

        try
        {
            await using (var archivoSalida = File.Create(rutaInterna))
            {
                await contenidoStream.CopyToAsync(archivoSalida, cancellationToken);
            }
            AplicarSeguridadWindows(rutaInterna, esDirectorio: false);

            var tamanoOriginal = new FileInfo(rutaInterna).Length;
            if (tamanoOriginal == 0)
                throw new InvalidOperationException("El archivo PDF está vacío o no se pudo leer su contenido.");

            try
            {
                AplicarMarcaAgua(rutaInterna, firmaElectronica, puntoVentaNombre, vendedorNombre, observacion);
            }
            catch (Exception ex)
            {
                EliminarSilencioso(rutaInterna + ".append.tmp");
                EliminarSilencioso(rutaInterna + ".rewrite.tmp");
                logger.LogWarning(ex,
                    "No fue posible aplicar firma/etiquetas al PDF de transferencia {TransferenciaId}. Se conserva el archivo original sin estampar.",
                    transferenciaId);
            }
        }
        catch
        {
            EliminarSilencioso(rutaInterna);
            EliminarSilencioso(rutaInterna + ".tmp");
            EliminarSilencioso(rutaInterna + ".append.tmp");
            EliminarSilencioso(rutaInterna + ".rewrite.tmp");
            throw;
        }

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
        if (!File.Exists(transferenciaArchivo.RutaInterna))
            throw new InvalidOperationException("El PDF asociado no existe físicamente en el almacenamiento seguro.");

        Stream contenido = File.OpenRead(transferenciaArchivo.RutaInterna);
        return Task.FromResult((contenido, transferenciaArchivo.NombreOriginal));
    }

    public Task EliminarPdfAsync(string rutaInterna, CancellationToken cancellationToken)
    {
        if (File.Exists(rutaInterna))
        {
            File.Delete(rutaInterna);
        }

        return Task.CompletedTask;
    }

    private static void AplicarMarcaAgua(string rutaArchivo, string? firmaElectronica, string? puntoVentaNombre, string? vendedorNombre, string? observacion)
    {
        var firma = firmaElectronica?.Trim();
        var tieneFirma = !string.IsNullOrWhiteSpace(firma);
        var tieneEtiquetas = !string.IsNullOrWhiteSpace(puntoVentaNombre) || !string.IsNullOrWhiteSpace(vendedorNombre) || !string.IsNullOrWhiteSpace(observacion);
        if (!tieneFirma && !tieneEtiquetas) return;

        var temporalAppend = rutaArchivo + ".append.tmp";
        var temporalRewrite = rutaArchivo + ".rewrite.tmp";
        string? temporalFinal = null;

        try
        {
            EstamparPdf(rutaArchivo, temporalAppend, firma, puntoVentaNombre, vendedorNombre, observacion, usarAppendMode: true);
            temporalFinal = temporalAppend;
        }
        catch (PdfException)
        {
            EliminarSilencioso(temporalAppend);
            EstamparPdf(rutaArchivo, temporalRewrite, firma, puntoVentaNombre, vendedorNombre, observacion, usarAppendMode: false);
            temporalFinal = temporalRewrite;
        }

        File.Delete(rutaArchivo);
        File.Move(temporalFinal!, rutaArchivo);
        EliminarSilencioso(temporalAppend);
        EliminarSilencioso(temporalRewrite);
    }

    private static void EstamparPdf(string rutaArchivo, string temporal, string? firma, string? puntoVentaNombre, string? vendedorNombre, string? observacion, bool usarAppendMode)
    {
        var tieneFirma = !string.IsNullOrWhiteSpace(firma);
        var tieneEtiquetas = !string.IsNullOrWhiteSpace(puntoVentaNombre) || !string.IsNullOrWhiteSpace(vendedorNombre) || !string.IsNullOrWhiteSpace(observacion);
        var esImagenFirma = tieneFirma && File.Exists(firma);

        using var reader = new PdfReader(rutaArchivo);
        reader.SetUnethicalReading(true);
        using var writer = new PdfWriter(temporal);
        var stamping = usarAppendMode ? new StampingProperties().UseAppendMode() : new StampingProperties();
        using var pdf = new PdfDocument(reader, writer, stamping);
        var font = PdfFontFactory.CreateFont(StandardFonts.HELVETICA_BOLD);
        var fontInfo = PdfFontFactory.CreateFont(StandardFonts.HELVETICA);

        for (var i = 1; i <= pdf.GetNumberOfPages(); i++)
        {
            var page = pdf.GetPage(i);
            var pageSize = page.GetPageSize();
            var canvas = new PdfCanvas(page.NewContentStreamAfter(), page.GetResources(), pdf);
            canvas.SaveState();

            using var layoutCanvas = new Canvas(canvas, pageSize);
            var posicionX = pageSize.GetWidth() * MarcaAguaLayout.PosicionFirmaXRatio;
            var posicionY = pageSize.GetHeight() * MarcaAguaLayout.PosicionFirmaYRatio;

            if (tieneFirma)
            {
                if (esImagenFirma)
                {
                    var gsImagen = new PdfExtGState().SetFillOpacity(0.95f);
                    canvas.SetExtGState(gsImagen);
                    var imageData = iText.IO.Image.ImageDataFactory.Create(firma!);
                    var imagen = new Image(imageData).ScaleToFit(
                        pageSize.GetWidth() * MarcaAguaLayout.MaximoAnchoFirmaRatio,
                        pageSize.GetHeight() * MarcaAguaLayout.MaximoAltoFirmaRatio);
                    imagen.SetFixedPosition(i, posicionX, posicionY);
                    layoutCanvas.Add(imagen);
                }
                else
                {
                    var gsTexto = new PdfExtGState().SetFillOpacity(0.12f);
                    canvas.SetExtGState(gsTexto);
                    layoutCanvas.SetFont(font).SetFontSize(18).SetFontColor(ColorConstants.GRAY);
                    layoutCanvas.ShowTextAligned(
                        new Paragraph(firma!),
                        posicionX + MarcaAguaLayout.DesplazamientoTextoFirmaX,
                        posicionY + MarcaAguaLayout.DesplazamientoTextoFirmaY,
                        i,
                        TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM,
                        0);
                }
            }

            if (tieneEtiquetas)
            {
                var gsInfo = new PdfExtGState().SetFillOpacity(MarcaAguaLayout.OpacidadInfo);
                canvas.SetExtGState(gsInfo);
                layoutCanvas.SetFont(fontInfo).SetFontSize(MarcaAguaLayout.TamanoFuenteInfo).SetFontColor(ColorConstants.DARK_GRAY);

                var infoY = MarcaAguaLayout.MargenInfoInferior;
                var puntoVentaX = MarcaAguaLayout.MargenInfoIzquierdo;
                var vendedorX = puntoVentaX + MarcaAguaLayout.SeparacionHorizontalInfo;
                var observacionX = vendedorX + MarcaAguaLayout.SeparacionHorizontalInfo;

                if (!string.IsNullOrWhiteSpace(puntoVentaNombre))
                {
                    var parrafoPuntoVenta = CrearParrafoInfo("Punto de venta", puntoVentaNombre);
                    layoutCanvas.ShowTextAligned(parrafoPuntoVenta, puntoVentaX, infoY, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
                }

                if (!string.IsNullOrWhiteSpace(vendedorNombre))
                {
                    var parrafoVendedor = CrearParrafoInfo("Vendedor", vendedorNombre);
                    layoutCanvas.ShowTextAligned(parrafoVendedor, vendedorX, infoY, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
                }

                if (!string.IsNullOrWhiteSpace(observacion))
                {
                    var parrafoObservacion = CrearParrafoInfo("Observación", observacion);
                    layoutCanvas.ShowTextAligned(parrafoObservacion, observacionX, infoY, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
                }
            }

            canvas.RestoreState();
        }

    }

    private static Paragraph CrearParrafoInfo(string etiqueta, string valor)
    {
        var valorNormalizado = NormalizarTextoInfo(valor);
        return new Paragraph()
            .Add(new Text($"{etiqueta}: ").SetBold())
            .Add(new Text(valorNormalizado))
            .SetMargin(0)
            .SetMultipliedLeading(1f)
            .SetWidth(MarcaAguaLayout.AnchoBloqueInfo);
    }

    private static string NormalizarTextoInfo(string? valor)
    {
        if (string.IsNullOrWhiteSpace(valor)) return string.Empty;

        var limpio = valor.Replace("\r", " ").Replace("\n", " ").Trim();
        if (limpio.Length <= MarcaAguaLayout.MaximoCaracteresInfo) return limpio;
        return $"{limpio[..MarcaAguaLayout.MaximoCaracteresInfo]}...";
    }

    private void AplicarSeguridadWindows(string ruta, bool esDirectorio)
    {
        if (!opcionesSeguridad.EnforceWindowsAcl || !RuntimeInformation.IsOSPlatform(OSPlatform.Windows))
            return;

        try
        {
            if (esDirectorio)
            {
                var directorio = new DirectoryInfo(ruta);
                var seguridad = FileSystemAclExtensions.GetAccessControl(directorio);
                var currentUser = WindowsIdentity.GetCurrent().User;
                if (currentUser is null) return;

                seguridad.AddAccessRule(new FileSystemAccessRule(currentUser, FileSystemRights.FullControl, InheritanceFlags.ContainerInherit | InheritanceFlags.ObjectInherit, PropagationFlags.None, AccessControlType.Allow));
                seguridad.AddAccessRule(new FileSystemAccessRule(new SecurityIdentifier(WellKnownSidType.BuiltinAdministratorsSid, null), FileSystemRights.FullControl, InheritanceFlags.ContainerInherit | InheritanceFlags.ObjectInherit, PropagationFlags.None, AccessControlType.Allow));
                seguridad.AddAccessRule(new FileSystemAccessRule(new SecurityIdentifier(WellKnownSidType.LocalSystemSid, null), FileSystemRights.FullControl, InheritanceFlags.ContainerInherit | InheritanceFlags.ObjectInherit, PropagationFlags.None, AccessControlType.Allow));
                FileSystemAclExtensions.SetAccessControl(directorio, seguridad);
            }
            else
            {
                var archivo = new FileInfo(ruta);
                var seguridad = FileSystemAclExtensions.GetAccessControl(archivo);
                var currentUser = WindowsIdentity.GetCurrent().User;
                if (currentUser is null) return;

                seguridad.AddAccessRule(new FileSystemAccessRule(currentUser, FileSystemRights.FullControl, AccessControlType.Allow));
                seguridad.AddAccessRule(new FileSystemAccessRule(new SecurityIdentifier(WellKnownSidType.BuiltinAdministratorsSid, null), FileSystemRights.FullControl, AccessControlType.Allow));
                seguridad.AddAccessRule(new FileSystemAccessRule(new SecurityIdentifier(WellKnownSidType.LocalSystemSid, null), FileSystemRights.FullControl, AccessControlType.Allow));
                FileSystemAclExtensions.SetAccessControl(archivo, seguridad);
            }
        }
        catch (Exception ex)
        {
            logger.LogWarning(ex, "No fue posible aplicar ACL de seguridad al recurso {Ruta}.", ruta);
        }
    }

    private static void EliminarSilencioso(string ruta)
    {
        if (!File.Exists(ruta)) return;
        try
        {
            File.Delete(ruta);
        }
        catch
        {
            // Ignorado: limpieza de mejor esfuerzo en rutas temporales.
        }
    }
}


public class FirmaElectronicaServicio : IFirmaElectronicaServicio
{
    private readonly string rutaRaizFirmas;

    public FirmaElectronicaServicio(IConfiguration configuracion)
    {
        rutaRaizFirmas = configuracion["Storage:FirmasPath"]
            ?? Path.Combine(AppContext.BaseDirectory, "storage", "firmas");
    }

    public async Task<string> GuardarFirmaAsync(int usuarioId, string nombreArchivo, Stream contenido, CancellationToken cancellationToken)
    {
        Directory.CreateDirectory(rutaRaizFirmas);
        var extension = Path.GetExtension(nombreArchivo);
        if (string.IsNullOrWhiteSpace(extension)) extension = ".png";
        var ruta = Path.Combine(rutaRaizFirmas, $"firma_usuario_{usuarioId}{extension}");

        await using var salida = File.Create(ruta);
        await contenido.CopyToAsync(salida, cancellationToken);
        return ruta;
    }
}
