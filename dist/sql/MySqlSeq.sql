DROP TABLE IF EXISTS `Sequences`;
CREATE TABLE IF NOT EXISTS `Sequences` (
  `name` varchar(50) NOT NULL,
   `current_value` bigint(20) NOT NULL,
   `increment` bigint(20) NOT NULL DEFAULT '1',
   `max_value` bigint(20) DEFAULT '-1' COMMENT '小于0，最大值机制失效；如果设置最大值，达到最大值会重新来过',
   `init_Value` bigint(20) DEFAULT '-1',
   PRIMARY KEY (`name`)
);
DROP FUNCTION IF EXISTS `seqCurrval`;
DELIMITER $
CREATE FUNCTION `seqCurrval`(`seq_name` VARCHAR(50)) RETURNS bigint
BEGIN
DECLARE value BIGINT;
 DECLARE maxVal BIGINT;
 SET value = 0;
 SET maxVal = 0;
 SELECT current_value INTO value FROM Sequences WHERE name = seq_name;
 select max_value into maxVal from Sequences where name = seq_name;
 if maxVal >0 and maxVal<value then
 begin
   select init_Value into value from Sequences where name = seq_name;
   UPDATE Sequences SET current_value = init_Value WHERE name = seq_name;
 end;
 end if;
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
