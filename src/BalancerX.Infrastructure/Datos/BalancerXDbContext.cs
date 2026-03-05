using BalancerX.Domain.Entidades;
using Microsoft.EntityFrameworkCore;

namespace BalancerX.Infrastructure.Datos;

public class BalancerXDbContext : DbContext
{
    public BalancerXDbContext(DbContextOptions<BalancerXDbContext> options) : base(options) { }

    public DbSet<Usuario> Usuarios => Set<Usuario>();
    public DbSet<Rol> Roles => Set<Rol>();
    public DbSet<UsuarioRol> UsuariosRoles => Set<UsuarioRol>();
    public DbSet<PuntoVenta> PuntosVenta => Set<PuntoVenta>();
    public DbSet<Vendedor> Vendedores => Set<Vendedor>();
    public DbSet<Banco> Bancos => Set<Banco>();
    public DbSet<CuentaContable> CuentasContables => Set<CuentaContable>();
    public DbSet<Transferencia> Transferencias => Set<Transferencia>();
    public DbSet<TransferenciaArchivo> TransferenciasArchivos => Set<TransferenciaArchivo>();
    public DbSet<EventoImpresion> EventosImpresion => Set<EventoImpresion>();
    public DbSet<EventoAuditoria> EventosAuditoria => Set<EventoAuditoria>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.HasDefaultSchema("bx");

        var entidadUsuario = modelBuilder.Entity<Usuario>();
        entidadUsuario.ToTable("users");
        entidadUsuario.Property(x => x.Id).HasColumnName("id");
        entidadUsuario.Property(x => x.UsuarioNombre).HasColumnName("username");
        entidadUsuario.Property(x => x.PasswordHash).HasColumnName("password_hash");
        entidadUsuario.Property(x => x.PinAdminHash).HasColumnName("admin_pin_hash");
        entidadUsuario.Property(x => x.Activo).HasColumnName("activo");
        entidadUsuario.Property(x => x.FirmaElectronica).HasColumnName("firma_electronica");

        var entidadRol = modelBuilder.Entity<Rol>();
        entidadRol.ToTable("roles");
        entidadRol.Property(x => x.Id).HasColumnName("id");
        entidadRol.Property(x => x.Nombre).HasColumnName("nombre");

        var entidadUsuarioRol = modelBuilder.Entity<UsuarioRol>();
        entidadUsuarioRol.ToTable("user_roles").HasKey(x => new { x.UsuarioId, x.RolId });
        entidadUsuarioRol.Property(x => x.UsuarioId).HasColumnName("usuario_id");
        entidadUsuarioRol.Property(x => x.RolId).HasColumnName("rol_id");
        entidadUsuarioRol.HasOne(x => x.Usuario).WithMany(x => x.Roles).HasForeignKey(x => x.UsuarioId);
        entidadUsuarioRol.HasOne(x => x.Rol).WithMany().HasForeignKey(x => x.RolId);



        var entidadPuntoVenta = modelBuilder.Entity<PuntoVenta>();
        entidadPuntoVenta.ToTable("puntos_venta");
        entidadPuntoVenta.Property(x => x.Id).HasColumnName("id");
        entidadPuntoVenta.Property(x => x.Nombre).HasColumnName("nombre");

        var entidadVendedor = modelBuilder.Entity<Vendedor>();
        entidadVendedor.ToView("vw_vendedores_siigo");
        entidadVendedor.HasKey(x => x.Id);
        entidadVendedor.Property(x => x.Id).HasColumnName("id");
        entidadVendedor.Property(x => x.Nombre).HasColumnName("nombre");

        var entidadBanco = modelBuilder.Entity<Banco>();
        entidadBanco.ToView("vw_bancos_siigo");
        entidadBanco.HasKey(x => x.Id);
        entidadBanco.Property(x => x.Id).HasColumnName("id");
        entidadBanco.Property(x => x.Nombre).HasColumnName("nombre");

        var entidadCuentaContable = modelBuilder.Entity<CuentaContable>();
        entidadCuentaContable.ToView("vw_cuentas_contables_siigo");
        entidadCuentaContable.HasKey(x => x.Id);
        entidadCuentaContable.Property(x => x.Id).HasColumnName("Id");
        entidadCuentaContable.Property(x => x.BancoId).HasColumnName("BancoId");
        entidadCuentaContable.Property(x => x.NumeroCuenta).HasColumnName("NumeroCuenta");
        entidadCuentaContable.Property(x => x.Descripcion).HasColumnName("Descripcion");

