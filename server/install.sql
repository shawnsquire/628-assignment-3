SET NAMES utf8;
SET foreign_key_checks = 0;
SET time_zone = 'SYSTEM';
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

CREATE DATABASE `cmsc_628` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `cmsc_628`;

DROP TABLE IF EXISTS `chats`;
CREATE TABLE `chats` (
  `id_user_from` int(10) unsigned NOT NULL,
  `id_user_to` int(10) unsigned NOT NULL,
  `message` text NOT NULL,
  `sent` datetime NOT NULL,
  KEY `id_user_from` (`id_user_from`),
  KEY `id_user_to` (`id_user_to`),
  CONSTRAINT `chats_ibfk_1` FOREIGN KEY (`id_user_from`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chats_ibfk_2` FOREIGN KEY (`id_user_to`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `locations`;
CREATE TABLE `locations` (
  `id_user` int(10) unsigned NOT NULL,
  `time` datetime NOT NULL,
  `longitude` double NOT NULL,
  `latitude` double NOT NULL,
  PRIMARY KEY (`id_user`),
  CONSTRAINT `locations_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id_user` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(64) NOT NULL,
  `firstname` varchar(50) DEFAULT NULL,
  `lastname` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

