-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 11-11-2024 a las 23:00:18
-- Versión del servidor: 8.3.0
-- Versión de PHP: 8.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `biblioteca_colegio`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cd`
--

DROP TABLE IF EXISTS `cd`;
CREATE TABLE IF NOT EXISTS `cd` (
  `id` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha` date DEFAULT NULL,
  `a` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `b` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Disponibles` int DEFAULT NULL,
  `Estado` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Palabra clave` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Ubicación Física` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `configuracion`
--

DROP TABLE IF EXISTS `configuracion`;
CREATE TABLE IF NOT EXISTS `configuracion` (
  `id_configuracion` int NOT NULL AUTO_INCREMENT,
  `clave` varchar(50) NOT NULL,
  `valor` varchar(255) NOT NULL,
  PRIMARY KEY (`id_configuracion`),
  UNIQUE KEY `clave` (`clave`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `configuracion`
--

INSERT INTO `configuracion` (`id_configuracion`, `clave`, `valor`) VALUES
(1, 'mora_diaria', '0.50'),
(2, 'limite_prestamos', '5');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `libros`
--

DROP TABLE IF EXISTS `libros`;
CREATE TABLE IF NOT EXISTS `libros` (
  `id` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Título` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Autor(es)` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Categoría` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Fecha de Publicación` date DEFAULT NULL,
  `Editorial` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ISBN` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Disponibles` int DEFAULT NULL,
  `Estado` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Palabra clave` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Ubicación Física` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `libros`
--

INSERT INTO `libros` (`id`, `Título`, `Autor(es)`, `Categoría`, `Fecha de Publicación`, `Editorial`, `ISBN`, `Disponibles`, `Estado`, `Palabra clave`, `Ubicación Física`) VALUES
('LIB001', 'aaaa', 'aaaa', 'aaaa', '2024-11-11', 'aaa', 'aaaa', 15, 'aaaa', 'aaaa', 'aaaaa'),
('LIB002', 'aaaa', 'aaa', 'aaa', '2024-11-11', 'aaaa', 'aaaa', 10, 'aaa', 'aaaa', 'aaa'),
('LIB003', 'asdasd', 'asdasdasda', 'asdasd', '2024-11-11', 'asdasdasd', 'sdasda', 0, 'sdasdasdasd', 'asdasdasd', 'asdasdasd'),
('LIB00004', 'AAAA', 'AAA', 'AAA', '2024-11-11', 'AAA', 'AAA', 12, 'AAA', 'AAA', 'AAA');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `moras`
--

DROP TABLE IF EXISTS `moras`;
CREATE TABLE IF NOT EXISTS `moras` (
  `id_mora` int NOT NULL AUTO_INCREMENT,
  `id_prestamo` int DEFAULT NULL,
  `dias_retraso` int NOT NULL,
  `monto_mora` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_mora`),
  KEY `id_prestamo` (`id_prestamo`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `prestamos`
--

DROP TABLE IF EXISTS `prestamos`;
CREATE TABLE IF NOT EXISTS `prestamos` (
  `id_prestamo` int NOT NULL AUTO_INCREMENT,
  `id_documento` int DEFAULT NULL,
  `id_usuario` varchar(10) DEFAULT NULL,
  `fecha_prestamo` datetime DEFAULT CURRENT_TIMESTAMP,
  `fecha_devolucion` datetime DEFAULT NULL,
  `estado_prestamo` enum('En curso','Devuelto','Con mora') DEFAULT 'En curso',
  PRIMARY KEY (`id_prestamo`),
  KEY `id_documento` (`id_documento`),
  KEY `id_usuario` (`id_usuario`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

DROP TABLE IF EXISTS `productos`;
CREATE TABLE IF NOT EXISTS `productos` (
  `id_producto` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(100) NOT NULL,
  `precio` decimal(5,2) NOT NULL,
  `unidades_disponibles` int NOT NULL DEFAULT '0',
  `id_tipo_producto` int NOT NULL,
  `estado` enum('Disponible','Prestado','Por Ingresar') DEFAULT NULL,
  PRIMARY KEY (`id_producto`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tiposdocumentos`
--

DROP TABLE IF EXISTS `tiposdocumentos`;
CREATE TABLE IF NOT EXISTS `tiposdocumentos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `tiposdocumentos`
--

INSERT INTO `tiposdocumentos` (`id`, `nombre`, `fecha_creacion`) VALUES
(5, 'LIBROS', '2024-11-11 16:36:47');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id_usuario` varchar(10) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `apellido` varchar(255) DEFAULT NULL,
  `tipo_usuario` enum('Administrador','Profesor','Alumno') NOT NULL,
  `email` varchar(100) NOT NULL,
  `clave` varchar(255) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `fecha_registro` datetime DEFAULT CURRENT_TIMESTAMP,
  `limite_prestamos` int DEFAULT '5',
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `email` (`email`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `nombre`, `apellido`, `tipo_usuario`, `email`, `clave`, `telefono`, `fecha_registro`, `limite_prestamos`) VALUES
('AD1', 'Admin', 'Ejemplo', 'Administrador', 'admin@colegio.com', 'admin123', '1234567890', '2024-11-04 13:55:34', 10),
('PR1', 'Profesor ', 'Ejemplo', 'Profesor', 'profesor@colegio.com', 'profesor123', '0987654321', '2024-11-04 13:55:34', 5),
('AL1', 'Alumno', 'Colegio', 'Alumno', 'alumno@colegio.com', '$2a$10$.kqu8uKFWIhl9tJdLgTMTuk65DLlC5Q2T8u5Zz3C9JTKdAXleywo6', '1122334455', '2024-11-04 13:55:34', 3);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
