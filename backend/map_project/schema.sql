CREATE DATABASE IF NOT EXISTS `localisation` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `localisation`;

CREATE TABLE IF NOT EXISTS `position` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `date_position` datetime NOT NULL,
  `imei` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
