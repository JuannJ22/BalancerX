/*
Ejecutar sobre una base existente para habilitar:
- Firma electrónica por usuario
- Bancos y cuentas contables
- Nuevos campos en transferencias
*/

IF COL_LENGTH('bx.users', 'firma_electronica') IS NULL
BEGIN
    ALTER TABLE bx.users ADD firma_electronica NVARCHAR(255) NULL;
END;
GO

IF OBJECT_ID('bx.bancos', 'U') IS NULL
BEGIN
    CREATE TABLE bx.bancos (
        id INT IDENTITY(1,1) PRIMARY KEY,
        nombre NVARCHAR(150) NOT NULL UNIQUE
    );
END;
GO

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
GO

IF COL_LENGTH('bx.transferencias', 'banco_id') IS NULL
BEGIN
    ALTER TABLE bx.transferencias ADD banco_id INT NULL;
END;
GO

IF COL_LENGTH('bx.transferencias', 'cuenta_contable_id') IS NULL
BEGIN
    ALTER TABLE bx.transferencias ADD cuenta_contable_id INT NULL;
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_transferencias_bancos')
BEGIN
    ALTER TABLE bx.transferencias WITH NOCHECK ADD CONSTRAINT FK_transferencias_bancos FOREIGN KEY (banco_id) REFERENCES bx.bancos(id);
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_transferencias_cuentas_contables')
BEGIN
    ALTER TABLE bx.transferencias WITH NOCHECK ADD CONSTRAINT FK_transferencias_cuentas_contables FOREIGN KEY (cuenta_contable_id) REFERENCES bx.cuentas_contables(id);
END;
GO

-- Semillas mínimas
IF NOT EXISTS (SELECT 1 FROM bx.bancos)
BEGIN
    INSERT INTO bx.bancos (nombre) VALUES ('Banco Nacional'), ('Banco Provincial');
END;

IF NOT EXISTS (SELECT 1 FROM bx.cuentas_contables)
BEGIN
    DECLARE @b1 INT = (SELECT TOP 1 id FROM bx.bancos WHERE nombre = 'Banco Nacional');
    DECLARE @b2 INT = (SELECT TOP 1 id FROM bx.bancos WHERE nombre = 'Banco Provincial');
    INSERT INTO bx.cuentas_contables (banco_id, numero_cuenta, descripcion)
    VALUES (@b1, '110-001-0001', 'Cuenta operacional principal'),
           (@b2, '210-002-0001', 'Cuenta transferencias regionales');
END;
