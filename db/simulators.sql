/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

LOCK TABLES `simulator` WRITE;
/*!40000 ALTER TABLE `simulator` DISABLE KEYS */;
INSERT INTO `simulator` VALUES (1,'Ролик 1',1,1,'Продолжительность 25 минут'),
							   (2,'Ролик 2',1,1,'Продолжительность 25 минут'),
							   (3,'Прессотерапия',1,1,'Продолжительность 25 минут'),
							   (4,'ИК тепло',2,1,'Продолжительность 50 минут'),
							   (5,'Массажная кровать 1',1,1,'Продолжительность 30 минут'),
							   (6,'Массажная кровать 2',1,1,'Продолжительность 30 минут'),
							   (7,'Велоэргометр',1,1,'Продолжительность 30 минут'),
							   (8,'Беговая дорожка',1,1,'Продолжительность 30 минут'),
							   (9,'Эллипсоид',1,1,'Продолжительность 30 минут'),
							   (10,'Виброплатформа',0,1,'Продолжительность 10 минут'),
							   (11,'Массажер для ног',0,0.5,'Продолжительность 15 минут'),
							   (12,'Очки',0,0.5,'Продолжительность 15 минут'),
							   (13,'Варежки',0,0.5,'Продолжительность 15 минут'),
							   (14,'Массажная подушка',0,0.5,'Продолжительность 15 минут'),
							   (15,'Пояс для похудения',0,0.5,'Продолжительность 15 минут'),
							   (16,'Инфракрасные сапоги',0,0.5,'Продолжительность 15 минут');
							   							   
/*!40000 ALTER TABLE `simulator` ENABLE KEYS */;
UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;