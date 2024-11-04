CREATE DATABASE IF NOT EXISTS biblioteca_colegio;
USE biblioteca_colegio;

-- Tabla TiposDocumento: Define los diferentes tipos de documentos
CREATE TABLE TiposDocumento (
    id_tipo_documento INT AUTO_INCREMENT PRIMARY KEY,
    nombre_tipo VARCHAR(50) NOT NULL,
    campos_requeridos JSON NOT NULL
);

-- Tabla Documentos: Almacena la información general de cada documento
CREATE TABLE Documentos (
    id_documento INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento_id INT,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255),
    editorial VARCHAR(255),
    ano_publicacion YEAR,
    ubicacion VARCHAR(100),
    estado ENUM('Nuevo', 'Bueno', 'Dañado') DEFAULT 'Bueno',
    cantidad_total INT DEFAULT 1,
    FOREIGN KEY (tipo_documento_id) REFERENCES TiposDocumento(id_tipo_documento)
);

-- Tabla DetallesDocumento: Almacena los atributos específicos para cada documento
CREATE TABLE DetallesDocumento (
    id_documento INT,
    campo VARCHAR(100) NOT NULL,
    valor VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_documento, campo), -- Clave compuesta
    FOREIGN KEY (id_documento) REFERENCES Documentos(id_documento) ON DELETE CASCADE
);

-- Tabla Usuarios: Almacena la información de los usuarios del sistema
CREATE TABLE Usuarios (
    id_usuario VARCHAR(10) PRIMARY KEY, -- VARCHAR para los IDs personalizados
    nombre VARCHAR(255) NOT NULL,
    tipo_usuario ENUM('Administrador', 'Profesor', 'Alumno') NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    clave VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    limite_prestamos INT DEFAULT 5 -- Límite de préstamos permitido
);

-- Tabla Prestamos: Almacena la información sobre los préstamos realizados
CREATE TABLE Prestamos (
    id_prestamo INT AUTO_INCREMENT PRIMARY KEY,
    id_documento INT,
    id_usuario VARCHAR(10),
    fecha_prestamo DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_devolucion DATETIME,
    estado_prestamo ENUM('En curso', 'Devuelto', 'Con mora') DEFAULT 'En curso',
    FOREIGN KEY (id_documento) REFERENCES Documentos(id_documento),
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario)
);

-- Tabla Moras: Almacena la información sobre las moras generadas por retrasos en la devolución
CREATE TABLE Moras (
    id_mora INT AUTO_INCREMENT PRIMARY KEY,
    id_prestamo INT,
    dias_retraso INT NOT NULL,
    monto_mora DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_prestamo) REFERENCES Prestamos(id_prestamo) ON DELETE CASCADE
);

-- Tabla Configuracion: Almacena configuraciones globales del sistema
CREATE TABLE Configuracion (
    id_configuracion INT AUTO_INCREMENT PRIMARY KEY,
    clave VARCHAR(50) UNIQUE NOT NULL,
    valor VARCHAR(255) NOT NULL
);

-- Insertar algunos tipos de documentos por defecto
INSERT INTO TiposDocumento (nombre_tipo, campos_requeridos) VALUES
('Libro', '{"genero": "string", "idioma": "string", "numero_paginas": "integer"}'),
('Revista', '{"volumen": "string", "numero_edicion": "string", "fecha_publicacion": "date"}'),
('CD', '{"contenido": "string", "duracion": "integer", "idioma": "string"}'),
('Tesis', '{"carrera_academica": "string", "nombre_tutor": "string", "resumen": "string"}');

-- Insertar usuarios de ejemplo en la tabla Usuarios
INSERT INTO Usuarios (id_usuario, nombre, tipo_usuario, email, clave, telefono, fecha_registro, limite_prestamos) VALUES
('AD1', 'Admin Ejemplo', 'Administrador', 'admin@colegio.com', 'admin123', '1234567890', CURRENT_TIMESTAMP, 10),
('PR1', 'Profesor Ejemplo', 'Profesor', 'profesor@colegio.com', 'profesor123', '0987654321', CURRENT_TIMESTAMP, 5), -- Corregido a PR1
('AL1', 'Alumno Ejemplo', 'Alumno', 'alumno@colegio.com', 'alumno123', '1122334455', CURRENT_TIMESTAMP, 3);

-- Insertar configuración inicial para mora diaria y límite de préstamos por usuario
INSERT INTO Configuracion (clave, valor) VALUES
('mora_diaria', '0.50'),
('limite_prestamos', '5');
