/*
  BALANCERX - ALTER V3
  Catálogos operativos para frontend:
  - puntos_venta
  - vendedores
  - bancos y cuentas contables
  Reglas:
  - cuenta contable depende de banco
  - transferencias valida referencias de catálogos
*/
SET NOCOUNT ON;

IF OBJECT_ID('bx.puntos_venta', 'U') IS NULL
BEGIN
    CREATE TABLE bx.puntos_venta (
        id INT IDENTITY(1,1) PRIMARY KEY,
        nombre NVARCHAR(150) NOT NULL UNIQUE
    );
END;

IF OBJECT_ID('bx.vendedores', 'U') IS NULL
BEGIN
    CREATE TABLE bx.vendedores (
        id INT IDENTITY(1,1) PRIMARY KEY,
        nombre NVARCHAR(150) NOT NULL UNIQUE
    );
END;

IF OBJECT_ID('bx.bancos', 'U') IS NULL
BEGIN
    CREATE TABLE bx.bancos (
        id INT IDENTITY(1,1) PRIMARY KEY,
        nombre NVARCHAR(150) NOT NULL UNIQUE
    );
END;

IF OBJECT_ID('bx.cuentas_contables', 'U') IS NULL
BEGIN
    CREATE TABLE bx.cuentas_contables (
        id INT IDENTITY(1,1) PRIMARY KEY,
        banco_id INT NOT NULL,
        numero_cuenta NVARCHAR(80) NOT NULL,
        descripcion NVARCHAR(200) NOT NULL,
        CONSTRAINT FK_cuentas_contables_bancos FOREIGN KEY (banco_id) REFERENCES bx.bancos(id)
    );
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
    INSERT INTO bx.bancos (nombre)
    VALUES ('Banco Nacional'), ('Banco Provincial');
END;

IF NOT EXISTS (SELECT 1 FROM bx.cuentas_contables)
BEGIN
    DECLARE @bancoNacional INT = (SELECT TOP 1 id FROM bx.bancos WHERE nombre = 'Banco Nacional');
    DECLARE @bancoProvincial INT = (SELECT TOP 1 id FROM bx.bancos WHERE nombre = 'Banco Provincial');

    INSERT INTO bx.cuentas_contables (banco_id, numero_cuenta, descripcion)
    VALUES
        (@bancoNacional, '110-001-0001', 'Cuenta operacional principal'),
        (@bancoNacional, '110-001-0002', 'Cuenta operativa secundaria'),
        (@bancoProvincial, '210-002-0001', 'Cuenta transferencias regionales');
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_transferencias_puntos_venta')
BEGIN
    ALTER TABLE bx.transferencias WITH NOCHECK
    ADD CONSTRAINT FK_transferencias_puntos_venta FOREIGN KEY (punto_venta_id) REFERENCES bx.puntos_venta(id);
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_transferencias_vendedores')
BEGIN
    ALTER TABLE bx.transferencias WITH NOCHECK
    ADD CONSTRAINT FK_transferencias_vendedores FOREIGN KEY (vendedor_id) REFERENCES bx.vendedores(id);
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_transferencias_bancos')
BEGIN
    ALTER TABLE bx.transferencias WITH NOCHECK
    ADD CONSTRAINT FK_transferencias_bancos FOREIGN KEY (banco_id) REFERENCES bx.bancos(id);
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_transferencias_cuentas_contables')
BEGIN
    ALTER TABLE bx.transferencias WITH NOCHECK
    ADD CONSTRAINT FK_transferencias_cuentas_contables FOREIGN KEY (cuenta_contable_id) REFERENCES bx.cuentas_contables(id);
END;
GO
