IF OBJECT_ID('bx.print_destinations', 'U') IS NULL
BEGIN
    CREATE TABLE bx.print_destinations (
        id INT IDENTITY(1,1) PRIMARY KEY,
        punto_venta_id INT NULL,
        usuario_id INT NULL,
        terminal_id NVARCHAR(100) NULL,
        printer_name NVARCHAR(255) NOT NULL,
        activo BIT NOT NULL DEFAULT 1,
        CONSTRAINT FK_print_destinations_punto_venta FOREIGN KEY (punto_venta_id) REFERENCES bx.puntos_venta(id),
        CONSTRAINT FK_print_destinations_usuario FOREIGN KEY (usuario_id) REFERENCES bx.users(id)
    );
END;
GO
