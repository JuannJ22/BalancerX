namespace BalancerX.Domain.Entidades;

public class Usuario
{
    public int Id { get; set; }
    public string UsuarioNombre { get; set; } = string.Empty;
    public string PasswordHash { get; set; } = string.Empty;
    public string PinAdminHash { get; set; } = string.Empty;
    public bool Activo { get; set; } = true;
    public List<UsuarioRol> Roles { get; set; } = new();
}

public class Rol
{
    public int Id { get; set; }
    public string Nombre { get; set; } = string.Empty;
}

public class UsuarioRol
{
    public int UsuarioId { get; set; }
    public Usuario Usuario { get; set; } = null!;
    public int RolId { get; set; }
    public Rol Rol { get; set; } = null!;
}

public class PuntoVenta { public int Id { get; set; } public string Nombre { get; set; } = string.Empty; }
public class Vendedor { public int Id { get; set; } public string Nombre { get; set; } = string.Empty; }

public class Transferencia
{
    public long Id { get; set; }
    public decimal Monto { get; set; }
    public int PuntoVentaId { get; set; }
    public int VendedorId { get; set; }
    public string? Observacion { get; set; }
    public string Estado { get; set; } = "CREADA";
    public DateTime CreadoEnUtc { get; set; } = DateTime.UtcNow;
    public int CreadoPorUsuarioId { get; set; }
    public DateTime? ImpresaEnUtc { get; set; }
    public List<TransferenciaArchivo> Archivos { get; set; } = new();
}

public class TransferenciaArchivo
{
    public long Id { get; set; }
    public long TransferenciaId { get; set; }
    public Transferencia Transferencia { get; set; } = null!;
    public string NombreOriginal { get; set; } = string.Empty;
    public string RutaInterna { get; set; } = string.Empty;
    public string Sha256 { get; set; } = string.Empty;
    public long TamanoBytes { get; set; }
    public DateTime SubidoEnUtc { get; set; } = DateTime.UtcNow;
    public int SubidoPorUsuarioId { get; set; }
}

public class EventoImpresion
{
    public long Id { get; set; }
    public long TransferenciaId { get; set; }
    public DateTime EventoEnUtc { get; set; } = DateTime.UtcNow;
    public bool EsReimpresion { get; set; }
    public int EjecutadoPorUsuarioId { get; set; }
    public int? AutorizadoPorUsuarioId { get; set; }
    public string? Razon { get; set; }
}

public class EventoAuditoria
{
    public long Id { get; set; }
    public string Accion { get; set; } = string.Empty;
    public string Entidad { get; set; } = string.Empty;
    public string EntidadId { get; set; } = string.Empty;
    public string Detalle { get; set; } = string.Empty;
    public int EjecutadoPorUsuarioId { get; set; }
    public DateTime EventoEnUtc { get; set; } = DateTime.UtcNow;
}
