-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        5.7.10 - MySQL Community Server (GPL)
-- 服务器操作系统:                      osx10.9
-- HeidiSQL 版本:                  9.3.0.5083
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- 导出 security 的数据库结构
CREATE DATABASE IF NOT EXISTS `security` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `security`;

-- 导出  表 security.sec_permissions 结构
DROP TABLE IF EXISTS `sec_permissions`;
CREATE TABLE IF NOT EXISTS `sec_permissions` (
  `permissionId` bigint(20) NOT NULL AUTO_INCREMENT,
  `permission` varchar(100) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `available` smallint(6) DEFAULT '0',
  PRIMARY KEY (`permissionId`),
  UNIQUE KEY `idx_sec_permissions_permission` (`permission`)
) ENGINE=InnoDB AUTO_INCREMENT=940 DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。
-- 导出  表 security.sec_roles 结构
DROP TABLE IF EXISTS `sec_roles`;
CREATE TABLE IF NOT EXISTS `sec_roles` (
  `roleId` bigint(20) NOT NULL AUTO_INCREMENT,
  `role` varchar(100) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `available` smallint(6) DEFAULT '0',
  PRIMARY KEY (`roleId`),
  UNIQUE KEY `idx_sec_roles_role` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=782 DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。
-- 导出  表 security.sec_roles_permissions 结构
DROP TABLE IF EXISTS `sec_roles_permissions`;
CREATE TABLE IF NOT EXISTS `sec_roles_permissions` (
  `roleId` bigint(20) NOT NULL,
  `permissionId` bigint(20) NOT NULL,
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`roleId`,`permissionId`),
  KEY `FK_Reference_3` (`permissionId`),
  CONSTRAINT `FK_Reference_3` FOREIGN KEY (`permissionId`) REFERENCES `sec_permissions` (`permissionId`),
  CONSTRAINT `FK_Reference_4` FOREIGN KEY (`roleId`) REFERENCES `sec_roles` (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。
-- 导出  表 security.sec_users 结构
DROP TABLE IF EXISTS `sec_users`;
CREATE TABLE IF NOT EXISTS `sec_users` (
  `userId` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `salt` varchar(100) DEFAULT NULL,
  `locked` smallint(6) DEFAULT '0',
  `trycount` smallint(6) DEFAULT '0' COMMENT 'try次数',
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `idx_sec_users_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1259 DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。
-- 导出  表 security.sec_users_roles 结构
DROP TABLE IF EXISTS `sec_users_roles`;
CREATE TABLE IF NOT EXISTS `sec_users_roles` (
  `userId` bigint(20) NOT NULL,
  `roleId` bigint(20) NOT NULL,
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`,`roleId`),
  KEY `FK_Reference_2` (`roleId`),
  CONSTRAINT `FK_Reference_1` FOREIGN KEY (`userId`) REFERENCES `sec_users` (`userId`),
  CONSTRAINT `FK_Reference_2` FOREIGN KEY (`roleId`) REFERENCES `sec_roles` (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
