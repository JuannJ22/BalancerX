using BalancerX.Domain.Entidades;
using Microsoft.EntityFrameworkCore;

namespace BalancerX.Infrastructure.Datos;

public class BalancerXDbContext : DbContext
{
    public BalancerXDbContext(DbContextOptions<BalancerXDbContext> options) : base(options) { }

    public DbSet<Usuario> Usuarios => Set<Usuario>();
    public DbSet<Rol> Roles => Set<Rol>();
    public DbSet<UsuarioRol> UsuariosRoles => Set<UsuarioRol>();
    public DbSet<Transferencia> Transferencias => Set<Transferencia>();
    public DbSet<TransferenciaArchivo> TransferenciasArchivos => Set<TransferenciaArchivo>();
    public DbSet<EventoImpresion> EventosImpresion => Set<EventoImpresion>();
    public DbSet<EventoAuditoria> EventosAuditoria => Set<EventoAuditoria>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.HasDefaultSchema("bx");
        modelBuilder.Entity<Usuario>().ToTable("users");
        modelBuilder.Entity<Rol>().ToTable("roles");
        modelBuilder.Entity<UsuarioRol>().ToTable("user_roles").HasKey(x => new { x.UsuarioId, x.RolId });
        modelBuilder.Entity<UsuarioRol>().HasOne(x => x.Usuario).WithMany(x => x.Roles).HasForeignKey(x => x.UsuarioId);
        modelBuilder.Entity<UsuarioRol>().HasOne(x => x.Rol).WithMany().HasForeignKey(x => x.RolId);
        modelBuilder.Entity<Transferencia>().ToTable("transferencias");
        modelBuilder.Entity<TransferenciaArchivo>().ToTable("transferencia_archivos");
        modelBuilder.Entity<EventoImpresion>().ToTable("print_events");
        modelBuilder.Entity<EventoAuditoria>().ToTable("audit_events");

        modelBuilder.Entity<Usuario>().Property(x => x.UsuarioNombre).HasColumnName("username");
        modelBuilder.Entity<Usuario>().Property(x => x.PasswordHash).HasColumnName("password_hash");
        modelBuilder.Entity<Usuario>().Property(x => x.PinAdminHash).HasColumnName("admin_pin_hash");

        modelBuilder.Entity<UsuarioRol>().Property(x => x.UsuarioId).HasColumnName("usuario_id");
        modelBuilder.Entity<UsuarioRol>().Property(x => x.RolId).HasColumnName("rol_id");

        modelBuilder.Entity<Transferencia>().Property(x => x.PuntoVentaId).HasColumnName("punto_venta_id");
        modelBuilder.Entity<Transferencia>().Property(x => x.VendedorId).HasColumnName("vendedor_id");
        modelBuilder.Entity<Transferencia>().Property(x => x.CreadoPorUsuarioId).HasColumnName("created_by");

        modelBuilder.Entity<TransferenciaArchivo>().Property(x => x.TransferenciaId).HasColumnName("transferencia_id");
        modelBuilder.Entity<TransferenciaArchivo>().Property(x => x.NombreOriginal).HasColumnName("nombre_original");
        modelBuilder.Entity<Transferencia>().Property(x => x.CreadoEnUtc).HasColumnName("created_at");
        modelBuilder.Entity<Transferencia>().Property(x => x.CreadoPorUsuarioId).HasColumnName("created_by");
        modelBuilder.Entity<Transferencia>().Property(x => x.ImpresaEnUtc).HasColumnName("printed_at");

        modelBuilder.Entity<TransferenciaArchivo>().Property(x => x.RutaInterna).HasColumnName("internal_path");
        modelBuilder.Entity<TransferenciaArchivo>().Property(x => x.TamanoBytes).HasColumnName("size_bytes");
        modelBuilder.Entity<TransferenciaArchivo>().Property(x => x.SubidoEnUtc).HasColumnName("uploaded_at");
        modelBuilder.Entity<TransferenciaArchivo>().Property(x => x.SubidoPorUsuarioId).HasColumnName("uploaded_by");

        modelBuilder.Entity<EventoImpresion>().Property(x => x.EventoEnUtc).HasColumnName("event_at");
        modelBuilder.Entity<EventoImpresion>().Property(x => x.EsReimpresion).HasColumnName("is_reprint");
        modelBuilder.Entity<EventoImpresion>().Property(x => x.TransferenciaId).HasColumnName("transferencia_id");
        modelBuilder.Entity<EventoImpresion>().Property(x => x.EjecutadoPorUsuarioId).HasColumnName("executed_by");
        modelBuilder.Entity<EventoImpresion>().Property(x => x.AutorizadoPorUsuarioId).HasColumnName("authorized_by");
        modelBuilder.Entity<EventoImpresion>().Property(x => x.Razon).HasColumnName("razon");

        modelBuilder.Entity<EventoAuditoria>().Property(x => x.Accion).HasColumnName("accion");
        modelBuilder.Entity<EventoAuditoria>().Property(x => x.Entidad).HasColumnName("entidad");
        modelBuilder.Entity<EventoAuditoria>().Property(x => x.EntidadId).HasColumnName("entity_id");
        modelBuilder.Entity<EventoAuditoria>().Property(x => x.Detalle).HasColumnName("detalle");
        modelBuilder.Entity<EventoAuditoria>().Property(x => x.EventoEnUtc).HasColumnName("event_at");
        modelBuilder.Entity<EventoAuditoria>().Property(x => x.EjecutadoPorUsuarioId).HasColumnName("executed_by");
    }
}
