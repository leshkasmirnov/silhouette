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

--
-- Table structure for table `log`
--

DROP TABLE IF EXISTS `log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Date` datetime NOT NULL,
  `Message` varchar(500) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Id_UNIQUE` (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payment` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Subscription_Id` int(11) NOT NULL,
  `Paid` bit(1) NOT NULL DEFAULT b'0',
  `Payment_Date` date NOT NULL,
  `Sum` varchar(45) NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `Payment_Subscription_idx` (`Subscription_Id`),
  CONSTRAINT `Payment_Subscription` FOREIGN KEY (`Subscription_Id`) REFERENCES `subscription` (`Id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='Оплата абонеменов';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `personal_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `personal_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `First_Name` varchar(60) NOT NULL,
  `Middle_Name` varchar(60) DEFAULT NULL,
  `Last_Name` varchar(60) NOT NULL,
  `Birthday` date DEFAULT NULL,
  `Phone1` varchar(20) DEFAULT NULL,
  `Phone2` varchar(20) DEFAULT NULL,
  `Contract_Number` varchar(45) NOT NULL,
  `Active` bit(1) NOT NULL DEFAULT b'0',
  `Note` varchar(500) DEFAULT NULL COMMENT 'Примечание',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `simulator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `simulator` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(100) NOT NULL,
  `Duration_Type` int(11) NOT NULL,
  `Cost` float NOT NULL,
  `Note` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='Тренажеры';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subscription` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Subscription_Number` varchar(45) NOT NULL,
  `Period` int(11) NOT NULL,
  `Type` bit(1) NOT NULL DEFAULT b'0',
  `State` bit(1) NOT NULL DEFAULT b'1',
  `Date_From` date NOT NULL,
  `Date_To` date NOT NULL,
  `Participation_Number` float NOT NULL,
  `Participation_Number_Present` float DEFAULT NULL,
  `Reason_Reject` varchar(300) DEFAULT NULL,
  `Personal_Data_Id` int(11) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Subscription_Number_UNIQUE` (`Subscription_Number`),
  KEY `Subscription_Personal_Data_idx` (`Personal_Data_Id`),
  CONSTRAINT `Subscription_Personal_Data` FOREIGN KEY (`Personal_Data_Id`) REFERENCES `personal_data` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='Абонементы';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `time_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `time_table` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Date` date NOT NULL,
  `Time` varchar(45) NOT NULL,
  `Simulator_Id` int(11) NOT NULL,
  `Personal_Data_Id` int(11) NOT NULL,
  `Note` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Id_UNIQUE` (`Id`),
  KEY `TIME_TABLE_SUBSCRIPTION_idx` (`Simulator_Id`),
  KEY `TIME_TABLE_PERSONAL_DATA_idx` (`Personal_Data_Id`),
  CONSTRAINT `TIME_TABLE_PERSONAL_DATA` FOREIGN KEY (`Personal_Data_Id`) REFERENCES `personal_data` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `TIME_TABLE_SIMULATOR` FOREIGN KEY (`Simulator_Id`) REFERENCES `simulator` (`Id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COMMENT='Расписание тренажеров';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;