CREATE  TABLE `settings` (
  `id` INT NOT NULL ,
  `param_name` VARCHAR(45) NOT NULL ,
  `param_value` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `param_name_UNIQUE` (`param_name` ASC) );

INSERT INTO `settings` (`id`, `param_name`, `param_value`) VALUES ('1', 'mysql.home', 'C:\\dev\\MySQL\\MySQL Server 5.6\\bin');
INSERT INTO `settings` (`id`, `param_name`, `param_value`) VALUES ('2', 'save.path', 'H:\\');
INSERT INTO `settings` (`id`, `param_name`, `param_value`) VALUES ('3', 'backup.counts', '3');
INSERT INTO `settings` (`id`, `param_name`, `param_value`) VALUES ('4', 'do.on.startup', 'true');
INSERT INTO `settings` (`id`, `param_name`, `param_value`) VALUES ('5', 'do.on.exit', 'true');
