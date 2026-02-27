namespace BalancerX.Application.DTOs;

public record CrearTransferenciaRequest(decimal Monto, int PuntoVentaId, int VendedorId, int BancoId, int CuentaContableId, string? Observacion);
public record TransferenciaResponse(long Id, decimal Monto, int PuntoVentaId, int VendedorId, int BancoId, int CuentaContableId, string? Observacion, string Estado, DateTime CreadoEnUtc, DateTime? ImpresaEnUtc);
public record FiltroTransferenciaRequest(DateTime? FechaDesde, DateTime? FechaHasta, int? PuntoVentaId, int? VendedorId, string? Estado, bool? Impresa);
public record ReimpresionRequest(string PinAdmin, string Razon);
public record ActualizarTransferenciaRequest(decimal Monto, int PuntoVentaId, int VendedorId, int BancoId, int CuentaContableId, string? Observacion, string Estado);

public record SubirPdfResponse(long Id, long TransferenciaId, string NombreOriginal, long TamanoBytes, DateTime SubidoEnUtc);

public record UsuarioAdminResponse(int Id, string Usuario, string Rol, bool Activo, string FirmaElectronica);
public record CrearUsuarioRequest(string Usuario, string Password, string Rol, string? PinAdmin, string FirmaElectronica);
public record EliminarPdfResponse(long TransferenciaId, bool Eliminado);
