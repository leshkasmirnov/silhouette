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

CREATE  TABLE `silhouette`.`Ambry` (
  `Id` INT NOT NULL AUTO_INCREMENT ,
  `Name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`Id`) );
  
ALTER TABLE `silhouette`.`personal_data` ADD COLUMN `Ambry_Id` INT NULL  AFTER `Note` , 
  ADD CONSTRAINT `PERSONAL_DATA_AMBRY`
  FOREIGN KEY (`Ambry_Id` )
  REFERENCES `silhouette`.`ambry` (`Id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `PERSONAL_DATA_AMBRY_idx` (`Ambry_Id` ASC) ;  

ALTER TABLE `silhouette`.`subscription` CHANGE COLUMN `Date_To` `Date_To` DATE NULL  ;

INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('1', '1');
INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('2', '2');
INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('3', '3');
INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('4', '4');
INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('5', '5');
INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('6', '6');
INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('7', '7');
INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('8', '8');
INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('9', '9');
INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('10', '10');
INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('11', '11');
INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('12', '12');
INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('13', '13');
INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('14', '14');
INSERT INTO `silhouette`.`Ambry` (`Id`, `Name`) VALUES ('15', '15');

ALTER TABLE `silhouette`.`subscription` CHANGE COLUMN `Subscription_Number` `Subscription_Number` VARCHAR(45) NULL  ;
ALTER TABLE `silhouette`.`subscription` CHANGE COLUMN `Participation_Number` `Participation_Number` FLOAT NULL  ;
ALTER TABLE `silhouette`.`personal_data` CHANGE COLUMN `Contract_Number` `Contract_Number` VARCHAR(45) NULL  ;
ALTER TABLE `silhouette`.`time_table` ADD COLUMN `Printed_Act` BIT NULL DEFAULT 0  AFTER `Note` ;
  
  
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

