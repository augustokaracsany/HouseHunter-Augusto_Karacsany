-- 1. Crear la base de datos.
CREATE DATABASE IF NOT EXISTS househunter;
USE househunter;

-- 2. Crear la tabla de usuarios.
-- IMPORTANTE: Los valores del campo 'rol' deben coincidir con los de Enum Rol.
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL -- IMPORTANTE: Valores esperados: ADMINISTRADOR, EMPRESA, INVITADO
);

-- 3. Insertar usuarios de testing.
INSERT INTO usuarios (email, password, nombre, rol) 
VALUES ('admin@hotel.com', 'admin123', 'Carlos Gomez', 'ADMINISTRADOR');

INSERT INTO usuarios (email, password, nombre, rol) 
VALUES ('empresa@test.com', 'emp123', 'Eventos S.A.', 'EMPRESA');

INSERT INTO usuarios (email, password, nombre, rol) 
VALUES ('invitado@test.com', 'inv123', 'Laura Fernandez', 'INVITADO');