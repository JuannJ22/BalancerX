using Microsoft.AspNetCore.Mvc;

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
            await EscribirProblema(contexto, StatusCodes.Status401Unauthorized, "No autorizado", ex.Message);
        }
        catch (InvalidOperationException ex)
        {
            await EscribirProblema(contexto, StatusCodes.Status400BadRequest, "Operación inválida", ex.Message);
        }
        catch (Exception ex)
        {
            await EscribirProblema(contexto, StatusCodes.Status500InternalServerError, "Error inesperado", ex.Message);
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
