using System.Diagnostics;
using System.IdentityModel.Tokens.Jwt;
using System.Runtime.InteropServices;
using System.Security.Claims;
using System.Security.Cryptography;
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
        servicios.AddDbContext<BalancerXDbContext>(opciones => opciones.UseSqlServer(configuracion.GetConnectionString("SqlServer")));
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
}

public interface IAdaptadorImpresionWindows
{
    Task<bool> ImprimirPdfAsync(string rutaArchivo, CancellationToken cancellationToken);
}

public class AdaptadorImpresionWindows : IAdaptadorImpresionWindows
{
    private readonly IConfiguration configuracion;
    private readonly ILogger<AdaptadorImpresionWindows> logger;

    public AdaptadorImpresionWindows(IConfiguration configuracion, ILogger<AdaptadorImpresionWindows> logger)
    {
        this.configuracion = configuracion;
        this.logger = logger;
    }

    public async Task<bool> ImprimirPdfAsync(string rutaArchivo, CancellationToken cancellationToken)
    {
        if (!File.Exists(rutaArchivo)) return false;

        var impresora = configuracion["Printing:PrinterName"];
        var comando = configuracion["Printing:CommandTemplate"];

        if (!string.IsNullOrWhiteSpace(comando))
            return await EjecutarComandoAsync(comando, rutaArchivo, impresora, cancellationToken);

        if (RuntimeInformation.IsOSPlatform(OSPlatform.Windows))
        {
            logger.LogWarning("No se configuró Printing:CommandTemplate en Windows. Configure un comando de impresión silenciosa.");
            return false;
        }

        var comandoLinux = string.IsNullOrWhiteSpace(impresora)
            ? "lp \"{file}\""
            : "lp -d \"{printer}\" \"{file}\"";

        return await EjecutarComandoAsync(comandoLinux, rutaArchivo, impresora, cancellationToken);
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
    private readonly string rutaRaiz;
    private readonly ILogger<ArchivoSeguroServicio> logger;

    public ArchivoSeguroServicio(IConfiguration configuracion, ILogger<ArchivoSeguroServicio> logger)
    {
        this.logger = logger;
        rutaRaiz = configuracion["Storage:TransferenciasPath"]
            ?? Path.Combine(AppContext.BaseDirectory, "storage", "transferencias");
    }

    public async Task<TransferenciaArchivo> GuardarPdfAsync(long transferenciaId, string nombreOriginal, Stream contenidoStream, int subidoPorUsuarioId, string? firmaElectronica, string? puntoVentaNombre, string? vendedorNombre, CancellationToken cancellationToken)
    {
        var ahora = DateTime.UtcNow;
        var carpeta = Path.Combine(rutaRaiz, ahora.Year.ToString(), ahora.Month.ToString("00"));
        Directory.CreateDirectory(carpeta);

        var nombreInterno = $"transferencia_{transferenciaId}_{Guid.NewGuid():N}.pdf";
        var rutaInterna = Path.Combine(carpeta, nombreInterno);

        try
        {
            await using (var archivoSalida = File.Create(rutaInterna))
            {
                await contenidoStream.CopyToAsync(archivoSalida, cancellationToken);
            }

            var tamanoOriginal = new FileInfo(rutaInterna).Length;
            if (tamanoOriginal == 0)
                throw new InvalidOperationException("El archivo PDF está vacío o no se pudo leer su contenido.");

            try
            {
                AplicarMarcaAgua(rutaInterna, firmaElectronica, puntoVentaNombre, vendedorNombre);
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

    private static void AplicarMarcaAgua(string rutaArchivo, string? firmaElectronica, string? puntoVentaNombre, string? vendedorNombre)
    {
        var firma = firmaElectronica?.Trim();
        var tieneFirma = !string.IsNullOrWhiteSpace(firma);
        var tieneEtiquetas = !string.IsNullOrWhiteSpace(puntoVentaNombre) || !string.IsNullOrWhiteSpace(vendedorNombre);
        if (!tieneFirma && !tieneEtiquetas) return;

        var temporalAppend = rutaArchivo + ".append.tmp";
        var temporalRewrite = rutaArchivo + ".rewrite.tmp";
        string? temporalFinal = null;

        try
        {
            EstamparPdf(rutaArchivo, temporalAppend, firma, puntoVentaNombre, vendedorNombre, usarAppendMode: true);
            temporalFinal = temporalAppend;
        }
        catch (PdfException)
        {
            EliminarSilencioso(temporalAppend);
            EstamparPdf(rutaArchivo, temporalRewrite, firma, puntoVentaNombre, vendedorNombre, usarAppendMode: false);
            temporalFinal = temporalRewrite;
        }

        File.Delete(rutaArchivo);
        File.Move(temporalFinal!, rutaArchivo);
        EliminarSilencioso(temporalAppend);
        EliminarSilencioso(temporalRewrite);
    }

    private static void EstamparPdf(string rutaArchivo, string temporal, string? firma, string? puntoVentaNombre, string? vendedorNombre, bool usarAppendMode)
    {
        var tieneFirma = !string.IsNullOrWhiteSpace(firma);
        var tieneEtiquetas = !string.IsNullOrWhiteSpace(puntoVentaNombre) || !string.IsNullOrWhiteSpace(vendedorNombre);
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
            var posicionX = pageSize.GetWidth() * 0.58f;
            var posicionY = pageSize.GetHeight() * 0.10f;

            if (tieneFirma)
            {
                if (esImagenFirma)
                {
                    var gsImagen = new PdfExtGState().SetFillOpacity(0.95f);
                    canvas.SetExtGState(gsImagen);
                    var imageData = iText.IO.Image.ImageDataFactory.Create(firma!);
                    var imagen = new Image(imageData).ScaleToFit(pageSize.GetWidth() * 0.22f, pageSize.GetHeight() * 0.09f);
                    imagen.SetFixedPosition(i, posicionX, posicionY);
                    layoutCanvas.Add(imagen);
                }
                else
                {
                    var gsTexto = new PdfExtGState().SetFillOpacity(0.12f);
                    canvas.SetExtGState(gsTexto);
                    layoutCanvas.SetFont(font).SetFontSize(18).SetFontColor(ColorConstants.GRAY);
                    layoutCanvas.ShowTextAligned(new Paragraph(firma!), posicionX + 5, posicionY + 15, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
                }
            }

            if (tieneEtiquetas)
            {
                var gsInfo = new PdfExtGState().SetFillOpacity(0.90f);
                canvas.SetExtGState(gsInfo);
                layoutCanvas.SetFont(fontInfo).SetFontSize(9).SetFontColor(ColorConstants.DARK_GRAY);

                var infoY = pageSize.GetHeight() - 28;
                var infoX = pageSize.GetWidth() - 24;
                if (!string.IsNullOrWhiteSpace(puntoVentaNombre))
                {
                    layoutCanvas.ShowTextAligned(new Paragraph($"Punto de venta: {puntoVentaNombre}"), infoX, infoY, i, TextAlignment.RIGHT, VerticalAlignment.TOP, 0);
                    infoY -= 12;
                }

                if (!string.IsNullOrWhiteSpace(vendedorNombre))
                {
                    layoutCanvas.ShowTextAligned(new Paragraph($"Vendedor: {vendedorNombre}"), infoX, infoY, i, TextAlignment.RIGHT, VerticalAlignment.TOP, 0);
                }
            }

            canvas.RestoreState();
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
