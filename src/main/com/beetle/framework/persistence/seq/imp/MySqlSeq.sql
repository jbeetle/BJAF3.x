DROP TABLE IF EXISTS `Sequences`;
CREATE TABLE IF NOT EXISTS `Sequences` (
  `name` varchar(50) NOT NULL,
  `current_value` bigint NOT NULL,
  `increment` bigint NOT NULL DEFAULT '1',
  PRIMARY KEY (`name`)
);
DROP FUNCTION IF EXISTS `seqCurrval`;
DELIMITER $
CREATE FUNCTION `seqCurrval`(`seq_name` VARCHAR(50)) RETURNS bigint
BEGIN   
	  DECLARE value INTEGER;   
	  SET value = 0;   
	  SELECT current_value INTO value   
	  FROM Sequences   
	  WHERE name = seq_name;   
	  RETURN value;   
END$
DELIMITER ;
DROP FUNCTION IF EXISTS `seqNextval`;
DELIMITER $
CREATE FUNCTION `seqNextval`(`seq_name` VARCHAR(50)) RETURNS bigint
BEGIN   
	   UPDATE Sequences   
	   SET          current_value = current_value + increment   
	   WHERE name = seq_name;   
	   RETURN seqCurrval(seq_name);   
END$
DELIMITER ;

