using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BalancerX.Api.Middleware;

public class MiddlewareErrores
{
    private readonly RequestDelegate siguiente;

    public MiddlewareErrores(RequestDelegate siguiente) => this.siguiente = siguiente;

    public async Task InvokeAsync(HttpContext contexto)
    {
        try
        {
            await siguiente(contexto);
        }
        catch (UnauthorizedAccessException ex)
        {
            await EscribirProblema(contexto, StatusCodes.Status401Unauthorized, "No autorizado", string.IsNullOrWhiteSpace(ex.Message) ? "Su sesión no es válida o no tiene permisos para esta operación." : ex.Message);
        }
        catch (InvalidOperationException ex)
        {
            await EscribirProblema(contexto, StatusCodes.Status400BadRequest, "Operación inválida", ex.Message);
        }
        catch (DbUpdateException)
        {
            await EscribirProblema(contexto, StatusCodes.Status400BadRequest, "No se pudo guardar la información", "Verifique que los datos seleccionados existan y estén relacionados correctamente.");
        }
        catch (Exception)
        {
            await EscribirProblema(contexto, StatusCodes.Status500InternalServerError, "Error inesperado", "Ocurrió un error interno. Intente nuevamente o contacte al administrador.");
        }
    }

    private static async Task EscribirProblema(HttpContext contexto, int status, string titulo, string detalle)
    {
        contexto.Response.StatusCode = status;
        var problema = new ProblemDetails { Status = status, Title = titulo, Detail = detalle };
        await contexto.Response.WriteAsJsonAsync(problema);
    }
}

public static class MiddlewareErroresExtension
{
    public static IApplicationBuilder UseMiddlewareErrores(this IApplicationBuilder app) => app.UseMiddleware<MiddlewareErrores>();
}
