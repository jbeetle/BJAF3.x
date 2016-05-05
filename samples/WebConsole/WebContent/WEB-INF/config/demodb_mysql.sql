-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.5.24 - MySQL Community Server (GPL)
-- Server OS:                    Win32
-- HeidiSQL version:             7.0.0.4160
-- Date/time:                    2012-09-04 10:04:00
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping database structure for demodb
DROP DATABASE IF EXISTS `demodb`;
CREATE DATABASE IF NOT EXISTS `demodb` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `demodb`;


-- Dumping structure for function demodb.seqCurrval
DROP FUNCTION IF EXISTS `seqCurrval`;
DELIMITER //
CREATE DEFINER=`root`@`localhost` FUNCTION `seqCurrval`(`seq_name` VARCHAR(50)) RETURNS bigint(20)
BEGIN   
	  DECLARE value INTEGER;   
	  SET value = 0;   
	  SELECT current_value INTO value   
	  FROM Sequences   
	  WHERE name = seq_name;   
	  RETURN value;   
END//
DELIMITER ;


-- Dumping structure for function demodb.seqNextval
DROP FUNCTION IF EXISTS `seqNextval`;
DELIMITER //
CREATE DEFINER=`root`@`localhost` FUNCTION `seqNextval`(`seq_name` VARCHAR(50)) RETURNS bigint(20)
BEGIN   
	   UPDATE Sequences   
	   SET          current_value = current_value + increment   
	   WHERE name = seq_name;   
	   RETURN seqCurrval(seq_name);   
END//
DELIMITER ;


-- Dumping structure for table demodb.sequences
DROP TABLE IF EXISTS `sequences`;
CREATE TABLE IF NOT EXISTS `sequences` (
  `name` varchar(50) NOT NULL,
  `current_value` bigint(20) NOT NULL,
  `increment` bigint(20) NOT NULL DEFAULT '1',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table demodb.sequences: ~1 rows (approximately)
DELETE FROM `sequences`;
/*!40000 ALTER TABLE `sequences` DISABLE KEYS */;
INSERT INTO `sequences` (`name`, `current_value`, `increment`) VALUES
	('user', 50360, 10);
/*!40000 ALTER TABLE `sequences` ENABLE KEYS */;


-- Dumping structure for table demodb.xx_friend
DROP TABLE IF EXISTS `xx_friend`;
CREATE TABLE IF NOT EXISTS `xx_friend` (
  `friendid` bigint(20) NOT NULL,
  `friendname` varchar(50) NOT NULL,
  `phone` varchar(50) NOT NULL,
  `email` varchar(50),
  `address` varchar(50),
  PRIMARY KEY (`friendid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table demodb.xx_friend: ~0 rows (approximately)
DELETE FROM `xx_friend`;
/*!40000 ALTER TABLE `xx_friend` DISABLE KEYS */;
/*!40000 ALTER TABLE `xx_friend` ENABLE KEYS */;


-- Dumping structure for table demodb.xx_relation
DROP TABLE IF EXISTS `xx_relation`;
CREATE TABLE IF NOT EXISTS `xx_relation` (
  `userid` bigint(20) NOT NULL,
  `friendid` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table demodb.xx_relation: ~0 rows (approximately)
DELETE FROM `xx_relation`;
/*!40000 ALTER TABLE `xx_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `xx_relation` ENABLE KEYS */;


-- Dumping structure for table demodb.xx_user
DROP TABLE IF EXISTS `xx_user`;
CREATE TABLE IF NOT EXISTS `xx_user` (
  `userid` bigint(20) NOT NULL,
  `passwd` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(50),
  `sex` int(11) DEFAULT NULL,
  `birthday` datetime DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table demodb.xx_user: ~0 rows (approximately)
DELETE FROM `xx_user`;
/*!40000 ALTER TABLE `xx_user` DISABLE KEYS */;
INSERT INTO `xx_user` (`userid`, `passwd`, `username`, `email`, `sex`, `birthday`) VALUES
	(50360, 'eaa90a9b2a15530fa7d24ad24a2956ddca9d620eb1580489e3c888f49cec9904', '余浩东', 'yuhaodong@gmail.com', 1, '2012-09-04 09:11:47');
/*!40000 ALTER TABLE `xx_user` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
