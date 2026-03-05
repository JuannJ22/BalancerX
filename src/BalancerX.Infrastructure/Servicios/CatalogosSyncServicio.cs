using BalancerX.Application.Contratos;

namespace BalancerX.Infrastructure.Servicios;

/// <summary>
/// Servicio de compatibilidad para entornos que aún inyectan ICatalogosSyncServicio.
/// En modo catálogos por vistas no se requiere sincronización previa.
/// </summary>
public class CatalogosSyncServicio : ICatalogosSyncServicio
{
    public Task SincronizarAsync(CancellationToken cancellationToken)
        => Task.CompletedTask;
}
