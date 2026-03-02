using System;
using System.Data;
using System.Data.Common;
using BalancerX.Application.Contratos;
using BalancerX.Domain.Entidades;
using BalancerX.Infrastructure.Datos;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;

namespace BalancerX.Infrastructure.Repositorios;

public class UsuarioRepositorio : IUsuarioRepositorio
{
    private readonly BalancerXDbContext contexto;
    private readonly PasswordHasher<string> passwordHasher = new();

    public UsuarioRepositorio(BalancerXDbContext contexto) => this.contexto = contexto;

    public Task<Usuario?> ObtenerPorUsuarioNombreAsync(string usuarioNombre, CancellationToken cancellationToken)
        => ObtenerUsuarioConRolesAsync("username", usuarioNombre, cancellationToken);

    public Task<Usuario?> ObtenerPorIdAsync(int usuarioId, CancellationToken cancellationToken)
        => ObtenerUsuarioConRolesAsync("id", usuarioId.ToString(), cancellationToken);

    public Task<bool> ValidarPinAdminAsync(int usuarioId, string pinAdminPlano, CancellationToken cancellationToken)
    {
        var hash = contexto.Usuarios.Where(x => x.Id == usuarioId).Select(x => x.PinAdminHash).FirstOrDefault();
        var resultado = false;
        if (!string.IsNullOrWhiteSpace(hash))
        {
            resultado = hash.StartsWith("{PLAIN}", StringComparison.Ordinal)
                ? string.Equals(hash[7..], pinAdminPlano, StringComparison.Ordinal)
                : passwordHasher.VerifyHashedPassword("PIN", hash, pinAdminPlano) != PasswordVerificationResult.Failed;
        }
        return Task.FromResult(resultado);
    }

