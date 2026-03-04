using BalancerX.Application.Contratos;
using BalancerX.Infrastructure.Datos;
using Microsoft.Data.SqlClient;
using Microsoft.EntityFrameworkCore;

namespace BalancerX.Infrastructure.Servicios;

public class CatalogosSyncServicio : ICatalogosSyncServicio
{
    private static readonly SemaphoreSlim Candado = new(1, 1);
    private static DateTime ultimaSincronizacionUtc = DateTime.MinValue;
    private static readonly TimeSpan VentanaMinima = TimeSpan.FromMinutes(2);

    private readonly BalancerXDbContext contexto;

    public CatalogosSyncServicio(BalancerXDbContext contexto)
    {
        this.contexto = contexto;
    }

    public async Task SincronizarAsync(CancellationToken cancellationToken)
    {
        if (DateTime.UtcNow - ultimaSincronizacionUtc < VentanaMinima)
            return;

        await Candado.WaitAsync(cancellationToken);
        try
        {
            if (DateTime.UtcNow - ultimaSincronizacionUtc < VentanaMinima)
                return;

            await contexto.Database.ExecuteSqlRawAsync("EXEC bx.sp_sincronizar_catalogos_desde_siigo @BaseOrigen = N'SiigoCat';", cancellationToken);
            ultimaSincronizacionUtc = DateTime.UtcNow;
        }
        catch (SqlException ex) when (ex.Message.Contains("sp_sincronizar_catalogos_desde_siigo", StringComparison.OrdinalIgnoreCase))
        {
            // Entorno sin script de sincronización instalado.
        }
        finally
        {
            Candado.Release();
        }
    }
}
