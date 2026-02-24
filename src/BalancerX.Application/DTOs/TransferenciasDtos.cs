namespace BalancerX.Application.DTOs;

public record CrearTransferenciaRequest(decimal Monto, int PuntoVentaId, int VendedorId, string? Observacion);
public record TransferenciaResponse(long Id, decimal Monto, int PuntoVentaId, int VendedorId, string? Observacion, string Estado, DateTime CreadoEnUtc, DateTime? ImpresaEnUtc);
public record FiltroTransferenciaRequest(DateTime? FechaDesde, DateTime? FechaHasta, int? PuntoVentaId, int? VendedorId, string? Estado, bool? Impresa);
public record ReimpresionRequest(string PinAdmin, string Razon);