    private async Task<Usuario?> ObtenerUsuarioConRolesAsync(string filtroLogico, string valor, CancellationToken cancellationToken)
    {
        var conexion = contexto.Database.GetDbConnection();
        await EnsureConnectionOpenAsync(conexion, cancellationToken);

        var columnasUsers = await ObtenerColumnasTablaAsync(conexion, "bx", "users", cancellationToken);
        var columnaId = ObtenerColumnaExistente(columnasUsers, "id", "Id");
        var columnaUsername = ObtenerColumnaExistente(columnasUsers, "username", "usuario", "Usuario", "user_name");
        var columnaPassword = ObtenerColumnaExistente(columnasUsers, "password_hash", "PasswordHash", "password");
        var columnaPin = ObtenerColumnaExistente(columnasUsers, "admin_pin_hash", "AdminPinHash", "pin_admin_hash");
        var columnaActivo = ObtenerColumnaExistente(columnasUsers, "activo", "Activo", "is_active", "IsActive");
        var columnaFirma = ObtenerColumnaExistente(columnasUsers, "firma_electronica", "FirmaElectronica", "firma", "signature");

        if (new[] { columnaId, columnaUsername, columnaPassword }.Any(string.IsNullOrWhiteSpace))
        {
            return null;
        }

        var columnaFiltroReal = filtroLogico == "id" ? columnaId : columnaUsername;

        await using var comandoUsuario = conexion.CreateCommand();
        comandoUsuario.CommandText = $@"
SELECT TOP(1)
    [u].[{columnaId}] AS [id],
    [u].[{columnaUsername}] AS [username],
    [u].[{columnaPassword}] AS [password_hash],
    {BuildNullableColumnExpression("u", columnaPin)} AS [admin_pin_hash],
    {BuildBooleanExpression("u", columnaActivo)} AS [activo],
    {BuildNullableColumnExpression("u", columnaFirma)} AS [firma_electronica]
FROM [bx].[users] AS [u]
WHERE [u].[{columnaFiltroReal}] = @valor
  AND {BuildBooleanExpression("u", columnaActivo)} = 1";
        AgregarParametro(comandoUsuario, "@valor", valor);

        await using var readerUsuario = await comandoUsuario.ExecuteReaderAsync(cancellationToken);
        if (!await readerUsuario.ReadAsync(cancellationToken))
        {
            return null;
        }

        var usuario = new Usuario
        {
            Id = readerUsuario.GetInt32(readerUsuario.GetOrdinal("id")),
            UsuarioNombre = readerUsuario.GetString(readerUsuario.GetOrdinal("username")),
            PasswordHash = readerUsuario.GetString(readerUsuario.GetOrdinal("password_hash")),
            PinAdminHash = readerUsuario.IsDBNull(readerUsuario.GetOrdinal("admin_pin_hash")) ? string.Empty : readerUsuario.GetString(readerUsuario.GetOrdinal("admin_pin_hash")),
            Activo = readerUsuario.GetBoolean(readerUsuario.GetOrdinal("activo")),
            FirmaElectronica = readerUsuario.IsDBNull(readerUsuario.GetOrdinal("firma_electronica")) ? string.Empty : readerUsuario.GetString(readerUsuario.GetOrdinal("firma_electronica")),
            Roles = new List<UsuarioRol>()
        };

        await readerUsuario.CloseAsync();

        var columnasUserRoles = await ObtenerColumnasTablaAsync(conexion, "bx", "user_roles", cancellationToken);
        var columnasRoles = await ObtenerColumnasTablaAsync(conexion, "bx", "roles", cancellationToken);
        var columnaUsuarioId = ObtenerColumnaExistente(columnasUserRoles, "usuario_id", "UsuarioId", "user_id", "UserId");
        var columnaRolIdEnUserRoles = ObtenerColumnaExistente(columnasUserRoles, "rol_id", "RolId", "role_id", "RoleId");
        var columnaRolId = ObtenerColumnaExistente(columnasRoles, "id", "Id");
        var columnaRolNombre = ObtenerColumnaExistente(columnasRoles, "nombre", "Nombre", "name", "Name");

        if (new[] { columnaUsuarioId, columnaRolIdEnUserRoles, columnaRolId, columnaRolNombre }.Any(string.IsNullOrWhiteSpace))
        {
            return usuario;
        }

        await using var comandoRoles = conexion.CreateCommand();
        comandoRoles.CommandText = $@"
SELECT
    [r].[{columnaRolId}] AS [rol_id],
    [r].[{columnaRolNombre}] AS [rol_nombre]
FROM [bx].[user_roles] AS [ur]
INNER JOIN [bx].[roles] AS [r] ON [r].[{columnaRolId}] = [ur].[{columnaRolIdEnUserRoles}]
WHERE [ur].[{columnaUsuarioId}] = @usuarioId";
        AgregarParametro(comandoRoles, "@usuarioId", usuario.Id);

        await using var readerRoles = await comandoRoles.ExecuteReaderAsync(cancellationToken);
        while (await readerRoles.ReadAsync(cancellationToken))
        {
            usuario.Roles.Add(new UsuarioRol
            {
                UsuarioId = usuario.Id,
                RolId = readerRoles.GetInt32(readerRoles.GetOrdinal("rol_id")),
                Rol = new Rol
                {
                    Id = readerRoles.GetInt32(readerRoles.GetOrdinal("rol_id")),
                    Nombre = readerRoles.GetString(readerRoles.GetOrdinal("rol_nombre"))
                }
            });
        }

        return usuario;
    }


    public async Task<List<Usuario>> ListarUsuariosAsync(CancellationToken cancellationToken)
        => await contexto.Usuarios.Include(x => x.Roles).ThenInclude(x => x.Rol).OrderBy(x => x.UsuarioNombre).ToListAsync(cancellationToken);

    public async Task<Usuario> CrearUsuarioAsync(Usuario usuario, string rolNombre, CancellationToken cancellationToken)
    {
        var password = string.IsNullOrWhiteSpace(usuario.PasswordHash) ? "Temporal123*" : usuario.PasswordHash;
        var pin = usuario.PinAdminHash;

        usuario.PasswordHash = password.StartsWith("{PLAIN}", StringComparison.Ordinal) ? password : $"{{PLAIN}}{password}";
        if (!string.IsNullOrWhiteSpace(pin))
        {
            usuario.PinAdminHash = pin.StartsWith("{PLAIN}", StringComparison.Ordinal) ? pin : $"{{PLAIN}}{pin}";
        }

        var rol = await contexto.Roles.FirstOrDefaultAsync(x => x.Nombre == rolNombre, cancellationToken)
            ?? throw new InvalidOperationException("Rol no encontrado.");

        contexto.Usuarios.Add(usuario);
        await contexto.SaveChangesAsync(cancellationToken);

        contexto.UsuariosRoles.Add(new UsuarioRol { UsuarioId = usuario.Id, RolId = rol.Id });
        await contexto.SaveChangesAsync(cancellationToken);

        usuario.Roles = new List<UsuarioRol>
        {
            new() { UsuarioId = usuario.Id, RolId = rol.Id, Rol = rol, Usuario = usuario }
        };

        return usuario;
    }

