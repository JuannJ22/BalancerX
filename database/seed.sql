/*
  Seed de desarrollo para BALANCERX.
  Nota: Usa credenciales en texto plano con prefijo {PLAIN}.
*/

SET NOCOUNT ON;

IF NOT EXISTS (SELECT 1 FROM bx.users WHERE username = 'admin')
BEGIN
    INSERT INTO bx.users (username, password_hash, admin_pin_hash, activo, firma_electronica, punto_venta_id)
    VALUES ('admin', '{PLAIN}Admin123*', '{PLAIN}1234', 1, 'FIRMA ADMIN', NULL);
END;

IF NOT EXISTS (SELECT 1 FROM bx.users WHERE username = 'tesoreria')
BEGIN
    INSERT INTO bx.users (username, password_hash, admin_pin_hash, activo, firma_electronica, punto_venta_id)
    VALUES ('tesoreria', '{PLAIN}Tesoreria123*', NULL, 1, 'FIRMA TESORERIA', NULL);
END;

IF NOT EXISTS (SELECT 1 FROM bx.users WHERE username = 'auxiliar')
BEGIN
    INSERT INTO bx.users (username, password_hash, admin_pin_hash, activo, firma_electronica, punto_venta_id)
    VALUES ('auxiliar', '{PLAIN}Auxiliar123*', NULL, 1, 'FIRMA AUXILIAR', 1);
END;

UPDATE bx.users SET punto_venta_id = 1 WHERE username = 'auxiliar' AND punto_venta_id IS NULL;

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

SET IDENTITY_INSERT bx.puntos_venta ON;

IF NOT EXISTS (SELECT 1 FROM bx.puntos_venta WHERE id = 1)
    INSERT INTO bx.puntos_venta (id, nombre) VALUES (1, N'Principal');
ELSE
    UPDATE bx.puntos_venta SET nombre = N'Principal' WHERE id = 1;

IF NOT EXISTS (SELECT 1 FROM bx.puntos_venta WHERE id = 2)
    INSERT INTO bx.puntos_venta (id, nombre) VALUES (2, N'Sucursal');
ELSE
    UPDATE bx.puntos_venta SET nombre = N'Sucursal' WHERE id = 2;

IF NOT EXISTS (SELECT 1 FROM bx.puntos_venta WHERE id = 3)
    INSERT INTO bx.puntos_venta (id, nombre) VALUES (3, N'Calarcá');
ELSE
    UPDATE bx.puntos_venta SET nombre = N'Calarcá' WHERE id = 3;

IF NOT EXISTS (SELECT 1 FROM bx.puntos_venta WHERE id = 4)
    INSERT INTO bx.puntos_venta (id, nombre) VALUES (4, N'Tienda Pintuco');
ELSE
    UPDATE bx.puntos_venta SET nombre = N'Tienda Pintuco' WHERE id = 4;

IF NOT EXISTS (SELECT 1 FROM bx.puntos_venta WHERE id = 5)
    INSERT INTO bx.puntos_venta (id, nombre) VALUES (5, N'Cartera');
ELSE
    UPDATE bx.puntos_venta SET nombre = N'Cartera' WHERE id = 5;

IF NOT EXISTS (SELECT 1 FROM bx.puntos_venta WHERE id = 10)
    INSERT INTO bx.puntos_venta (id, nombre) VALUES (10, N'No identificada');
ELSE
    UPDATE bx.puntos_venta SET nombre = N'No identificada' WHERE id = 10;

SET IDENTITY_INSERT bx.puntos_venta OFF;

DECLARE @maxPuntoVentaId INT = (SELECT ISNULL(MAX(id), 0) FROM bx.puntos_venta);
DBCC CHECKIDENT ('bx.puntos_venta', RESEED, @maxPuntoVentaId) WITH NO_INFOMSGS;
