namespace BalancerX.Application.DTOs;

public record CrearTransferenciaRequest(decimal Monto, int PuntoVentaId, int VendedorId, int BancoId, int CuentaContableId, string? Observacion);
public record TransferenciaResponse(long Id, decimal Monto, int PuntoVentaId, int VendedorId, int BancoId, int CuentaContableId, string? Observacion, string Estado, DateTime CreadoEnUtc, DateTime? ImpresaEnUtc);
public record FiltroTransferenciaRequest(DateTime? FechaDesde, DateTime? FechaHasta, int? PuntoVentaId, int? VendedorId, string? Estado, bool? Impresa);
public record ReimpresionRequest(string UsuarioEncargado, string PinEncargado, string Razon);
public record ActualizarTransferenciaRequest(decimal Monto, int PuntoVentaId, int VendedorId, int BancoId, int CuentaContableId, string? Observacion, string Estado);

public record SubirPdfResponse(long Id, long TransferenciaId, string NombreOriginal, long TamanoBytes, DateTime SubidoEnUtc);

public record UsuarioAdminResponse(int Id, string Usuario, int RolId, string Rol, bool Activo, string FirmaElectronica, int? PuntoVentaId);
public record CrearUsuarioRequest(string Usuario, string Password, int RolId, string? PinAdmin, string FirmaElectronica, int? PuntoVentaId);
public record ActualizarRolUsuarioRequest(int RolId);
public record EliminarPdfResponse(long TransferenciaId, bool Eliminado);

public record CambiarPasswordRequest(string PasswordActual, string PasswordNueva);
public record ActualizarFirmaResponse(int UsuarioId, string FirmaElectronica);
