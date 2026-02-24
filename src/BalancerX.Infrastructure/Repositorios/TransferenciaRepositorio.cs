using BalancerX.Application.Contratos;
using BalancerX.Application.DTOs;
using BalancerX.Domain.Entidades;
using BalancerX.Infrastructure.Datos;
using Microsoft.EntityFrameworkCore;

namespace BalancerX.Infrastructure.Repositorios;

public class TransferenciaRepositorio : ITransferenciaRepositorio
{
    private readonly BalancerXDbContext contexto;

    public TransferenciaRepositorio(BalancerXDbContext contexto) => this.contexto = contexto;

    public async Task<Transferencia> CrearAsync(Transferencia transferencia, CancellationToken cancellationToken)
    {
        contexto.Transferencias.Add(transferencia);
        await contexto.SaveChangesAsync(cancellationToken);
        return transferencia;
    }

    public Task<Transferencia?> ObtenerPorIdAsync(long transferenciaId, CancellationToken cancellationToken)
        => contexto.Transferencias.FirstOrDefaultAsync(x => x.Id == transferenciaId, cancellationToken);

    public async Task<List<Transferencia>> ListarAsync(FiltroTransferenciaRequest filtroTransferenciaRequest, CancellationToken cancellationToken)
    {
        var consulta = contexto.Transferencias.AsQueryable();
        if (filtroTransferenciaRequest.FechaDesde.HasValue) consulta = consulta.Where(x => x.CreadoEnUtc >= filtroTransferenciaRequest.FechaDesde.Value);
        if (filtroTransferenciaRequest.FechaHasta.HasValue) consulta = consulta.Where(x => x.CreadoEnUtc <= filtroTransferenciaRequest.FechaHasta.Value);
        if (filtroTransferenciaRequest.PuntoVentaId.HasValue) consulta = consulta.Where(x => x.PuntoVentaId == filtroTransferenciaRequest.PuntoVentaId.Value);
        if (filtroTransferenciaRequest.VendedorId.HasValue) consulta = consulta.Where(x => x.VendedorId == filtroTransferenciaRequest.VendedorId.Value);
        if (!string.IsNullOrWhiteSpace(filtroTransferenciaRequest.Estado)) consulta = consulta.Where(x => x.Estado == filtroTransferenciaRequest.Estado);
        if (filtroTransferenciaRequest.Impresa.HasValue) consulta = filtroTransferenciaRequest.Impresa.Value ? consulta.Where(x => x.ImpresaEnUtc != null) : consulta.Where(x => x.ImpresaEnUtc == null);
        return await consulta.OrderByDescending(x => x.CreadoEnUtc).ToListAsync(cancellationToken);
    }

    public async Task<bool> MarcarImpresaPrimeraVezAsync(long transferenciaId, DateTime fechaUtc, CancellationToken cancellationToken)
    {
        await using var transaccion = await contexto.Database.BeginTransactionAsync(System.Data.IsolationLevel.Serializable, cancellationToken);
        var filas = await contexto.Database.ExecuteSqlInterpolatedAsync($"UPDATE bx.transferencias SET printed_at = {fechaUtc} WHERE id = {transferenciaId} AND printed_at IS NULL", cancellationToken);
        await transaccion.CommitAsync(cancellationToken);
        return filas == 1;
    }

    public async Task GuardarEventoImpresionAsync(EventoImpresion eventoImpresion, CancellationToken cancellationToken)
    {
        contexto.EventosImpresion.Add(eventoImpresion);
        await contexto.SaveChangesAsync(cancellationToken);
    }

    public async Task GuardarEventoAuditoriaAsync(EventoAuditoria eventoAuditoria, CancellationToken cancellationToken)
    {
        contexto.EventosAuditoria.Add(eventoAuditoria);
        await contexto.SaveChangesAsync(cancellationToken);
    }

    public async Task<TransferenciaArchivo> GuardarArchivoAsync(TransferenciaArchivo transferenciaArchivo, CancellationToken cancellationToken)
    {
        contexto.TransferenciasArchivos.Add(transferenciaArchivo);
        await contexto.SaveChangesAsync(cancellationToken);
        return transferenciaArchivo;
    }

    public Task<TransferenciaArchivo?> ObtenerArchivoPorTransferenciaAsync(long transferenciaId, CancellationToken cancellationToken)
        => contexto.TransferenciasArchivos.OrderByDescending(x => x.SubidoEnUtc).FirstOrDefaultAsync(x => x.TransferenciaId == transferenciaId, cancellationToken);
}
