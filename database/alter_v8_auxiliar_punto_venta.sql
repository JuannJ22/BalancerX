/*
  Agrega asignación opcional de punto de venta a usuarios y
  aplica reglas base para AUXILIAR.
*/

SET NOCOUNT ON;

IF COL_LENGTH('bx.users', 'punto_venta_id') IS NULL
BEGIN
    ALTER TABLE bx.users ADD punto_venta_id INT NULL;
END;

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'FK_users_punto_venta'
      AND parent_object_id = OBJECT_ID('bx.users')
)
BEGIN
    ALTER TABLE bx.users
    ADD CONSTRAINT FK_users_punto_venta
    FOREIGN KEY (punto_venta_id) REFERENCES bx.puntos_venta(id);
END;

UPDATE u
SET u.punto_venta_id = 1
FROM bx.users u
WHERE u.username = 'auxiliar'
  AND u.punto_venta_id IS NULL
  AND EXISTS (SELECT 1 FROM bx.puntos_venta pv WHERE pv.id = 1);
