using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;
using System.Data.Common;

namespace BalancerX.Api.Middleware;

public class MiddlewareErrores
{
    private readonly RequestDelegate siguiente;
    private readonly ILogger<MiddlewareErrores> logger;

    public MiddlewareErrores(RequestDelegate siguiente, ILogger<MiddlewareErrores> logger)
    {
        this.siguiente = siguiente;
        this.logger = logger;
    }

    public async Task InvokeAsync(HttpContext contexto)
    {
        try
        {
            await siguiente(contexto);
        }
        catch (OperationCanceledException) when (contexto.RequestAborted.IsCancellationRequested)
        {
            logger.LogInformation("Solicitud cancelada por el cliente. Ruta: {Ruta}", contexto.Request.Path);
        }
        catch (UnauthorizedAccessException ex)
        {
            logger.LogWarning(ex, "Acceso no autorizado. Ruta: {Ruta}", contexto.Request.Path);
            await EscribirProblema(contexto, StatusCodes.Status401Unauthorized, "No autorizado", string.IsNullOrWhiteSpace(ex.Message) ? "Su sesión no es válida o no tiene permisos para esta operación." : ex.Message);
        }
        catch (InvalidOperationException ex)
        {
            logger.LogWarning(ex, "Operación inválida. Ruta: {Ruta}", contexto.Request.Path);
            await EscribirProblema(contexto, StatusCodes.Status400BadRequest, "Operación inválida", ex.Message);
        }
        catch (DbException ex)
        {
            logger.LogError(ex, "Error de conectividad con base de datos. Ruta: {Ruta}", contexto.Request.Path);
            await EscribirProblema(contexto, StatusCodes.Status503ServiceUnavailable, "Servicio temporalmente no disponible", "No fue posible completar la operación por un problema de conectividad con la base de datos.");
        }
        catch (DbUpdateException)
        {
            logger.LogWarning("Error al persistir datos. Ruta: {Ruta}", contexto.Request.Path);
            await EscribirProblema(contexto, StatusCodes.Status400BadRequest, "No se pudo guardar la información", "Verifique que los datos seleccionados existan y estén relacionados correctamente.");
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Error inesperado. Ruta: {Ruta}", contexto.Request.Path);
            await EscribirProblema(contexto, StatusCodes.Status500InternalServerError, "Error inesperado", "Ocurrió un error interno. Intente nuevamente o contacte al administrador.");
        }
    }

    private static async Task EscribirProblema(HttpContext contexto, int status, string titulo, string detalle)
    {
        if (contexto.Response.HasStarted) return;
        contexto.Response.StatusCode = status;
        var problema = new ProblemDetails { Status = status, Title = titulo, Detail = detalle };
        await contexto.Response.WriteAsJsonAsync(problema);
    }
}

public static class MiddlewareErroresExtension
{
    public static IApplicationBuilder UseMiddlewareErrores(this IApplicationBuilder app) => app.UseMiddleware<MiddlewareErrores>();
}
