-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 10-06-2022 a las 20:32:15
-- Versión del servidor: 10.4.24-MariaDB
-- Versión de PHP: 8.1.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `proyectofinal`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `denunciasofertas`
--

CREATE TABLE `denunciasofertas` (
  `id_usuario` int(11) NOT NULL,
  `id_oferta` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `denunciasofertas`
--

INSERT INTO `denunciasofertas` (`id_usuario`, `id_oferta`) VALUES
(1, 101),
(2, 101);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `denunciasrecetas`
--

CREATE TABLE `denunciasrecetas` (
  `id_usuario` int(11) NOT NULL,
  `id_receta` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `denunciasrecetas`
--

INSERT INTO `denunciasrecetas` (`id_usuario`, `id_receta`) VALUES
(1, 4),
(4, 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estar`
--

CREATE TABLE `estar` (
  `id_supermercado` int(11) NOT NULL,
  `id_oferta` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `estar`
--

INSERT INTO `estar` (`id_supermercado`, `id_oferta`) VALUES
(33, 101),
(38, 110),
(38, 111),
(38, 115),
(38, 120),
(39, 116),
(39, 117),
(39, 118),
(39, 119);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `etiqueta`
--

CREATE TABLE `etiqueta` (
  `id` int(11) NOT NULL,
  `nombre` varchar(200) DEFAULT NULL,
  `contador` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `etiqueta`
--

INSERT INTO `etiqueta` (`id`, `nombre`, `contador`) VALUES
(1, 'leche', 94),
(2, 'puleva', 85),
(3, 'semidesnatada', 86),
(4, 'tomate', 11),
(5, 'frito', 11),
(6, 'hacendado', 17),
(10, 'largo', 0),
(9, 'arroz', 5),
(11, 'pechuga', 0),
(12, 'pollo', 0),
(13, 'andaluz', 0),
(14, 'soja', 0),
(15, 'almendras', 0),
(16, 'avellanas', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `favoritosrecetas`
--

CREATE TABLE `favoritosrecetas` (
  `id_usuario` int(11) NOT NULL,
  `id_receta` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `favoritosrecetas`
--

INSERT INTO `favoritosrecetas` (`id_usuario`, `id_receta`) VALUES
(1, 4),
(2, 6),
(4, 4),
(4, 5),
(4, 6);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `oferta`
--

CREATE TABLE `oferta` (
  `id` int(11) NOT NULL,
  `precio` float DEFAULT NULL,
  `precio_unidad` varchar(200) DEFAULT NULL,
  `imagen` varchar(200) DEFAULT NULL,
  `id_usuario` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `oferta`
--

INSERT INTO `oferta` (`id`, `precio`, `precio_unidad`, `imagen`, `id_usuario`) VALUES
(101, 1, '1€/L', 'images/admin/D2022-05-25H7M53S53admin.png', 1),
(111, 1, '1€/l', 'images/noelia/D2022-06-05H17M23S31noelia.png', 4),
(112, 1, '1€/l', 'android.graphics.Bitmap@5b5a92f', 4),
(113, 1, '1€/l', 'android.graphics.Bitmap@6e1ba52', 4),
(114, 1, '1€/l', 'android.graphics.Bitmap@3a69e10', 4),
(115, 1, '1€/l', 'images/josemaxp/D2022-06-06H19M35S47josemaxp.png', 2),
(110, 2.29, '5.73€/kgr', 'images/noelia/D2022-06-04H16M22S36noelia.png', 4),
(116, 1, '1€/l', 'android.graphics.Bitmap@3606438', 2),
(117, 1, '1€/l', 'android.graphics.Bitmap@f4d1473', 2),
(118, 1, '1€/l', 'images/josemaxp/D2022-06-10H18M26S41josemaxp.png', 2),
(119, 1, '1€/l', 'images/josemaxp/D2022-06-10H18M27S57josemaxp.png', 2),
(120, 1, '1€/l', 'images/admin/D2022-06-10H18M30S35admin.png', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto`
--

CREATE TABLE `producto` (
  `id` int(11) NOT NULL,
  `nombre` varchar(200) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `producto`
--

INSERT INTO `producto` (`id`, `nombre`) VALUES
(4, 'lechuga'),
(2, 'macarrones'),
(3, 'tomate'),
(5, 'arroz redondo'),
(6, 'pechugas de pollo troceadas'),
(7, 'cebolla'),
(8, 'salsa de soja'),
(9, 'sal'),
(10, 'nata para cocinar'),
(11, 'oregano');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `publicar`
--

CREATE TABLE `publicar` (
  `id_usuario` int(11) NOT NULL,
  `id_oferta` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `publicar`
--

INSERT INTO `publicar` (`id_usuario`, `id_oferta`) VALUES
(1, 101),
(1, 120),
(2, 115),
(2, 116),
(2, 117),
(2, 118),
(2, 119),
(4, 110),
(4, 111);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `receta`
--

CREATE TABLE `receta` (
  `id` int(11) NOT NULL,
  `nombre` varchar(200) DEFAULT NULL,
  `tiempo` varchar(200) DEFAULT NULL,
  `comensales` int(11) DEFAULT NULL,
  `pasos` varchar(4000) DEFAULT NULL,
  `utensilios` varchar(4000) DEFAULT NULL,
  `likes` int(11) DEFAULT NULL,
  `imagen` varchar(4000) DEFAULT NULL,
  `id_usuario` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `receta`
--

INSERT INTO `receta` (`id`, `nombre`, `tiempo`, `comensales`, `pasos`, `utensilios`, `likes`, `imagen`, `id_usuario`) VALUES
(5, 'Ensalada', '00.01', 1, '1. Unirt todo|2. Comer', 'Bol', 1, 'images/josemaxp/D2022-06-01H18M54S5josemaxp.png', 2),
(4, 'Macarrones con tomate', '00.30', 1, '1. Cocer los macarrones|2. Echar el tomate|3. Comer', 'Olla', 3, 'images/josemaxp/D2022-06-07H16M14S40josemaxp.png', 2),
(6, 'Arroz con pollo', '00.30', 1, '1. Poner el arroz a hervir en un cazo con agua y sal (12-20 min).|2. Echamos aceite de oliva en una sarten y sofreimos la cebolla.|3. En un bol, ponemos la carne y vertemos la soja junto con sal y demas espcias al gusto.|4. Un vez, hecha la cebolla, ponemos la carne en la sarten.|5. Cuando este dorada la carne, vertemos la nata y esperamos que espese.|6. Sacamos el arroz y emplatamos junto con la carne y la salsa.||A disfrutar!', '- Cazo|- Sarten|', 2, 'images/noelia/D2022-06-04H16M15S14noelia.png', 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `relacionetiqueta`
--

CREATE TABLE `relacionetiqueta` (
  `id_etq1` int(11) NOT NULL,
  `id_etq2` int(11) NOT NULL,
  `contador` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `relacionetiqueta`
--

INSERT INTO `relacionetiqueta` (`id_etq1`, `id_etq2`, `contador`) VALUES
(1, 2, 77),
(1, 3, 77),
(2, 3, 76),
(3, 14, 1),
(2, 14, 1),
(1, 14, 1),
(12, 13, 1),
(11, 13, 1),
(11, 12, 1),
(9, 10, 1),
(6, 10, 1),
(6, 9, 6),
(5, 6, 7),
(4, 6, 7),
(4, 5, 7),
(1, 1, 1),
(2, 2, 1),
(3, 3, 1),
(14, 14, 1),
(1, 15, 1),
(2, 15, 1),
(3, 15, 1),
(15, 15, 1),
(1, 16, 1),
(2, 16, 1),
(3, 16, 1),
(15, 16, 1),
(1, 9, 6),
(2, 9, 1),
(3, 9, 1),
(9, 9, 1),
(9, 14, 1),
(1, 6, 5);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `supermercado`
--

CREATE TABLE `supermercado` (
  `id` int(11) NOT NULL,
  `nombre` varchar(200) DEFAULT NULL,
  `longitud` float DEFAULT NULL,
  `latitud` float DEFAULT NULL,
  `direccion` varchar(1000) DEFAULT NULL,
  `poblacion` varchar(200) DEFAULT NULL,
  `provincia` varchar(200) DEFAULT NULL,
  `comunidadAutonoma` varchar(200) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `supermercado`
--

INSERT INTO `supermercado` (`id`, `nombre`, `longitud`, `latitud`, `direccion`, `poblacion`, `provincia`, `comunidadAutonoma`) VALUES
(1, 'Lidl', -6.14611, 36.4165, 'CALLE LAUREL, nº 3 Bloque 2 1ºB Residencial \"El Jardin, 11130 Chiclana de la Frontera, Cádiz, Spain', 'Villamartin', 'Cadiz', 'Andalucia'),
(37, 'Lidl', -5.74334, 36.816, 'C. Jardín, 2, 11640 Bornos, Cádiz, Spain', 'Bornos', 'Cadiz', 'Andalucia'),
(36, 'Mercadona', -5.74334, 36.816, 'C. Jardín, 2, 11640 Bornos, Cádiz, Spain', 'Bornos', 'Cadiz', 'Andalucia'),
(35, 'Mercadona', -5.64684, 36.8647, 'Matadero Municipal, C. la Vega, 5, 11650 Villamartin, Cádiz, Spain', 'Villamartin', 'Cadiz', 'Andalucia'),
(34, 'Dia', -5.64172, 36.861, 'Parcela 7 prado del guadalete, 11650 Villamartin, Cádiz, Spain', 'Villamartin', 'Cadiz', 'Andalucia'),
(33, 'Mercadona', -5.64172, 36.861, 'Parcela 7 prado del guadalete, 11650 Villamartin, Cádiz, Spain', 'Villamartin', 'Cadiz', 'Andalucia'),
(32, 'Lidl', -5.64172, 36.861, 'Parcela 7 prado del guadalete, 11650 Villamartin, Cádiz, Spain', 'Villamartin', 'Cadiz', 'Andalucia'),
(38, 'Lidl', -6.28049, 36.521, 'Gta. Lebón, 11007 Cádiz, Spain', 'Cadiz', 'Cadiz', 'Andalucia'),
(39, 'Mercadona', -6.28049, 36.521, 'Gta. Lebón, 11007 Cádiz, Spain', 'Cadiz', 'Cadiz', 'Andalucia');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tener`
--

CREATE TABLE `tener` (
  `id_etiqueta` int(11) NOT NULL,
  `id_oferta` int(11) NOT NULL,
  `especial` tinyint(1) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `tener`
--

INSERT INTO `tener` (`id_etiqueta`, `id_oferta`, `especial`) VALUES
(2, 111, 0),
(3, 111, 0),
(1, 111, 1),
(2, 101, 0),
(3, 101, 0),
(1, 101, 1),
(9, 115, 0),
(1, 115, 0),
(2, 115, 0),
(14, 115, 0),
(3, 115, 0),
(13, 110, 0),
(12, 110, 0),
(11, 110, 1),
(1, 116, 1),
(9, 116, 0),
(6, 116, 0),
(1, 117, 1),
(9, 117, 0),
(6, 117, 0),
(1, 118, 1),
(9, 118, 0),
(6, 118, 0),
(1, 119, 1),
(9, 119, 0),
(6, 119, 0),
(1, 120, 1),
(9, 120, 0),
(6, 120, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tenerproducto`
--

CREATE TABLE `tenerproducto` (
  `id_producto` int(11) NOT NULL,
  `id_receta` int(11) NOT NULL,
  `cantidad` double DEFAULT NULL,
  `unidadmedida` varchar(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `tenerproducto`
--

INSERT INTO `tenerproducto` (`id_producto`, `id_receta`, `cantidad`, `unidadmedida`) VALUES
(2, 3, 200, 'gr'),
(3, 3, 400, 'gr'),
(2, 4, 200, 'gr'),
(3, 4, 400, 'gr'),
(3, 5, 100, 'gr'),
(4, 5, 100, 'gr'),
(5, 6, 100, 'gr'),
(6, 6, 250, 'gr'),
(7, 6, 1, 'unidad'),
(8, 6, 50, 'ml'),
(9, 6, 2, 'gr'),
(10, 6, 200, 'ml'),
(11, 4, 1, 'pizca');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id` int(11) NOT NULL,
  `correo` varchar(200) DEFAULT NULL,
  `username` varchar(200) DEFAULT NULL,
  `password` varchar(512) DEFAULT NULL,
  `puntos` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id`, `correo`, `username`, `password`, `puntos`) VALUES
(1, 'admin', 'admin', 'c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec', 100),
(2, 'josemaxp@gmail.com', 'josemaxp', '3c9909afec25354d551dae21590bb26e38d53f2173b8d3dc3eee4c047e7ab1c1eb8b85103e3be7ba613b31bb5c9c36214dc9f14a42fd7a2fdb84856bca5c44c2', 1330),
(3, 'paco13@gmail.com', 'paco', '3c9909afec25354d551dae21590bb26e38d53f2173b8d3dc3eee4c047e7ab1c1eb8b85103e3be7ba613b31bb5c9c36214dc9f14a42fd7a2fdb84856bca5c44c2', 0),
(4, 'noeliagameroolva@gmail.com', 'Noelia', '3627909a29c31381a071ec27f7c9ca97726182aed29a7ddd2e54353322cfb30abb9e3a6df2ac2c20fe23436311d678564d0c8d305930575f60e2d3d048184d79', 520);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `denunciasofertas`
--
ALTER TABLE `denunciasofertas`
  ADD PRIMARY KEY (`id_usuario`,`id_oferta`),
  ADD KEY `denunciasOfertas_id_oferta_fk` (`id_oferta`);

--
-- Indices de la tabla `denunciasrecetas`
--
ALTER TABLE `denunciasrecetas`
  ADD PRIMARY KEY (`id_usuario`,`id_receta`),
  ADD KEY `denunciasRecetas_id_oferta_fk` (`id_receta`);

--
-- Indices de la tabla `estar`
--
ALTER TABLE `estar`
  ADD PRIMARY KEY (`id_supermercado`,`id_oferta`),
  ADD KEY `estar_id_oferta_fk` (`id_oferta`);

--
-- Indices de la tabla `etiqueta`
--
ALTER TABLE `etiqueta`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `favoritosrecetas`
--
ALTER TABLE `favoritosrecetas`
  ADD PRIMARY KEY (`id_usuario`,`id_receta`),
  ADD KEY `denunciasRecetas_id_oferta_fk` (`id_receta`);

--
-- Indices de la tabla `oferta`
--
ALTER TABLE `oferta`
  ADD PRIMARY KEY (`id`),
  ADD KEY `oferta_id_usuario_fk` (`id_usuario`);

--
-- Indices de la tabla `producto`
--
ALTER TABLE `producto`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `publicar`
--
ALTER TABLE `publicar`
  ADD PRIMARY KEY (`id_usuario`,`id_oferta`),
  ADD KEY `publicar_id_oferta_fk` (`id_oferta`);

--
-- Indices de la tabla `receta`
--
ALTER TABLE `receta`
  ADD PRIMARY KEY (`id`),
  ADD KEY `recetas_id_usuario_fk` (`id_usuario`);

--
-- Indices de la tabla `relacionetiqueta`
--
ALTER TABLE `relacionetiqueta`
  ADD PRIMARY KEY (`id_etq1`,`id_etq2`),
  ADD KEY `relacionEtiqueta_id_etq2_fk` (`id_etq2`);

--
-- Indices de la tabla `supermercado`
--
ALTER TABLE `supermercado`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `tener`
--
ALTER TABLE `tener`
  ADD PRIMARY KEY (`id_etiqueta`,`id_oferta`),
  ADD KEY `tener_id_oferta_fk` (`id_oferta`);

--
-- Indices de la tabla `tenerproducto`
--
ALTER TABLE `tenerproducto`
  ADD PRIMARY KEY (`id_producto`,`id_receta`),
  ADD KEY `tenerproducto_id_oferta_fk` (`id_receta`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `correo` (`correo`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `etiqueta`
--
ALTER TABLE `etiqueta`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de la tabla `oferta`
--
ALTER TABLE `oferta`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=121;

--
-- AUTO_INCREMENT de la tabla `producto`
--
ALTER TABLE `producto`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `receta`
--
ALTER TABLE `receta`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `supermercado`
--
ALTER TABLE `supermercado`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
