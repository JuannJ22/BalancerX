/*
  Seed de desarrollo para BALANCERX.
  Nota: Usa credenciales en texto plano con prefijo {PLAIN}.
*/

SET NOCOUNT ON;

IF NOT EXISTS (SELECT 1 FROM bx.users WHERE username = 'admin')
BEGIN
    INSERT INTO bx.users (username, password_hash, admin_pin_hash, activo, firma_electronica)
    VALUES ('admin', '{PLAIN}Admin123*', '{PLAIN}1234', 1, 'FIRMA ADMIN');
END;

IF NOT EXISTS (SELECT 1 FROM bx.users WHERE username = 'tesoreria')
BEGIN
    INSERT INTO bx.users (username, password_hash, admin_pin_hash, activo, firma_electronica)
    VALUES ('tesoreria', '{PLAIN}Tesoreria123*', NULL, 1, 'FIRMA TESORERIA');
END;

IF NOT EXISTS (SELECT 1 FROM bx.users WHERE username = 'auxiliar')
BEGIN
    INSERT INTO bx.users (username, password_hash, admin_pin_hash, activo, firma_electronica)
    VALUES ('auxiliar', '{PLAIN}Auxiliar123*', NULL, 1, 'FIRMA AUXILIAR');
END;

DECLARE @usuarioAdminId INT = (SELECT id FROM bx.users WHERE username = 'admin');
DECLARE @usuarioTesoreriaId INT = (SELECT id FROM bx.users WHERE username = 'tesoreria');
DECLARE @usuarioAuxiliarId INT = (SELECT id FROM bx.users WHERE username = 'auxiliar');

DECLARE @rolAdminId INT = (SELECT id FROM bx.roles WHERE nombre = 'ADMIN');
DECLARE @rolTesoreriaId INT = (SELECT id FROM bx.roles WHERE nombre = 'TESORERIA');
DECLARE @rolAuxiliarId INT = (SELECT id FROM bx.roles WHERE nombre = 'AUXILIAR');

IF NOT EXISTS (SELECT 1 FROM bx.user_roles WHERE usuario_id = @usuarioAdminId AND rol_id = @rolAdminId)
BEGIN
    INSERT INTO bx.user_roles (usuario_id, rol_id) VALUES (@usuarioAdminId, @rolAdminId);
END;

IF NOT EXISTS (SELECT 1 FROM bx.user_roles WHERE usuario_id = @usuarioTesoreriaId AND rol_id = @rolTesoreriaId)
BEGIN
    INSERT INTO bx.user_roles (usuario_id, rol_id) VALUES (@usuarioTesoreriaId, @rolTesoreriaId);
END;

IF NOT EXISTS (SELECT 1 FROM bx.user_roles WHERE usuario_id = @usuarioAuxiliarId AND rol_id = @rolAuxiliarId)
BEGIN
    INSERT INTO bx.user_roles (usuario_id, rol_id) VALUES (@usuarioAuxiliarId, @rolAuxiliarId);
END;

IF NOT EXISTS (SELECT 1 FROM bx.puntos_venta)
BEGIN
    INSERT INTO bx.puntos_venta (nombre)
    VALUES ('Sucursal Centro'), ('Sucursal Norte'), ('Sucursal Sur');
END;

IF NOT EXISTS (SELECT 1 FROM bx.vendedores)
BEGIN
    INSERT INTO bx.vendedores (nombre)
    VALUES ('Vendedor A'), ('Vendedor B'), ('Vendedor C');
END;


IF NOT EXISTS (SELECT 1 FROM bx.bancos)
BEGIN
    INSERT INTO bx.bancos (nombre) VALUES ('Banco Nacional'), ('Banco Provincial');
END;

IF NOT EXISTS (SELECT 1 FROM bx.cuentas_contables)
BEGIN
    DECLARE @banco1 INT = (SELECT TOP 1 id FROM bx.bancos WHERE nombre = 'Banco Nacional');
    DECLARE @banco2 INT = (SELECT TOP 1 id FROM bx.bancos WHERE nombre = 'Banco Provincial');

    INSERT INTO bx.cuentas_contables (banco_id, numero_cuenta, descripcion)
    VALUES (@banco1, '110-001-0001', 'Cuenta operacional principal'),
           (@banco2, '210-002-0001', 'Cuenta transferencias regionales');
END;
