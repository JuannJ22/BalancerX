/*
  BALANCERX - ALTER V6
  Catálogos de bancos/cuentas/vendedores solo por vistas Siigo.
  - Elimina FKs de transferencias hacia tablas físicas.
  - Elimina tablas físicas bx.bancos, bx.cuentas_contables, bx.vendedores.
  - Deja puntos_venta con catálogo fijo solicitado.
*/
SET NOCOUNT ON;
GO

IF OBJECT_ID('bx.transferencias', 'U') IS NOT NULL
BEGIN
    DECLARE @sql NVARCHAR(MAX) = N'';

    SELECT @sql = @sql + N'ALTER TABLE bx.transferencias DROP CONSTRAINT [' + fk.name + N'];'
    FROM sys.foreign_keys fk
    WHERE fk.parent_object_id = OBJECT_ID('bx.transferencias')
      AND fk.referenced_object_id IN (
          OBJECT_ID('bx.bancos'),
          OBJECT_ID('bx.cuentas_contables'),
          OBJECT_ID('bx.vendedores')
      );

    IF LEN(@sql) > 0 EXEC sp_executesql @sql;
END;
GO

IF OBJECT_ID('bx.cuentas_contables', 'U') IS NOT NULL DROP TABLE bx.cuentas_contables;
IF OBJECT_ID('bx.bancos', 'U') IS NOT NULL DROP TABLE bx.bancos;
IF OBJECT_ID('bx.vendedores', 'U') IS NOT NULL DROP TABLE bx.vendedores;
GO

IF OBJECT_ID('bx.puntos_venta', 'U') IS NULL
BEGIN
    CREATE TABLE bx.puntos_venta (
        id INT IDENTITY(1,1) PRIMARY KEY,
        nombre NVARCHAR(150) NOT NULL
    );
END;
GO

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
GO

DECLARE @maxPuntoVentaId INT = (SELECT ISNULL(MAX(id), 0) FROM bx.puntos_venta);
DBCC CHECKIDENT ('bx.puntos_venta', RESEED, @maxPuntoVentaId) WITH NO_INFOMSGS;
GO
