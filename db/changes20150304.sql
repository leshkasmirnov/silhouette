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

ALTER TABLE `simulator` ADD COLUMN `Ord` INT(11) NULL  AFTER `Note` , ADD COLUMN `Activity` BIT NULL  AFTER `Ord` ;
UPDATE `simulator` SET Ord=Id;
UPDATE `simulator` SET Activity=1;
/*UPDATE `simulator` SET Activity=0 where id=13;*/
ALTER TABLE `simulator` CHANGE COLUMN `Ord` `Ord` INT(11) NOT NULL  , CHANGE COLUMN `Activity` `Activity` BIT(1) NOT NULL DEFAULT 1  ;
ALTER TABLE `subscription` CHANGE COLUMN `Type` `Type` INT(11) NOT NULL COMMENT '0 - общий, 1 - дневной, 2 - ланч, 3 - вечерний, 4 - 1 зал, 5 - 2 зал';
ALTER TABLE `subscription` CHANGE COLUMN `State` `State` INT(11) NOT NULL  ;

INSERT INTO `simulator` (`Name`, `Duration_Type`, `Cost`, `Note`, `Ord`, `Activity`) VALUES ('Прессотерапия + ИК тепло', '2', '2', 'Продолжительность 1 час', '6', 1);
INSERT INTO `simulator` (`Name`, `Duration_Type`, `Cost`, `Note`, `Ord`, `Activity`) VALUES ('Прессотерапия 2', '1', '1', 'Продолжительность 30 мин', '3', 1);
INSERT INTO `simulator` (`Name`, `Duration_Type`, `Cost`, `Note`, `Ord`, `Activity`) VALUES ('ИК одеяло', '2', '1', 'Продолжительность 1 час', '5', 1);
INSERT INTO `simulator` (`Name`, `Duration_Type`, `Cost`, `Note`, `Ord`, `Activity`) VALUES ('Степпер', '1', '1', 'Продолжительность 30 мин', '11', 1);

/*UPDATE `simulator` set Ord = 4 where id = 3;
UPDATE `simulator` set Ord = 7 where id = 4;
UPDATE `simulator` set Ord = 8 where id = 5;
UPDATE `simulator` set Ord = 9 where id = 6;
UPDATE `simulator` set Ord = 10 where id = 7;
UPDATE `simulator` set Ord = 12 where id = 8;
UPDATE `simulator` set Ord = 13 where id = 9;
UPDATE `simulator` set Ord = 14 where id = 10;
UPDATE `simulator` set Ord = 15 where id = 11;
UPDATE `simulator` set Ord = 16 where id = 12;
UPDATE `simulator` set Ord = 17 where id = 14;
UPDATE `simulator` set Ord = 18 where id = 15;
UPDATE `simulator` set Ord = 19 where id = 16;*/

ALTER TABLE `simulator` CHANGE COLUMN `Duration_Type` `Duration_Type` INT(11) NOT NULL COMMENT '0 - продолжительность 15 мин., 1 - продолжительность 30 мин., 2 - продолжительность 1 час.'  ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;