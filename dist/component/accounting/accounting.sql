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


-- 导出 accounting 的数据库结构
CREATE DATABASE IF NOT EXISTS `accounting` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `accounting`;

-- 导出  表 accounting.account 结构
CREATE TABLE IF NOT EXISTS `account` (
  `accountId` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '记录标识',
  `memberNo` varchar(50) NOT NULL COMMENT '账户所属会员号',
  `accountNo` char(32) NOT NULL COMMENT '账号32位固定号码',
  `accountName` varchar(50) NOT NULL COMMENT '账户名称',
  `accountType` smallint(6) NOT NULL COMMENT '账户类型，1-中间账户(特殊的科目账户)；2-科目账户；3-会员账户',
  `accountStatus` smallint(6) NOT NULL COMMENT '账务状态；1-正常；2-冻结；3-销户',
  `password` varchar(50) DEFAULT NULL COMMENT '账户支付密码',
  `subjectNo` varchar(20) NOT NULL COMMENT '科目号',
  `subjectDirect` char(2) NOT NULL COMMENT '科目借贷方向；DR-借;CR-贷',
  `passwordCheck` smallint(6) NOT NULL COMMENT '是否需要支付密码验证；1-需要；0-不需求',
  `balance` bigint(20) unsigned NOT NULL COMMENT '余额，单位为分',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账户最新更新时间',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间创建就不变',
  PRIMARY KEY (`accountId`),
  UNIQUE KEY `accountNoUK` (`accountNo`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='账户表';

-- 数据导出被取消选择。
-- 导出  表 accounting.subject 结构
CREATE TABLE IF NOT EXISTS `subject` (
  `subjectNo` varchar(20) NOT NULL COMMENT '科目号',
  `subjectName` varchar(100) NOT NULL COMMENT '科目名称',
  `subjectType` smallint(6) NOT NULL COMMENT '1-ASSETS资产\r2-LIABILITY负债\r3-EQUITY所有者权益类\r4-Cost成本类\r5-损益类 Profit and loss\r6-OUTFORM表外',
  `subjectDirect` char(2) NOT NULL COMMENT '科目借贷方向；DR-借;CR-贷',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`subjectNo`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='科目表\r\n';

-- 数据导出被取消选择。
-- 导出  表 accounting.water 结构
CREATE TABLE IF NOT EXISTS `water` (
  `waterId` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `accountId` bigint(20) unsigned NOT NULL COMMENT '账户标识',
  `accountNo` char(32) NOT NULL COMMENT '账户号',
  `orderNo` varchar(50) NOT NULL COMMENT '业务订单编号',
  `subjectNo` varchar(20) NOT NULL COMMENT '科目号',
  `directFlag` char(2) NOT NULL COMMENT '借贷方向标记;DR-借;CR-贷',
  `amount` bigint(20) unsigned NOT NULL COMMENT '交易金额，单位为分',
  `foreBalance` bigint(20) unsigned NOT NULL COMMENT '记账前账户余额，单位为分',
  `aftBalance` bigint(20) unsigned NOT NULL COMMENT '记账后账户余额，单位为分',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记账时间',
  PRIMARY KEY (`waterId`),
  KEY `subjectno` (`subjectNo`),
  KEY `accountId` (`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账户记账流水表';

-- 数据导出被取消选择。
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
