-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 12-11-2024 a las 23:02:39
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
-- Estructura de tabla para la tabla `asdadea_tabla`
--

DROP TABLE IF EXISTS `asdadea_tabla`;
CREATE TABLE IF NOT EXISTS `asdadea_tabla` (
  `id` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `A` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `B` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `C` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Disponibles` int DEFAULT NULL,
  `Estado` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Palabra clave` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Ubicación Física` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asde_tabla`
--

DROP TABLE IF EXISTS `asde_tabla`;
CREATE TABLE IF NOT EXISTS `asde_tabla` (
  `id` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `A` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `B` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `C` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Disponibles` int DEFAULT NULL,
  `Estado` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Palabra clave` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Ubicación Física` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cds_tabla`
--

DROP TABLE IF EXISTS `cds_tabla`;
CREATE TABLE IF NOT EXISTS `cds_tabla` (
  `id` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `A` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `B` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `C` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `D` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `E` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Disponibles` int DEFAULT NULL,
  `Estado` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Palabra clave` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Ubicación Física` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pintos_tabla`
--

DROP TABLE IF EXISTS `pintos_tabla`;
CREATE TABLE IF NOT EXISTS `pintos_tabla` (
  `id` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `A` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `B` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Disponibles` int DEFAULT NULL,
  `Estado` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Palabra clave` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Ubicación Física` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tiposdocumentos`
--

DROP TABLE IF EXISTS `tiposdocumentos`;
CREATE TABLE IF NOT EXISTS `tiposdocumentos` (
  `id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `nombre` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `nombre_tabla` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tiposdocumentos`
--

INSERT INTO `tiposdocumentos` (`id`, `nombre`, `fecha_creacion`, `nombre_tabla`) VALUES
('TIP00001', 'ASDE', '2024-11-12 22:51:13', 'asde_tabla'),
('TIP00002', 'CDS', '2024-11-12 22:58:25', 'cds_tabla');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id_usuario` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `nombre` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `apellido` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `tipo_usuario` enum('Administrador','Profesor','Alumno') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `clave` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `telefono` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_registro` datetime DEFAULT CURRENT_TIMESTAMP,
  `limite_prestamos` int DEFAULT '5',
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `nombre`, `apellido`, `tipo_usuario`, `email`, `clave`, `telefono`, `fecha_registro`, `limite_prestamos`) VALUES
('AD1', 'Admin', 'Ejemplo', 'Administrador', 'admin@colegio.com', 'admin123', '1234567890', '2024-11-04 13:55:34', 10),
('AL1', 'Alumno', 'Colegio', 'Alumno', 'alumno@colegio.com', '$2a$10$.kqu8uKFWIhl9tJdLgTMTuk65DLlC5Q2T8u5Zz3C9JTKdAXleywo6', '1122334455', '2024-11-04 13:55:34', 3),
('PR1', 'Profesor', 'Ejemplo', 'Profesor', 'profesor@colegio.com', 'profesor123', '0987654321', '2024-11-04 13:55:34', 5);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
