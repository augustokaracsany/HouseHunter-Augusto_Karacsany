-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 05-06-2026 a las 02:31:29
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `househunter`
--

-- --------------------------------------------------------



--
-- Estructura de tabla para la tabla `actividades`
--

CREATE TABLE `actividades` (
  `id` int(11) NOT NULL,
  `id_reserva` int(11) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `importancia` enum('ALTA','MEDIA','BAJA') NOT NULL,
  `categoria` enum('COWORK','CHARLAS','RECREACION','DESCANSO','VARIADOS','OTROS') NOT NULL,
  `hora_actividad` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `actividades`
--

INSERT INTO `actividades` (`id`, `id_reserva`, `nombre`, `descripcion`, `importancia`, `categoria`, `hora_actividad`) VALUES
(6, 1, 'Keynote de Apertura: Innovación AI', 'Presentación de las metas tecnológicas y bienvenida institucional.', 'ALTA', 'CHARLAS', '09:00:00'),
(7, 1, 'TEST', 'DESCTEST', 'ALTA', 'OTROS', '12:30:05'),
(8, 1, 'Workshop Técnico: Java Avanzado', 'Laboratorio práctico interactivo sobre concurrencia y JPA.', 'MEDIA', 'COWORK', '14:30:00'),
(9, 1, 'After Office & Networking', 'Espacio libre de recreación con barra de tragos y música en vivo.', 'BAJA', 'RECREACION', '19:00:00'),
(10, 1, 'TEST2', '', 'ALTA', 'CHARLAS', '19:20:00'),
(12, 2, 'TEST3', 'Recopilación todos los Choques de Franco.', 'ALTA', 'RECREACION', '20:30:00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asignaciones_habitaciones`
--

CREATE TABLE `asignaciones_habitaciones` (
  `id` int(11) NOT NULL,
  `id_reserva` int(11) NOT NULL,
  `id_habitacion` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `casillero_checkin` enum('1','2') NOT NULL,
  `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `asignaciones_habitaciones`
--

INSERT INTO `asignaciones_habitaciones` (`id`, `id_reserva`, `id_habitacion`, `id_usuario`, `casillero_checkin`, `fecha_registro`) VALUES
(1, 1, 1, 1, '1', '2026-05-28 14:49:55'),
(2, 1, 1, 3, '2', '2026-05-28 14:50:41'),
(3, 2, 2, 4, '1', '2026-06-04 14:49:21'),
(4, 1, 3, 11, '1', '2026-06-04 23:13:49');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asistencias_actividades`
--

CREATE TABLE `asistencias_actividades` (
  `id` int(11) NOT NULL,
  `id_actividad` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `asistio` char(1) NOT NULL DEFAULT 'N'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `asistencias_actividades`
--

INSERT INTO `asistencias_actividades` (`id`, `id_actividad`, `id_usuario`, `asistio`) VALUES
(2, 12, 4, 'S'),
(3, 8, 11, 'S'),
(4, 10, 11, 'S');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `datos_empresas`
--

CREATE TABLE `datos_empresas` (
  `id_usuario` int(11) NOT NULL,
  `cuit` varchar(20) NOT NULL,
  `razon_social` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `datos_empresas`
--

INSERT INTO `datos_empresas` (`id_usuario`, `cuit`, `razon_social`) VALUES
(2, '30-12345678-9', 'Globant S.A.');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `datos_personas`
--

CREATE TABLE `datos_personas` (
  `id_usuario` int(11) NOT NULL,
  `dni` varchar(15) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) DEFAULT NULL,
  `celular` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `datos_personas`
--

INSERT INTO `datos_personas` (`id_usuario`, `dni`, `nombre`, `apellido`, `celular`) VALUES
(1, '47299224', 'Augusto', 'Karacsany', NULL),
(3, '44555666', 'Luca', 'Borrelli', NULL),
(4, '44728397', 'Franco', 'Colapinto', NULL),
(6, '12345678', 'Robert', 'Trebor', NULL),
(8, '99888777', 'Alfonso', 'Gutierrez', '4745450955'),
(9, '11222333', 'Eustaquio', 'Ramirez', NULL),
(10, '12345666', 'Michael', 'Schumacher', NULL),
(11, '55544433', 'TEST', 'TEST', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `habitaciones`
--

CREATE TABLE `habitaciones` (
  `id` int(11) NOT NULL,
  `numero` varchar(10) NOT NULL,
  `tipo` varchar(50) NOT NULL DEFAULT 'Standard',
  `capacidad` int(11) NOT NULL DEFAULT 2,
  `estado` enum('Libre','Half','Completa') DEFAULT 'Libre'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `habitaciones`
--

INSERT INTO `habitaciones` (`id`, `numero`, `tipo`, `capacidad`, `estado`) VALUES
(1, '101', 'Standard', 2, 'Completa'),
(2, '102', 'Standard', 2, 'Half'),
(3, '103', 'Standard', 2, 'Half'),
(4, '201', 'Standard', 2, 'Libre'),
(5, '202', 'Standard', 2, 'Libre'),
(6, '203', 'Standard', 2, 'Libre');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `lista_invitados_previa`
--

CREATE TABLE `lista_invitados_previa` (
  `id` int(11) NOT NULL,
  `id_reserva` int(11) NOT NULL,
  `dni` varchar(15) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `celular` varchar(20) NOT NULL,
  `dni_companero` varchar(15) NOT NULL,
  `token_acceso` varchar(255) DEFAULT NULL,
  `asistencia_confirmada` char(1) NOT NULL DEFAULT 'N'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `lista_invitados_previa`
--

INSERT INTO `lista_invitados_previa` (`id`, `id_reserva`, `dni`, `nombre`, `apellido`, `celular`, `dni_companero`, `token_acceso`, `asistencia_confirmada`) VALUES
(1, 1, '44555666', 'Luca', 'Borrelli', '1122334455', '44728397', '7520', 'N'),
(2, 1, '44728397', 'Franco', 'Colapinto', '1155667788', '44555666', '1062', 'N'),
(3, 1, '12345678', 'Robert', 'Trebor', '1199887766', '0', '3624', 'N'),
(4, 1, '99888777', 'Alfonso Gutierrez', '', '4745450955', '', '8726', 'N'),
(5, 1, '11222333', 'Eustaquio Ramirez', '', '', '', '7699', 'N'),
(6, 2, '47299224', 'Augusto Karacsany', '', '', '', '6311', 'N'),
(7, 2, '44728397', 'Franco Colapinto', '', '', '', '3019', 'S'),
(8, 1, '55544433', 'TEST TEST', '', '', '', '8993', 'S');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `opiniones_feedback`
--

CREATE TABLE `opiniones_feedback` (
  `id` int(11) NOT NULL,
  `id_reserva` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `puntuacion_general` int(11) NOT NULL,
  `comentario` text DEFAULT NULL,
  `fecha_feedback` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `participaciones_premios`
--

CREATE TABLE `participaciones_premios` (
  `id` int(11) NOT NULL,
  `id_invitado` int(11) NOT NULL,
  `id_premio` int(11) NOT NULL,
  `elegible` tinyint(1) NOT NULL DEFAULT 0,
  `ganador` tinyint(1) NOT NULL DEFAULT 0,
  `voucher` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `participaciones_premios`
--

INSERT INTO `participaciones_premios` (`id`, `id_invitado`, `id_premio`, `elegible`, `ganador`, `voucher`) VALUES
(3, 4, 1, 0, 0, NULL),
(4, 11, 1, 1, 1, 'VOU-11-778'),
(5, 11, 3, 1, 0, NULL),
(6, 11, 2, 1, 0, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `plantillas`
--

CREATE TABLE `plantillas` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `url_imagen` varchar(255) DEFAULT NULL,
  `activa` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `plantillas`
--

INSERT INTO `plantillas` (`id`, `nombre`, `descripcion`, `url_imagen`, `activa`) VALUES
(1, 'Corporativo Tech', 'Diseño oscuro con tonos azules para eventos de sistemas.', 'src/img/tech.png', 1),
(2, 'Branding Ejecutivo', 'Estilo minimalista y formal para juntas de negocios.', 'src/img/executive.png', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `premios`
--

CREATE TABLE `premios` (
  `id` int(11) NOT NULL,
  `id_reserva` int(11) NOT NULL,
  `nombre_premio` varchar(150) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `cantidad_disponible` int(11) NOT NULL DEFAULT 1,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `entregado` char(1) DEFAULT 'N',
  `id_ganador_usuario` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `premios`
--

INSERT INTO `premios` (`id`, `id_reserva`, `nombre_premio`, `descripcion`, `cantidad_disponible`, `activo`, `entregado`, `id_ganador_usuario`) VALUES
(1, 1, 'Viaje a Miame', 'Un viaje a miame, cuantos queres', 0, 1, 'N', NULL),
(2, 2, 'Viaje a Bosnia', 'Un viaje a Bosnia, ida y vuelta.', 2, 1, 'N', NULL),
(3, 1, 'Viaje a Italia', 'Un viaje a Italia, sin retorno.', 1, 1, 'N', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reservas_hotel`
--

CREATE TABLE `reservas_hotel` (
  `id` int(11) NOT NULL,
  `id_empresa` int(11) NOT NULL,
  `codigo_unico_evento` varchar(50) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `cantidad_estimada_asistentes` int(11) NOT NULL,
  `id_plantilla` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `reservas_hotel`
--

INSERT INTO `reservas_hotel` (`id`, `id_empresa`, `codigo_unico_evento`, `fecha_inicio`, `fecha_fin`, `cantidad_estimada_asistentes`, `id_plantilla`, `created_at`) VALUES
(1, 2, 'GLOBANT-2026', '2026-06-01', '2026-06-05', 50, 1, '2026-05-26 00:41:42'),
(2, 2, 'GLOBANTX', '2027-05-20', '2027-05-25', 30, NULL, '2026-06-01 01:57:40');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `rol` enum('ADMINISTRADOR','EMPRESA','INVITADO') NOT NULL,
  `asistencia_confirmada` char(1) NOT NULL DEFAULT 'N',
  `fecha_confirmacion` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `email`, `password`, `rol`, `asistencia_confirmada`, `fecha_confirmacion`) VALUES
(1, 'admin@hotel.com', '$2a$10$uF/66Rb9xmS8jD3cSVtP.eXIaRujgfBi9KD8e68y9Zd3NcgD6i83i', 'ADMINISTRADOR', 'N', NULL),
(2, 'info@globant.com', '$2a$10$wjR9D2Q5HYWx41yfG89LE.PQDi.FF7WE7YNxHw4IMbxD9rH12MurC', 'EMPRESA', 'N', NULL),
(3, 'invitado@gmail.com', '$2a$10$q/ABRKqFBznwHcrPci3st.rAjlfxQkZMzrAHGXmSi7zjPMFkUHCo6', 'INVITADO', 'N', NULL),
(4, 'invitado1@gmail.com', '$2a$10$tHd/HAtSKXNyhggL5TYfYe5HcYWe1gA04cLpY0OJG2FIYxMz7u2bm', 'INVITADO', 'N', NULL),
(6, 'Robert@gmail.com', '$2a$10$vQNuD3WE0qG5r.yeGE2Bvu1qMRJKvzS/aXuz0y0nNJOsRxwc5m94i', 'INVITADO', 'N', NULL),
(8, 'invitado9@gmail.com', '$2a$10$DdEv1MMiV7y4lIY9V4/arO8PUdc5PaOg5O2il.MnxbOZ3Blb1kxpy', 'INVITADO', 'N', NULL),
(9, 'invitado10@gmail.com', '$2a$10$sxGcXNaIN2aI5C3z5knUJuLmkSbYeU.Gp/Akh/06VcyZAJlm1S9Uu', 'INVITADO', 'N', NULL),
(10, 'invitado7@gmail.com', '$2a$10$Y4MLKEh9Ut3cUHDRNqGYh.WIUNiVVU5rM5mF/XnINyIbeBTkgv.Sm', 'INVITADO', 'N', NULL),
(11, 'invitado11@gmail.com', '$2a$10$W5KOCywuiSz32DPt39itmesjpUk84z4u4pNlFIWawqnMDcgTDdG6S', 'INVITADO', 'S', '2026-06-04 20:17:03');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `actividades`
--
ALTER TABLE `actividades`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_reserva` (`id_reserva`);

--
-- Indices de la tabla `asignaciones_habitaciones`
--
ALTER TABLE `asignaciones_habitaciones`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unica_asignacion_invitado` (`id_reserva`,`id_usuario`),
  ADD KEY `id_habitacion` (`id_habitacion`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `asistencias_actividades`
--
ALTER TABLE `asistencias_actividades`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `registro_unico_asistencia` (`id_actividad`,`id_usuario`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `datos_empresas`
--
ALTER TABLE `datos_empresas`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `cuit` (`cuit`);

--
-- Indices de la tabla `datos_personas`
--
ALTER TABLE `datos_personas`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `dni` (`dni`);

--
-- Indices de la tabla `habitaciones`
--
ALTER TABLE `habitaciones`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `numero` (`numero`);

--
-- Indices de la tabla `lista_invitados_previa`
--
ALTER TABLE `lista_invitados_previa`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unico_dni_reserva` (`id_reserva`,`dni`);

--
-- Indices de la tabla `opiniones_feedback`
--
ALTER TABLE `opiniones_feedback`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `un_feedback_por_usuario` (`id_reserva`,`id_usuario`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `participaciones_premios`
--
ALTER TABLE `participaciones_premios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `inscripcion_unica` (`id_invitado`,`id_premio`),
  ADD KEY `fk_participaciones_premio` (`id_premio`);

--
-- Indices de la tabla `plantillas`
--
ALTER TABLE `plantillas`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `premios`
--
ALTER TABLE `premios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_reserva` (`id_reserva`),
  ADD KEY `id_ganador_usuario` (`id_ganador_usuario`);

--
-- Indices de la tabla `reservas_hotel`
--
ALTER TABLE `reservas_hotel`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `codigo_unico_evento` (`codigo_unico_evento`),
  ADD KEY `id_empresa` (`id_empresa`),
  ADD KEY `id_plantilla` (`id_plantilla`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `actividades`
--
ALTER TABLE `actividades`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT de la tabla `asignaciones_habitaciones`
--
ALTER TABLE `asignaciones_habitaciones`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `asistencias_actividades`
--
ALTER TABLE `asistencias_actividades`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `habitaciones`
--
ALTER TABLE `habitaciones`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `lista_invitados_previa`
--
ALTER TABLE `lista_invitados_previa`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `opiniones_feedback`
--
ALTER TABLE `opiniones_feedback`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `participaciones_premios`
--
ALTER TABLE `participaciones_premios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `plantillas`
--
ALTER TABLE `plantillas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `premios`
--
ALTER TABLE `premios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `reservas_hotel`
--
ALTER TABLE `reservas_hotel`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `actividades`
--
ALTER TABLE `actividades`
  ADD CONSTRAINT `actividades_ibfk_1` FOREIGN KEY (`id_reserva`) REFERENCES `reservas_hotel` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `asignaciones_habitaciones`
--
ALTER TABLE `asignaciones_habitaciones`
  ADD CONSTRAINT `asignaciones_habitaciones_ibfk_1` FOREIGN KEY (`id_reserva`) REFERENCES `reservas_hotel` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `asignaciones_habitaciones_ibfk_2` FOREIGN KEY (`id_habitacion`) REFERENCES `habitaciones` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `asignaciones_habitaciones_ibfk_3` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `asistencias_actividades`
--
ALTER TABLE `asistencias_actividades`
  ADD CONSTRAINT `asistencias_actividades_ibfk_1` FOREIGN KEY (`id_actividad`) REFERENCES `actividades` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `asistencias_actividades_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `datos_empresas`
--
ALTER TABLE `datos_empresas`
  ADD CONSTRAINT `datos_empresas_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `datos_personas`
--
ALTER TABLE `datos_personas`
  ADD CONSTRAINT `datos_personas_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `lista_invitados_previa`
--
ALTER TABLE `lista_invitados_previa`
  ADD CONSTRAINT `lista_invitados_previa_ibfk_1` FOREIGN KEY (`id_reserva`) REFERENCES `reservas_hotel` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `opiniones_feedback`
--
ALTER TABLE `opiniones_feedback`
  ADD CONSTRAINT `opiniones_feedback_ibfk_1` FOREIGN KEY (`id_reserva`) REFERENCES `reservas_hotel` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `opiniones_feedback_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `participaciones_premios`
--
ALTER TABLE `participaciones_premios`
  ADD CONSTRAINT `fk_participaciones_premio` FOREIGN KEY (`id_premio`) REFERENCES `premios` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_participaciones_usuario` FOREIGN KEY (`id_invitado`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `premios`
--
ALTER TABLE `premios`
  ADD CONSTRAINT `premios_ibfk_1` FOREIGN KEY (`id_reserva`) REFERENCES `reservas_hotel` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `premios_ibfk_2` FOREIGN KEY (`id_ganador_usuario`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL;

--
-- Filtros para la tabla `reservas_hotel`
--
ALTER TABLE `reservas_hotel`
  ADD CONSTRAINT `reservas_hotel_ibfk_1` FOREIGN KEY (`id_empresa`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reservas_hotel_ibfk_2` FOREIGN KEY (`id_plantilla`) REFERENCES `plantillas` (`id`) ON DELETE SET NULL;
COMMIT;

ALTER TABLE actividades ADD COLUMN duracion_minutos INT DEFAULT 0;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
