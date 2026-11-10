-- =============================================
-- Script de creación de la base de datos
-- Gestión de Usuarios - Arquitectura Hexagonal
-- =============================================

CREATE DATABASE IF NOT EXISTS vuelos_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE vuelos_management;

CREATE TABLE IF NOT EXISTS users (
    id          VARCHAR(36)  NOT NULL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('ADMIN', 'MEMBER', 'REVIEWER') NOT NULL,
    status      ENUM('ACTIVE', 'INACTIVE', 'PENDING', 'BLOCKED') NOT NULL DEFAULT 'PENDING',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS vuelos (
    id                  VARCHAR(36)  NOT NULL PRIMARY KEY,
    fecha_compra        DATETIME     NOT NULL,
    fecha_salida        DATETIME     NOT NULL,
    fecha_llegada       DATETIME     NOT NULL,
    agencia_viajes      VARCHAR(100) NOT NULL,
    aerolinea           VARCHAR(100) NOT NULL,
    numero              VARCHAR(10)  NOT NULL,
    estado              ENUM('PROGRAMADO', 'CONFIRMADO', 'EN_VUELO', 'ATERRIZADO', 'CANCELADO', 'RETRASADO')
                        NOT NULL DEFAULT 'PROGRAMADO',
    valor               DECIMAL(14,2) NOT NULL,
    cliente             VARCHAR(100) NOT NULL,
    puesto              VARCHAR(3)   NOT NULL,
    avion               VARCHAR(50)  NOT NULL,
    aeropuerto_salida   VARCHAR(100) NOT NULL,
    aeropuerto_llegada  VARCHAR(100) NOT NULL,
    piloto              VARCHAR(100) NOT NULL,
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_vuelos_numero_fecha_salida_puesto UNIQUE (numero, fecha_salida, puesto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Usuario administrador inicial (password: Admin1234!)
INSERT INTO users (id, name, email, password, role, status)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Administrador',
    'admin@example.com',
    '$2a$12$sfwkajsls.fktQb0Ngw5cOiSiV3z4LIwbucSAy4sw2wyNlxbUBt4q',
    'ADMIN',
    'ACTIVE'
);