        var entidadTransferencia = modelBuilder.Entity<Transferencia>();
        entidadTransferencia.ToTable("transferencias");
        entidadTransferencia.Property(x => x.Id).HasColumnName("id");
        entidadTransferencia.Property(x => x.Monto).HasColumnName("monto").HasPrecision(18, 2);
        entidadTransferencia.Property(x => x.PuntoVentaId).HasColumnName("punto_venta_id");
        entidadTransferencia.Property(x => x.VendedorId).HasColumnName("vendedor_id");
        entidadTransferencia.Property(x => x.BancoId).HasColumnName("banco_id");
        entidadTransferencia.Property(x => x.CuentaContableId).HasColumnName("cuenta_contable_id");
        entidadTransferencia.Property(x => x.Observacion).HasColumnName("observacion");
        entidadTransferencia.Property(x => x.Estado).HasColumnName("estado");
        entidadTransferencia.Property(x => x.CreadoEnUtc).HasColumnName("created_at");
        entidadTransferencia.Property(x => x.CreadoPorUsuarioId).HasColumnName("created_by");
        entidadTransferencia.Property(x => x.ImpresaEnUtc).HasColumnName("printed_at");

        var entidadTransferenciaArchivo = modelBuilder.Entity<TransferenciaArchivo>();
        entidadTransferenciaArchivo.ToTable("transferencia_archivos");
        entidadTransferenciaArchivo.Property(x => x.Id).HasColumnName("id");
        entidadTransferenciaArchivo.Property(x => x.TransferenciaId).HasColumnName("transferencia_id");
        entidadTransferenciaArchivo.Property(x => x.NombreOriginal).HasColumnName("nombre_original");
        entidadTransferenciaArchivo.Property(x => x.RutaInterna).HasColumnName("internal_path");
        entidadTransferenciaArchivo.Property(x => x.Sha256).HasColumnName("sha256");
        entidadTransferenciaArchivo.Property(x => x.TamanoBytes).HasColumnName("size_bytes");
        entidadTransferenciaArchivo.Property(x => x.SubidoEnUtc).HasColumnName("uploaded_at");
        entidadTransferenciaArchivo.Property(x => x.SubidoPorUsuarioId).HasColumnName("uploaded_by");

        var entidadEventoImpresion = modelBuilder.Entity<EventoImpresion>();
        entidadEventoImpresion.ToTable("print_events");
        entidadEventoImpresion.Property(x => x.Id).HasColumnName("id");
        entidadEventoImpresion.Property(x => x.TransferenciaId).HasColumnName("transferencia_id");
        entidadEventoImpresion.Property(x => x.EventoEnUtc).HasColumnName("event_at");
        entidadEventoImpresion.Property(x => x.EsReimpresion).HasColumnName("is_reprint");
        entidadEventoImpresion.Property(x => x.EjecutadoPorUsuarioId).HasColumnName("executed_by");
        entidadEventoImpresion.Property(x => x.AutorizadoPorUsuarioId).HasColumnName("authorized_by");
        entidadEventoImpresion.Property(x => x.Razon).HasColumnName("razon");

        var entidadEventoAuditoria = modelBuilder.Entity<EventoAuditoria>();
        entidadEventoAuditoria.ToTable("audit_events");
        entidadEventoAuditoria.Property(x => x.Id).HasColumnName("id");
        entidadEventoAuditoria.Property(x => x.Accion).HasColumnName("accion");
        entidadEventoAuditoria.Property(x => x.Entidad).HasColumnName("entidad");
        entidadEventoAuditoria.Property(x => x.EntidadId).HasColumnName("entity_id");
        entidadEventoAuditoria.Property(x => x.Detalle).HasColumnName("detalle");
        entidadEventoAuditoria.Property(x => x.EventoEnUtc).HasColumnName("event_at");
        entidadEventoAuditoria.Property(x => x.EjecutadoPorUsuarioId).HasColumnName("executed_by");
    }
}
