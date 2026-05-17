DROP DATABASE IF EXISTS househunter;
CREATE DATABASE househunter;
USE househunter;

-- Tabla Padre: Credenciales y discriminador de rol
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMINISTRADOR', 'EMPRESA', 'INVITADO') NOT NULL
);

-- Tabla Hija 1: Datos para entes con DNI (Admin e Invitados)
CREATE TABLE datos_personas (
    id_usuario INT PRIMARY KEY,
    dni VARCHAR(15) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla Hija 2: Datos para entes con CUIT (Empresas)
CREATE TABLE datos_empresas (
    id_usuario INT PRIMARY KEY,
    cuit VARCHAR(20) UNIQUE NOT NULL,
    razon_social VARCHAR(100) NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- INSERTS DE PRUEBA (Copiá y pegá esto también)

-- 1. Insertar Administrador (DNI: 11222333)
INSERT INTO usuarios (email, password, rol) VALUES ('admin@hotel.com', '1234', 'ADMINISTRADOR');
INSERT INTO datos_personas (id_usuario, dni, nombre, apellido) VALUES (1, '11222333', 'Martin', 'Waltar');

-- 2. Insertar Empresa (CUIT: 30-12345678-9)
INSERT INTO usuarios (email, password, rol) VALUES ('info@globant.com', 'empresa123', 'EMPRESA');
INSERT INTO datos_empresas (id_usuario, cuit, razon_social) VALUES (2, '30-12345678-9', 'Globant S.A.');

-- 3. Insertar Invitado (DNI: 44555666)
INSERT INTO usuarios (email, password, rol) VALUES ('invitado@gmail.com', 'invitado123', 'INVITADO');
INSERT INTO datos_personas (id_usuario, dni, nombre, apellido) VALUES (3, '44555666', 'Juan', 'Perez');