    public async Task<bool> EliminarUsuarioAsync(int usuarioId, CancellationToken cancellationToken)
    {
        var usuario = await contexto.Usuarios.FirstOrDefaultAsync(x => x.Id == usuarioId, cancellationToken);
        if (usuario is null) return false;

        var roles = contexto.UsuariosRoles.Where(x => x.UsuarioId == usuarioId);
        contexto.UsuariosRoles.RemoveRange(roles);
        contexto.Usuarios.Remove(usuario);
        await contexto.SaveChangesAsync(cancellationToken);
        return true;
    }

    public async Task<bool> CambiarPasswordAsync(int usuarioId, string passwordActual, string passwordNueva, CancellationToken cancellationToken)
    {
        var usuario = await contexto.Usuarios.FirstOrDefaultAsync(x => x.Id == usuarioId, cancellationToken);
        if (usuario is null) return false;

        var hashActual = usuario.PasswordHash ?? string.Empty;
        var valido = hashActual.StartsWith("{PLAIN}", StringComparison.Ordinal)
            ? string.Equals(hashActual[7..], passwordActual, StringComparison.Ordinal)
            : passwordHasher.VerifyHashedPassword("PWD", hashActual, passwordActual) != PasswordVerificationResult.Failed;

        if (!valido) return false;

        usuario.PasswordHash = passwordNueva.StartsWith("{PLAIN}", StringComparison.Ordinal) ? passwordNueva : $"{{PLAIN}}{passwordNueva}";
        await contexto.SaveChangesAsync(cancellationToken);
        return true;
    }

    public async Task<Usuario> ActualizarFirmaElectronicaAsync(int usuarioId, string firmaElectronica, CancellationToken cancellationToken)
    {
        var usuario = await contexto.Usuarios.FirstOrDefaultAsync(x => x.Id == usuarioId, cancellationToken)
            ?? throw new InvalidOperationException("Usuario no encontrado.");

        usuario.FirmaElectronica = firmaElectronica;
        await contexto.SaveChangesAsync(cancellationToken);
        return usuario;
    }

    private static async Task<List<string>> ObtenerColumnasTablaAsync(DbConnection conexion, string schema, string tabla, CancellationToken cancellationToken)
    {
        await using var comando = conexion.CreateCommand();
        comando.CommandText = @"
SELECT c.name
FROM sys.columns c
INNER JOIN sys.tables t ON c.object_id = t.object_id
INNER JOIN sys.schemas s ON t.schema_id = s.schema_id
WHERE s.name = @schema AND t.name = @tabla";
        AgregarParametro(comando, "@schema", schema);
        AgregarParametro(comando, "@tabla", tabla);

        var resultado = new List<string>();
        await using var reader = await comando.ExecuteReaderAsync(cancellationToken);
        while (await reader.ReadAsync(cancellationToken))
        {
            resultado.Add(reader.GetString(0));
        }

        return resultado;
    }

    private static string ObtenerColumnaExistente(IEnumerable<string> existentes, params string[] candidatas)
        => candidatas.FirstOrDefault(candidata => existentes.Any(x => string.Equals(x, candidata, StringComparison.OrdinalIgnoreCase))) ?? string.Empty;

    private static string BuildNullableColumnExpression(string alias, string? columna)
        => string.IsNullOrWhiteSpace(columna) ? "CAST(NULL AS nvarchar(255))" : $"[{alias}].[{columna}]";

    private static string BuildBooleanExpression(string alias, string? columna)
        => string.IsNullOrWhiteSpace(columna) ? "CAST(1 AS bit)" : $"CAST([{alias}].[{columna}] AS bit)";

    private static void AgregarParametro(DbCommand comando, string nombre, object valor)
    {
        var parametro = comando.CreateParameter();
        parametro.ParameterName = nombre;
        parametro.Value = valor;
        comando.Parameters.Add(parametro);
    }

    private static async Task EnsureConnectionOpenAsync(DbConnection conexion, CancellationToken cancellationToken)
    {
        if (conexion.State != ConnectionState.Open)
        {
            await conexion.OpenAsync(cancellationToken);
        }
    }
}
