
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: easytable
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `IDCategoria` int NOT NULL AUTO_INCREMENT,
  `NombreCategoria` varchar(50) NOT NULL,
  `IDPDV` int DEFAULT NULL,
  PRIMARY KEY (`IDCategoria`),
  KEY `fk_categoria_pdv` (`IDPDV`),
  CONSTRAINT `fk_categoria_pdv` FOREIGN KEY (`IDPDV`) REFERENCES `pdv` (`IDPDV`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` VALUES (1,'Piqueos',1),(2,'Entradas',1),(3,'Principales',1),(4,'Agregados',1),(5,'Postres',1),(6,'Platos niños',1),(7,'Bebidas',1),(8,'Vinos',1),(9,'Bajativos',1),(10,'Cafetería',1),(11,'BAR SPRITZ',2),(12,'BAR CLASICOS',2),(13,'BAR MULE',2),(14,'BAR PISCO',2),(15,'BAR RON',2),(16,'BAR VODKA',2),(17,'BAR GIN',2),(18,'BAR WHISKY',2),(19,'BAJATIVO',2),(20,'BEBIDAS Y JUGOS',2),(21,'BAR MOCKTAIL',2),(22,'BAR CERVEZA',2),(23,'BAR VINOS',2),(24,'BAR PIQUEOS',2),(25,'SANDWICH',2),(26,'BAR TEQUILA',2),(27,'COMBOS',3),(28,'SANDWICH',3),(29,'VARIEDAD',3),(30,'CAFÉ MARLEY',3),(31,'TORTAS',3),(32,'PASTELERIA',3),(33,'BEBIDAS',3),(34,'HELADOS SAVORY',3),(35,'CERVEZAS',3),(36,'Bar (Combinados)',3),(37,'Bar (Cocktail)',3),(38,'Vinos',3);
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comensal`
--

DROP TABLE IF EXISTS `comensal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comensal` (
  `IDComensal` int NOT NULL AUTO_INCREMENT,
  `NombreComensal` varchar(20) NOT NULL,
  `IDMesa` int NOT NULL,
  `Pagado` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`IDComensal`),
  KEY `comensal_mesa` (`IDMesa`),
  CONSTRAINT `comensal_mesa` FOREIGN KEY (`IDMesa`) REFERENCES `mesa` (`IDMesa`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comensal`
--

LOCK TABLES `comensal` WRITE;
/*!40000 ALTER TABLE `comensal` DISABLE KEYS */;
/*!40000 ALTER TABLE `comensal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comensal_producto`
--

DROP TABLE IF EXISTS `comensal_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comensal_producto` (
  `IDComensal` int NOT NULL,
  `IDProducto` int NOT NULL,
  `cantidad` int NOT NULL,
  `Notas` varchar(200) NOT NULL,
  `Entregado` tinyint(1) DEFAULT '0',
  `Instancia` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`IDComensal`,`IDProducto`,`Notas`,`Instancia`),
  UNIQUE KEY `idx_comensal_producto_instancia` (`IDComensal`,`IDProducto`,`Instancia`),
  KEY `comensal_producto_producto` (`IDProducto`),
  CONSTRAINT `comensal_producto_comensal` FOREIGN KEY (`IDComensal`) REFERENCES `comensal` (`IDComensal`),
  CONSTRAINT `comensal_producto_producto` FOREIGN KEY (`IDProducto`) REFERENCES `producto` (`IDProducto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comensal_producto`
--

LOCK TABLES `comensal_producto` WRITE;
/*!40000 ALTER TABLE `comensal_producto` DISABLE KEYS */;
/*!40000 ALTER TABLE `comensal_producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comensal_producto_extra`
--

DROP TABLE IF EXISTS `comensal_producto_extra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comensal_producto_extra` (
  `IDComensal` int NOT NULL,
  `IDProducto` int NOT NULL,
  `IDExtra` int NOT NULL,
  `Cantidad` int NOT NULL,
  `Instancia` int NOT NULL DEFAULT '1',
  `Notas` varchar(200) NOT NULL DEFAULT '',
  PRIMARY KEY (`IDComensal`,`IDProducto`,`IDExtra`,`Instancia`,`Notas`),
  KEY `comensal_producto_extra_extra` (`IDExtra`),
  KEY `fk_comensal_producto_extra` (`IDComensal`,`IDProducto`,`Instancia`),
  KEY `comensal_producto_extra_comensal_producto_idx` (`IDComensal`,`IDProducto`,`Instancia`,`Notas`),
  CONSTRAINT `comensal_producto_extra_comensal_producto` FOREIGN KEY (`IDComensal`, `IDProducto`, `Instancia`) REFERENCES `comensal_producto` (`IDComensal`, `IDProducto`, `Instancia`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `comensal_producto_extra_extra` FOREIGN KEY (`IDExtra`) REFERENCES `extra` (`IDExtra`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comensal_producto_extra`
--

LOCK TABLES `comensal_producto_extra` WRITE;
/*!40000 ALTER TABLE `comensal_producto_extra` DISABLE KEYS */;
/*!40000 ALTER TABLE `comensal_producto_extra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `extra`
--

DROP TABLE IF EXISTS `extra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `extra` (
  `IDExtra` int NOT NULL AUTO_INCREMENT,
  `NombreExtra` varchar(30) NOT NULL,
  `ValorExtra` int DEFAULT NULL,
  PRIMARY KEY (`IDExtra`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `extra`
--

LOCK TABLES `extra` WRITE;
/*!40000 ALTER TABLE `extra` DISABLE KEYS */;
INSERT INTO `extra` VALUES (1,'A. PALTA',1800),(2,'A. TOMATE',1200),(3,'A. QUESO',1300),(4,'A. JAMON',1000),(5,'A. CHUCRUT',500),(6,'A. HUEVO',800),(7,'A. TOCINO',1500),(8,'A. POROTO VERDE',500),(9,'A. AJI VERDE',500),(10,'A. PEPINILLO',500),(11,'COCA MAQ COMBO',0),(12,'FANTA MAQ COMBO',0),(13,'COCA LIGHT MAQ COMBO',0),(14,'SPRITE MAQ COMBO',0),(15,'SODA MAQ COMBO',0);
/*!40000 ALTER TABLE `extra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `garzon`
--

DROP TABLE IF EXISTS `garzon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `garzon` (
  `IDGarzon` int NOT NULL,
  `idSesionTablet` varchar(20) NOT NULL,
  PRIMARY KEY (`IDGarzon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `garzon`
--

LOCK TABLES `garzon` WRITE;
/*!40000 ALTER TABLE `garzon` DISABLE KEYS */;
/*!40000 ALTER TABLE `garzon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mesa`
--

DROP TABLE IF EXISTS `mesa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mesa` (
  `IDMesa` int NOT NULL,
  `ValorPedido` int DEFAULT NULL,
  `IDGarzon` int DEFAULT NULL,
  `HoraPedido` datetime DEFAULT NULL,
  `IDPDV` int NOT NULL,
  PRIMARY KEY (`IDMesa`),
  KEY `mesa_garzon` (`IDGarzon`),
  KEY `mesa_pdv` (`IDPDV`),
  CONSTRAINT `mesa_garzon` FOREIGN KEY (`IDGarzon`) REFERENCES `garzon` (`IDGarzon`),
  CONSTRAINT `mesa_pdv` FOREIGN KEY (`IDPDV`) REFERENCES `pdv` (`IDPDV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mesa`
--

LOCK TABLES `mesa` WRITE;
/*!40000 ALTER TABLE `mesa` DISABLE KEYS */;
INSERT INTO `mesa` VALUES (1,NULL,NULL,NULL,1),(2,NULL,NULL,NULL,1),(3,NULL,NULL,NULL,1),(4,NULL,NULL,NULL,1),(5,NULL,NULL,NULL,1),(6,NULL,NULL,NULL,1),(7,NULL,NULL,NULL,1),(8,NULL,NULL,NULL,1),(10,NULL,NULL,NULL,1),(11,NULL,NULL,NULL,1),(12,NULL,NULL,NULL,1),(14,NULL,NULL,NULL,1),(15,NULL,NULL,NULL,1),(16,NULL,NULL,NULL,1),(17,NULL,NULL,NULL,1),(18,NULL,NULL,NULL,1),(19,NULL,NULL,NULL,1),(20,NULL,NULL,NULL,1),(21,NULL,NULL,NULL,1),(22,NULL,NULL,NULL,1),(23,NULL,NULL,NULL,1),(24,NULL,NULL,NULL,1),(25,NULL,NULL,NULL,1),(26,NULL,NULL,NULL,1),(27,NULL,NULL,NULL,1),(28,NULL,NULL,NULL,1),(29,NULL,NULL,NULL,1),(30,NULL,NULL,NULL,1),(31,NULL,NULL,NULL,1),(33,NULL,NULL,NULL,1),(34,NULL,NULL,NULL,1),(35,NULL,NULL,NULL,1),(36,NULL,NULL,NULL,1),(37,NULL,NULL,NULL,1),(40,NULL,NULL,NULL,1),(41,NULL,NULL,NULL,1),(42,NULL,NULL,NULL,1),(43,NULL,NULL,NULL,1),(44,NULL,NULL,NULL,1),(45,NULL,NULL,NULL,1),(46,NULL,NULL,NULL,1),(110,NULL,NULL,NULL,3),(111,NULL,NULL,NULL,3),(112,NULL,NULL,NULL,3),(113,NULL,NULL,NULL,3),(114,NULL,NULL,NULL,3),(115,NULL,NULL,NULL,3),(116,NULL,NULL,NULL,3),(117,NULL,NULL,NULL,3),(118,NULL,NULL,NULL,3),(119,NULL,NULL,NULL,3),(120,NULL,NULL,NULL,3),(121,NULL,NULL,NULL,3),(122,NULL,NULL,NULL,3),(123,NULL,NULL,NULL,3),(124,NULL,NULL,NULL,3),(125,NULL,NULL,NULL,3),(126,NULL,NULL,NULL,3),(127,NULL,NULL,NULL,3),(128,NULL,NULL,NULL,3),(141,NULL,NULL,NULL,3),(142,NULL,NULL,NULL,3),(143,NULL,NULL,NULL,3),(144,NULL,NULL,NULL,3),(145,NULL,NULL,NULL,3),(146,NULL,NULL,NULL,3),(147,NULL,NULL,NULL,3),(148,NULL,NULL,NULL,3),(149,NULL,NULL,NULL,3),(150,NULL,NULL,NULL,3),(151,NULL,NULL,NULL,3),(152,NULL,NULL,NULL,3),(161,NULL,NULL,NULL,2),(162,NULL,NULL,NULL,2),(163,NULL,NULL,NULL,2),(164,NULL,NULL,NULL,2),(165,NULL,NULL,NULL,2),(166,NULL,NULL,NULL,2),(167,NULL,NULL,NULL,2),(168,NULL,NULL,NULL,2),(169,NULL,NULL,NULL,2),(170,NULL,NULL,NULL,2),(171,NULL,NULL,NULL,2),(172,NULL,NULL,NULL,2),(173,NULL,NULL,NULL,2),(174,NULL,NULL,NULL,2),(175,NULL,NULL,NULL,2),(176,NULL,NULL,NULL,2),(177,NULL,NULL,NULL,2),(178,NULL,NULL,NULL,2),(179,NULL,NULL,NULL,2),(180,NULL,NULL,NULL,2),(181,NULL,NULL,NULL,2),(182,NULL,NULL,NULL,2),(183,NULL,NULL,NULL,2),(184,NULL,NULL,NULL,2),(185,NULL,NULL,NULL,2),(186,NULL,NULL,NULL,2),(187,NULL,NULL,NULL,2),(701,NULL,NULL,NULL,1),(702,NULL,NULL,NULL,1),(703,NULL,NULL,NULL,1),(704,NULL,NULL,NULL,1),(800,NULL,NULL,NULL,2),(801,NULL,NULL,NULL,2),(802,NULL,NULL,NULL,2),(803,NULL,NULL,NULL,2),(804,NULL,NULL,NULL,2),(805,NULL,NULL,NULL,2),(806,NULL,NULL,NULL,2),(807,NULL,NULL,NULL,2),(808,NULL,NULL,NULL,2),(809,NULL,NULL,NULL,2),(810,NULL,NULL,NULL,2),(811,NULL,NULL,NULL,2),(812,NULL,NULL,NULL,2);
/*!40000 ALTER TABLE `mesa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pdv`
--

DROP TABLE IF EXISTS `pdv`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pdv` (
  `IDPDV` int NOT NULL AUTO_INCREMENT,
  `NombrePDV` varchar(20) NOT NULL,
  PRIMARY KEY (`IDPDV`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pdv`
--

LOCK TABLES `pdv` WRITE;
/*!40000 ALTER TABLE `pdv` DISABLE KEYS */;
INSERT INTO `pdv` VALUES (1,'Restaurant'),(2,'Bar'),(3,'Cafetería');
/*!40000 ALTER TABLE `pdv` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `IDProducto` int NOT NULL AUTO_INCREMENT,
  `NombreProducto` varchar(255) DEFAULT NULL,
  `ValorProducto` int NOT NULL,
  `IDCategoria` int NOT NULL,
  `IDPDV` int NOT NULL,
  PRIMARY KEY (`IDProducto`),
  KEY `producto_categoria` (`IDCategoria`),
  KEY `producto_pdv` (`IDPDV`),
  CONSTRAINT `producto_categoria` FOREIGN KEY (`IDCategoria`) REFERENCES `categoria` (`IDCategoria`),
  CONSTRAINT `producto_pdv` FOREIGN KEY (`IDPDV`) REFERENCES `pdv` (`IDPDV`)
) ENGINE=InnoDB AUTO_INCREMENT=765 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (116,'Empanadas Cocktail Queso',2500,1,1),(117,'Empanadas de Pino Horno',2550,1,1),(118,'Papas Fritas',1700,1,1),(119,'Canastillo de Palta Frita',4000,1,1),(120,'Tabla Trilogia Club',8600,1,1),(121,'Tabla Mediterránea',8700,1,1),(122,'Calugas de Pescado',7500,1,1),(123,'Ensalada Cesar Club',5900,2,1),(124,'Ceviche Mixto',8200,2,1),(125,'Camarón al Pil Pil',7200,2,1),(126,'Consomé con huevo',2500,2,1),(127,'Crema de Zapallo',3000,2,1),(128,'Palta Tartar',7400,2,1),(129,'Lomo Puré de Zapallo',10000,3,1),(130,'Lomo Pastelera',10300,3,1),(131,'Saltado Club',9600,3,1),(132,'Filete Rústico',12900,3,1),(133,'Vidriola Beurre',9500,3,1),(134,'Ñoquis de la casa con Plateada',9800,3,1),(135,'Risotto con Osobuco',12900,3,1),(136,'Estofado de Filete',8900,3,1),(137,'Filete Mongoliano',8900,3,1),(138,'Filete Quinoa',12900,3,1),(139,'Filete Pastelera',12900,3,1),(140,'Salmón Gratinado',9900,3,1),(141,'Reineta Crocante',9600,3,1),(142,'Pastel de Jaiba',9500,3,1),(143,'Lasaña Vegetariana',7300,3,1),(144,'Lasaña de Boloñesa',8000,3,1),(145,'Merluza Austral con Sorrentinos',8800,3,1),(146,'Cannelloni Di Vitello',8400,3,1),(147,'Fetuccini Pesto Camarones',8900,3,1),(148,'Pastel de Choclo',8000,3,1),(149,'Milanesa',7500,3,1),(150,'Menú Naval',9200,3,1),(151,'Lomo Liso',9200,3,1),(152,'Lomo Vetado Grille',10000,3,1),(153,'Filete Grille',12600,3,1),(154,'Salmón Grille',9000,3,1),(155,'Atún Grille',8800,3,1),(156,'Pollo Grille',7200,3,1),(157,'Trucha',9000,3,1),(158,'A lo Pobre',2800,4,1),(159,'Ensalada Surtida',2500,4,1),(160,'Papas Fritas',1500,4,1),(161,'Arroz',1200,4,1),(162,'Arroz Cremoso',2400,4,1),(163,'Pastelera',1400,4,1),(164,'Pure de Zapallo',2400,4,1),(165,'Huevos',800,4,1),(166,'Cebolla Frita',1200,4,1),(167,'Ensalada de Tomate',1500,4,1),(168,'Momoto',2500,4,1),(169,'Ensalada Chilena',2000,4,1),(170,'Palta',1900,4,1),(171,'Panna Cotta Tradicional con Salsa de Frambuesa',2600,5,1),(172,'Créme Brulée',2900,5,1),(173,'Torta del Dia',3200,5,1),(174,'Copa de Helado',2100,5,1),(175,'Suspiro de la Casa',2900,5,1),(176,'Pie de Limón en vaso',3100,5,1),(177,'Celestino Tradicional',3800,5,1),(178,'Papayas al Jugo',3800,5,1),(179,'Leche Asada',3000,5,1),(180,'Nuggets con Papas Fritas',3700,6,1),(181,'Salchipapas',3150,6,1),(182,'Bebida (350 cc)',1700,7,1),(183,'Agua Mineral con o sin Gas (330 cc)',1400,7,1),(184,'Néctar',1600,7,1),(185,'Jugo de Fruta',1800,7,1),(186,'Corona',2800,7,1),(187,'Heineken',3000,7,1),(188,'Kunstman Torobayo',3500,7,1),(189,'Austral Calafate',3500,7,1),(190,'Schop Kunstman Valdivia Pale Lager',4000,7,1),(191,'Heineken 0.0 Alcohol',3000,7,1),(192,'Sauvignon Blanc Casillero del Diablo Reserva',8900,8,1),(193,'Chardonnay Terramater Vineyards Reserva',6200,8,1),(194,'Chardonnay Casillero del Diablo Reserva',8900,8,1),(195,'Ensamblaje Cs/Ca Terramater Vineyard Reserva',6200,8,1),(196,'Cabernet Sauvignon Casillero del Diablo Reserva.',8900,8,1),(197,'Cabernet Sauvignon Marques de Casa Concha Reserva.',19500,8,1),(198,'Carmenere Misiones de Rengo Reserva',7500,8,1),(199,'Carmenere Casillero del Diablo Reserva',8900,8,1),(200,'Carmenere Marques de Casa Concha Reserva',19500,8,1),(201,'Copa Sauvignon Blanc Frontera',2200,8,1),(202,'Copa Cabernet Sauvignon Frontera',2200,8,1),(203,'Descorche',4000,8,1),(204,'Sauvignon Casillero del Diablo 375 cc',5100,8,1),(205,'Merlot Santa Ema Gran Reserva',14900,8,1),(206,'Carmenere Santa Ema Gran Reserva',14900,8,1),(207,'Carmenere Casillero del Diablo 375 cc',5100,8,1),(208,'Sauvignon Blanc Casillero del Diablo 375 cc',5100,8,1),(209,'Chardonnay Marques De Casa Concha Viña Concha Y Toro',19500,8,1),(210,'Amaretto',1200,9,1),(211,'Araucano',1200,9,1),(212,'Manzanilla',1200,9,1),(213,'Cacao',1200,9,1),(214,'Menta',1200,9,1),(215,'Copa espumante',2800,9,1),(216,'Mango Sour',3000,9,1),(217,'Pisco Sour',3500,9,1),(218,'Pisco Sour Catedral',6700,9,1),(219,'Jerez Tío Pepe',4500,9,1),(220,'Jerez Zalamero',4000,9,1),(221,'Twinings Breakfast - Earl Grey',800,10,1),(222,'Twinings manzanilla pure peppermint',800,10,1),(223,'Nes',900,10,1),(224,'Grano Corto Espresso',1050,10,1),(225,'Americano',1600,10,1),(226,'Capuccino',1550,10,1),(227,'Mokaccino',1600,10,1),(228,'Vainilla',1600,10,1),(229,'Cortado',1600,10,1),(230,'Chocolate',1600,10,1),(231,'Disaronno Spritz',5900,11,2),(232,'Ramazzotti',4700,11,2),(233,'Aperol',4500,11,2),(234,'St Germain',5900,11,2),(235,'Jagger Boom',5900,11,2),(236,'Tropical Gin',6500,11,2),(237,'Chambord Spritz',5900,11,2),(238,'Mojito',3800,12,2),(239,'Mojito Sabores',4200,12,2),(240,'Pisco Sour Nacional',3500,12,2),(241,'Pisco Sour Catedral',6700,12,2),(242,'Old fashioned',3500,12,2),(243,'Negroni',3500,12,2),(244,'Tom Collins',3500,12,2),(245,'Martiny Dry',3800,12,2),(246,'John Collins',3500,12,2),(247,'Tequila Margarita',3800,12,2),(248,'Moscow Mule',5800,13,2),(249,'Tequila Tropical',4000,12,2),(250,'Pisco alto del Carmen 35',3200,14,2),(251,'Piscolon Alto del Carmen 35',4200,14,2),(252,'Pisco Mistral 35',3400,14,2),(253,'Piscolon Mistral 35',4400,14,2),(254,'Pisco Alto del Carmen 40',3600,14,2),(255,'Piscolon Alto del Carmen 40',4600,14,2),(256,'Pisco Mistral Nobel 40',4300,14,2),(257,'Piscolon Mistral Nobel 40',5300,14,2),(258,'Havana Club Reserva',2500,15,2),(259,'Ron Malibu',2600,15,2),(260,'Ron Abuelo',2300,15,2),(261,'Piña Colada (sabores)',5500,15,2),(262,'Pampero',2200,15,2),(263,'Absolut Blue',3200,16,2),(264,'Absolut Sabores',3500,16,2),(265,'Stolichnaya',2200,16,2),(266,'Ruso Blanco',3900,16,2),(267,'Ruso Negro',3900,16,2),(268,'Caipiroska',4500,16,2),(269,'Stolichnaya Dry',3800,16,2),(270,'Rigoletto Club',4500,16,2),(271,'Amaretto Sour',3700,12,2),(272,'Chardonnay Sour',3000,12,2),(273,'Pisco Sour Sutil',3800,12,2),(274,'Pisco Sour Sutil Catedral',7000,12,2),(275,'Manhattan',4700,12,2),(276,'Caipirinha',4500,12,2),(277,'Mango Sour',3000,12,2),(278,'Pichuncho',4200,12,2),(279,'Campari',3000,12,2),(280,'Dissarono Corto',4900,12,2),(281,'Jerez tio Pepe',4500,12,2),(282,'Jerez Zalamero',4000,12,2),(283,'Jerez Sour',4500,12,2),(284,'Chambord Corto',4900,12,2),(285,'Kir Royal',3200,12,2),(286,'Copa Espumante',2800,12,2),(287,'Clavo Oxidado',4200,12,2),(288,'London Mule',5800,13,2),(289,'Pisco Mule',5600,13,2),(290,'Parche Triple',4200,17,2),(291,'Bombay',3700,17,2),(292,'Ternura Maracuy',6000,17,2),(293,'Tropical Gin',6500,17,2),(294,'Tanqueray',3700,17,2),(295,'Kantal',3700,17,2),(296,'Gin Hendrick’s',5100,17,2),(297,'Tequila Sunrise',4000,26,2),(298,'Caipir ssima',4500,26,2),(299,'Olmeca',3900,26,2),(300,'Senda Silver',3200,26,2),(301,'Ballantines',4700,18,2),(302,'Johnnie Walker Rojo',4500,18,2),(303,'Johnnie Walker Negro',6600,18,2),(304,'Jack Boulevardier',5900,18,2),(305,'Jack Tennessee Mule',6500,18,2),(306,'Jack Manhattan',6500,18,2),(307,'Jack Honey Lemonade',5900,18,2),(308,'Jack Honey Mule',6500,18,2),(309,'Jack Apple Breeze',5500,18,2),(310,'Jack Apple Tonic',5500,18,2),(311,'Jack Daniels Old',5500,18,2),(312,'Jack Daniels Apple',6000,18,2),(313,'Jack Daniels Honey',6000,18,2),(314,'Baileys Irish Cream',4400,18,2),(315,'Araucano',1200,19,2),(316,'Manzanilla',1200,19,2),(317,'Cacao',1200,19,2),(318,'Menta',1200,19,2),(319,'Amaretto',1200,19,2),(320,'Jagger Shot',3000,19,2),(321,'Frangelico',2800,19,2),(322,'Coca cola',1700,20,2),(323,'Coca light',1700,20,2),(324,'Coca zero',1700,20,2),(325,'Sprite',1700,20,2),(326,'Sprite zero',1700,20,2),(327,'Canada dry',1700,20,2),(328,'Canada dry zero',1700,20,2),(329,'Fanta',1700,20,2),(330,'Tonica',1700,20,2),(331,'Agua mineral CG 330cc',1400,20,2),(332,'Agua mineral SG 330cc',1400,20,2),(333,'Jugo frutilla',1800,20,2),(334,'Jugo pina',1800,20,2),(335,'Jugo mango',1800,20,2),(336,'Nectar Durazno',1600,20,2),(337,'Nectar Naranja',1600,20,2),(338,'Red Bull',2500,20,2),(339,'Limonada',2100,20,2),(340,'Limonada Menta',2100,20,2),(341,'Limonada menta jengibre',2100,20,2),(342,'Limonada Frutilla Crema',2500,20,2),(343,'Mojito S/A',2900,21,2),(344,'Mojito Sabores S/A',2900,21,2),(345,'Gin sin Gin S/A',2900,21,2),(346,'Margarita Blue S/A',2900,21,2),(347,'Moscow Mule S/A',3400,21,2),(348,'Sunrise S/A',2900,21,2),(349,'Austral Calafate',3500,22,2),(350,'Corona',2800,22,2),(351,'Heineken',3000,22,2),(352,'Heineken 0.0',3000,22,2),(353,'Kunstman Torobayo',3500,22,2),(354,'Schop Kunstman Torobayo',4000,22,2),(355,'Porcion Michelada',600,22,2),(356,'Porcion chelada',600,22,2),(357,'Shot Limon',600,22,2),(358,'Schop Kunstman Lager',4000,22,2),(359,'Ensamblaje Cs/Ca Terramater Vineyard Reserva',6200,23,2),(360,'Fernet',3000,23,2),(361,'Carmenere Casillero del Diablo 375 cc',5100,23,2),(362,'Cabernet Sauvignon Casillero del Diablo 375cc',5100,23,2),(363,'Sauvignon Blanc Casillero del Diablo 375cc',5100,23,2),(364,'Copa espumante',2800,23,2),(365,'Kir royal',3200,23,2),(366,'Jerez Tio Pepe',4500,23,2),(367,'Jerez Zalamero',4000,23,2),(368,'Jerez Sour',4500,23,2),(369,'Botella Casillero del Diablo Reserva Carmenere',8900,23,2),(370,'Botella Casillero del Diablo Reserva Cabernet Sauvignon',8900,23,2),(371,'Sauvignon Blanc Casillero del Diablo 750cc',8900,23,2),(372,'Cabernet Sauvignon Terramater 750cc',6200,23,2),(373,'Jarra de Vino Navegado 1 Lt.',8900,23,2),(374,'Tabla Trilogía Club',8600,24,2),(375,'Tabla Mexicana',7800,24,2),(376,'Tabla Mixta',9500,24,2),(377,'Tabla Mediterránea',8700,24,2),(378,'Porción de Papas Fritas',1700,24,2),(379,'Empanada Cocktail Queso (4)',2500,24,2),(380,'Nugget de Pollo (6)',1600,24,2),(381,'Hamburguesa Italiana Vegetariana',5500,25,2),(382,'Chacarero',4700,25,2),(383,'Cheeseburger',5800,25,2),(384,'Hamburguesa Criolla',5400,25,2),(385,'Super Pingui',6800,25,2),(386,'Hamburguesa Italiana',4800,25,2),(387,'The King Burger',5600,25,2),(388,'Burger Blue',5600,25,2),(389,'Churrasco Italiano',4900,25,2),(390,'Vianesa Italiana',3100,25,2),(391,'Barros Jarpa',3600,25,2),(392,'Barros Luco',4500,25,2),(393,'Ave Palta en Miga',4200,25,2),(394,'25 Queso Solo',2500,25,2),(395,'Miga Ave Pimentón',3500,25,2),(396,'Miga Vegetariano',3500,25,2),(397,'Agregado Palta',1800,25,2),(398,'Agregado Tomate',1200,25,2),(399,'Agregado Queso',1300,25,2),(400,'Agregado Jamón',1100,25,2),(401,'Agregado Chucrut',500,25,2),(402,'Agregado Huevo',800,25,2),(403,'Agregado Tocino',1500,25,2),(404,'Agregado Poroto Verde',1200,25,2),(405,'Agregado Aji Verde',500,25,2),(406,'Agregado Pepinillo',500,25,2),(586,'COMBO 1 Vienesa Italiana',4500,27,3),(587,'COMBO 2 Hamburguesa Italiana',5500,27,3),(588,'COMBO 3 Dos Vienesas',3350,27,3),(589,'COMBO 4 Nugget de Pollo',3900,27,3),(590,'Desayuno 1',2450,27,3),(591,'Desayuno 2',2350,27,3),(592,'Desayuno 3',3750,27,3),(593,'Desayuno 4',3550,27,3),(594,'Desayuno 5',3950,27,3),(595,'Churrasco Chacarero',4700,28,3),(596,'Hamburguesa Italiana',4800,28,3),(597,'Churrasco Italiano',4900,28,3),(598,'Vianesa Italiana',3100,28,3),(599,'Completo',3650,28,3),(600,'Barros Jarpa',3600,28,3),(601,'Barros Luco',4500,28,3),(602,'Ave Palta en Miga',4200,28,3),(603,'Ave Italiano Miga',4400,28,3),(604,'28 queso solo',2500,28,3),(605,'Super Pingui',6800,28,3),(606,'Cheese burger',5800,28,3),(607,'Criolla',5400,28,3),(608,'The King Burguer',5600,28,3),(609,'Burger Blue',5600,28,3),(610,'Hamburguesa Vegetariana',5500,28,3),(611,'Ave Pimenton',3500,28,3),(612,'Miga Vegetariano',3500,28,3),(613,'Papas Fritas',1700,29,3),(614,'Empanadas Cocktail Queso',2500,29,3),(615,'Nugget de Pollo',1600,29,3),(616,'Sopaipillas',1200,29,3),(617,'Empanada de Pino Horno',2500,29,3),(618,'Ensalada Cesar',5900,29,3),(619,'Ensalada Atun',5700,29,3),(620,'Huevos Revueltos con Tostadas',2000,29,3),(621,'Huevos Revueltos con Jamon y Tostadas',2500,29,3),(622,'Tabla Mediterranea',8700,29,3),(623,'Tabla Trilogia Club',8600,29,3),(624,'Milk Shake de Vainilla',3500,29,3),(625,'Milk Shake de Chocolate',3500,29,3),(626,'Café Helado',3600,30,3),(627,'Milk Shake de Frutilla',3500,29,3),(628,'Café Helado',3600,29,3),(629,'Te e infusiones Twinings English Breakfast / Earl Grey',800,30,3),(630,'Te e infusiones Twinings manzanilla / Pure peppermint',850,30,3),(631,'Vaso expresso Grano Corto',1050,30,3),(632,'Vaso expresso Expreso Machado',1050,30,3),(633,'Vaso mediano Canela',1600,30,3),(634,'Vaso mediano Mokaccino',1600,30,3),(635,'Vaso mediano Capuccino',1600,30,3),(636,'Vaso mediano Cortaddo',1600,30,3),(637,'Vaso mediano Vainilla',1600,30,3),(638,'Vaso mediano Chai Late',1600,30,3),(639,'Vaso mediano Chocolate Fuerte',1600,30,3),(640,'Vaso mediano Chocolate con Leche',1600,30,3),(641,'Vaso mediano Latte',1600,30,3),(642,'Vaso mediano Americano',1600,30,3),(643,'Vaso grande Canela',2050,30,3),(644,'Vaso grande Mokaccino',2050,30,3),(645,'Vaso grande Capuccino',2050,30,3),(646,'Vaso grande Cortaddo',2050,30,3),(647,'Vaso grande Vainilla',2050,30,3),(648,'Vaso grande Chai Late',2050,30,3),(649,'Vaso grande Chocolate Fuerte',2050,30,3),(650,'Vaso grande Chocolate con Leche',2050,30,3),(651,'Vaso grande Latte',2050,30,3),(652,'Vaso grande Americano',2050,30,3),(653,'Expresso Cafetal',1500,30,3),(654,'Americano Cafetal',1500,30,3),(655,'Expresso Doble Cafetal',2000,30,3),(656,'Latte Cafetal',2000,30,3),(657,'Capuccino Cafetal',2000,30,3),(658,'Leche Sola Cafetal',1000,30,3),(659,'Torta Pastel 3 Leche y 4 Leche',16500,31,3),(660,'Torta Pastel Bizcocho',16500,31,3),(661,'Torta Pastel Hojarasca',17000,31,3),(662,'Torta Pastel Panqueque',17000,31,3),(663,'31 de Bizcochuelo Chocolate (10 personas)',23100,31,3),(664,'31 de Bizcochuelo Chocolate (20 personas)',27400,31,3),(665,'31 de Bizcochuelo 3 Leches (10 Personas)',20300,31,3),(666,'31 de Bizcochuelo 3 Leches (20 Personas)',27400,31,3),(667,'31 de Bizcochuelo 4 Leches (10 Personas)',20300,31,3),(668,'31 de Bizcochuelo 4 Leches (20 Personas)',27400,31,3),(669,'Torta Panqueque (10 personas)',24300,31,3),(670,'Torta Panqueque (20 personas)',32500,31,3),(671,'Torta Hojarazca (10 personas)',20300,31,3),(672,'Torta Hojarazca (20 personas)',29500,31,3),(673,'Torta Hojarazca sin Azúcar (10 personas)',26000,31,3),(674,'Torta Merengue (20 personas)',29500,31,3),(675,'Kuchen Manzana',12100,31,3),(676,'Kuchen Nuez',14200,31,3),(677,'Cheesecake Frutos Rojos',16200,31,3),(678,'Cheesecake Maracuyá',16200,31,3),(679,'Pie de Limón',11200,31,3),(680,'Pie Maracuyá',13100,31,3),(681,'Trozo Torta',3200,32,3),(682,'Trozo Kuchen',2100,32,3),(683,'Trozo Cheesecake',3000,32,3),(684,'Torta Brownie (10 personas)',20300,32,3),(685,'Torta Carrot Cake (10 personas)',20300,32,3),(686,'Galleta Bolsa 70 grs',1800,32,3),(687,'Galletón Avena (Vegano)',1100,32,3),(688,'Galletón De Miel',700,32,3),(689,'Galletas De Miel Bolsa 3 Unidades',600,32,3),(690,'Media Luna',1200,32,3),(691,'Rollo De Canela',1700,32,3),(692,'Dulce Chileno',1150,32,3),(693,'Cono Chocolate',2200,32,3),(694,'Cocada',700,32,3),(695,'Cocada Sin Azúcar',950,32,3),(696,'Frutin Sin Azúcar',1900,32,3),(697,'Frutin',1300,32,3),(698,'Brownie',1700,32,3),(699,'Brownie Sin Azúcar',1700,32,3),(700,'Brownie Vegano',1700,32,3),(701,'Crocante De Almendra',700,32,3),(702,'Coca cola Maquina',1200,33,3),(703,'Coca light Maquina',1200,33,3),(704,'Coca lata',1700,33,3),(705,'Coca Zero Lata',1700,33,3),(706,'Coca Light lata',1700,33,3),(707,'Sprite zero maquina',1200,33,3),(708,'Fanta Zero Maquina',1200,33,3),(709,'Fanta Lata',1700,33,3),(710,'Sprite lata',1700,33,3),(711,'Sprite Zero lata',1700,33,3),(712,'Canada Dry lata',1700,33,3),(713,'Canada Dry Zero lata',1700,33,3),(714,'Tonica lata',1700,33,3),(715,'Vaso de soda',400,33,3),(716,'Mineral 600cc',1700,33,3),(717,'Agua Mineral C/G 330',1400,33,3),(718,'Agua Mineral S/G 330',1400,33,3),(719,'Nectar Durazno 300C',1600,33,3),(720,'Nectar Frutilla',1600,33,3),(721,'Jugo Melon',1800,33,3),(722,'Jugo Piña',1800,33,3),(723,'Jugo frutilla',1800,33,3),(724,'Jugo Mango',1800,33,3),(725,'Egocéntrico',890,34,3),(726,'Crazy Flocos',1290,34,3),(727,'Crazy Frambuesa',1290,34,3),(728,'Sangurucho',1290,34,3),(729,'Bilz y Pap',750,34,3),(730,'Kriko',750,34,3),(731,'Crocante',790,34,3),(732,'Lolly Pop',750,34,3),(733,'Centella',300,34,3),(734,'Trululú',500,34,3),(735,'Cola de Tigre',500,34,3),(736,'Danky',1890,34,3),(737,'Chocolito',890,34,3),(738,'Mega Sahne Nuss',1890,34,3),(739,'Pura Fruta',1100,34,3),(740,'Helado Pistacho (Keto)',4900,34,3),(741,'Helado Nutella (Keto)',4900,34,3),(742,'Helado Mango',4900,34,3),(743,'Helado Frutos del Bosque',4900,34,3),(744,'Corona',2800,35,3),(745,'Heineken',3000,35,3),(746,'Kunstmann Torobayo',3500,35,3),(747,'Austral Calafate',3500,35,3),(748,'Schop Kunstmann Torobayo',4000,35,3),(749,'Schop Kunstmann Valdivia Pale Lager',4000,35,3),(750,'Espumante francés Perle de Lune, Brut Rosé',12000,36,3),(751,'Piscola',4900,36,3),(752,'Ron Havana Club Añejo Reserva',3900,36,3),(753,'Copa de Espumante',2800,36,3),(754,'Vodka Stolichnnaya',3900,36,3),(755,'Vodka Absolut Sabores',5500,36,3),(756,'Margarita',3800,37,3),(757,'Aperol',4500,37,3),(758,'Pisco Sour',3500,37,3),(759,'Pisco Sour Catedral',6700,37,3),(760,'Mango Sour',3000,37,3),(761,'Mojito',3800,37,3),(762,'Cabernet Sauvignon Casillero del Diablo 375 cc',5100,38,3),(763,'Carmenere Casillero del Diablo 375 cc',5100,38,3),(764,'Sauvignon Blanc Casillero del Diablo 375 cc',5200,38,3);
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `IDRol` int NOT NULL AUTO_INCREMENT,
  `NombreRol` varchar(255) NOT NULL,
  `Habilitado` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`IDRol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `IDUsuario` int NOT NULL AUTO_INCREMENT,
  `NombreUsuario` varchar(255) NOT NULL,
  `MailUsuario` varchar(255) NOT NULL,
  `PWDHash` varchar(255) NOT NULL,
  `Habilitado` tinyint(1) NOT NULL DEFAULT '1',
  `IDRol` int NOT NULL,
  PRIMARY KEY (`IDUsuario`),
  KEY `usuario_rol` (`IDRol`),
  CONSTRAINT `usuario_rol` FOREIGN KEY (`IDRol`) REFERENCES `rol` (`IDRol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-28 19:32:36