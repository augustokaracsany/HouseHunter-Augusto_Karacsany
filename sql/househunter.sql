DROP DATABASE IF EXISTS househunter;
CREATE DATABASE househunter;
USE househunter;

-- Tabla Padre: Credenciales y Discriminador de Rol.
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMINISTRADOR', 'EMPRESA', 'INVITADO') NOT NULL
);

-- Tabla Hija 1: Datos para entes con DNI ( Admin e Invitados. )
CREATE TABLE datos_personas (
    id_usuario INT PRIMARY KEY,
    dni VARCHAR(15) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla Hija 2: Datos para entes con CUIT ( Empresas. )
CREATE TABLE datos_empresas (
    id_usuario INT PRIMARY KEY,
    cuit VARCHAR(20) UNIQUE NOT NULL,
    razon_social VARCHAR(100) NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
);
-- BORRAR LOS INSERTS GENERADOS CON IA.
-- INSERTS DE PRUEBA SIN CONTRASEÑAS HASHEADAS CON BCRYPT.

-- 1. Insertar Administrador (DNI: 11222333)
-- INSERT INTO usuarios (email, password, rol) VALUES ('admin@hotel.com', '1234', 'ADMINISTRADOR');
-- INSERT INTO datos_personas (id_usuario, dni, nombre, apellido) VALUES (1, '11222333', 'Martin', 'Waltar');

-- 2. Insertar Empresa (CUIT: 30-12345678-9)
-- INSERT INTO usuarios (email, password, rol) VALUES ('info@globant.com', 'empresa123', 'EMPRESA');
-- INSERT INTO datos_empresas (id_usuario, cuit, razon_social) VALUES (2, '30-12345678-9', 'Globant S.A.');

-- 3. Insertar Invitado (DNI: 44555666)
-- INSERT INTO usuarios (email, password, rol) VALUES ('invitado@gmail.com', 'invitado123', 'INVITADO');
-- INSERT INTO datos_personas (id_usuario, dni, nombre, apellido) VALUES (3, '44555666', 'Luca', 'Borrelli');

-- INSERTS DE PRUEBA CON CONTRASEÑAS HASHEADAS CON BCRYPT

-- 1. Administrador (Email: admin@hotel.com | Clave real: 1234)
INSERT INTO usuarios (email, password, rol) VALUES ('admin@hotel.com', '$2a$10$K7SgA6R37pE9AunlW7N6duGz8N17e4u/Tq8HbeB3HkRCHYEqE1iW.', 'ADMINISTRADOR');
INSERT INTO datos_personas (id_usuario, dni, nombre, apellido) VALUES (1, '11222333', 'Martin', 'Waltar');

-- 2. Empresa (Email: info@globant.com | Clave real: empresa123)
INSERT INTO usuarios (email, password, rol) VALUES ('info@globant.com', '$2a$10$mRzLUnrYwRE0X9aKsn6pruY.9I7uM1I83LzXQk2jM/YnF57r9wTjG', 'EMPRESA');
INSERT INTO datos_empresas (id_usuario, cuit, razon_social) VALUES (2, '30-12345678-9', 'Globant S.A.');

-- 3. Invitado (Email: invitado@gmail.com | Clave real: invitado123)
INSERT INTO usuarios (email, password, rol) VALUES ('invitado@gmail.com', '$2a$10$wEHLd1R16zS9xYfF5N2L6ee1Bq6xIoxCisX5GZ7XwY3i6VpBghXMC', 'INVITADO');
INSERT INTO datos_personas (id_usuario, dni, nombre, apellido) VALUES (3, '44555666', 'Luca', 'Borrelli');