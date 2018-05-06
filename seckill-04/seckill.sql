/*
SQLyog Ultimate v8.32 
MySQL - 5.7.9 : Database - seckill
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`seckill` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `seckill`;

/*Table structure for table `seckill` */

DROP TABLE IF EXISTS `seckill`;

CREATE TABLE `seckill` (
  `seckill_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  `name` varchar(255) NOT NULL COMMENT '商品名称',
  `number` int(11) NOT NULL COMMENT '库存数量',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀开启时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀结束时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1009 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

/*Data for the table `seckill` */

insert  into `seckill`(`seckill_id`,`name`,`number`,`start_time`,`end_time`,`create_time`) values (1000,'1000元秒杀ihone6',83,'2018-04-10 20:29:41','2019-11-02 00:00:00','2018-03-25 15:19:30'),(1001,'500元秒杀ipad2',200,'2019-04-10 21:29:35','2019-04-11 00:00:00','2018-03-25 15:20:08'),(1002,'300元秒杀小米4',295,'2018-04-11 14:13:14','2018-11-02 00:00:00','2018-03-25 15:20:50'),(1003,'200元秒杀红米note',400,'2015-11-01 00:00:00','2015-11-02 00:00:00','2018-03-25 15:22:02'),(1004,'100',10,'2015-11-01 00:00:00','2015-11-01 00:00:00','2018-05-02 21:10:29'),(1005,'100',1,'2015-11-01 00:00:00','2015-11-01 00:00:00','2018-05-02 21:15:32'),(1007,'100',10,'2015-11-01 00:00:00','2015-11-01 00:00:00','2018-05-02 21:20:14'),(1008,'100',10,'2015-11-01 00:00:00','2015-11-01 00:00:00','2018-05-03 15:31:51');

/*Table structure for table `success_killed` */

DROP TABLE IF EXISTS `success_killed`;

CREATE TABLE `success_killed` (
  `seckill_id` bigint(20) NOT NULL COMMENT '秒杀商品id',
  `user_phone` bigint(20) NOT NULL COMMENT '用户手机号',
  `state` tinyint(4) NOT NULL DEFAULT '-1' COMMENT '状态提示：-1：无效 0：成功 1：已付款',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`,`user_phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

/*Data for the table `success_killed` */

insert  into `success_killed`(`seckill_id`,`user_phone`,`state`,`create_time`) values (1000,125484561,0,'2018-05-05 16:27:15'),(1000,1234418198,0,'2018-05-05 21:01:45'),(1000,12345665432,-1,'2018-05-04 22:11:27'),(1000,13456487515,0,'2018-05-06 14:50:02'),(1000,13502178891,-1,'2018-05-04 20:51:10'),(1000,15871797571,0,'2018-05-06 14:28:01'),(1000,98765432123,0,'2018-05-06 15:19:35'),(1001,15876498116,0,'2018-03-30 00:37:14'),(1002,12345665432,-1,'2018-05-05 20:24:05'),(1002,12345678999,0,'2018-04-12 12:24:29'),(1002,15871797571,0,'2018-04-11 14:13:14');

/* Procedure structure for procedure `execute_seckill` */

/*!50003 DROP PROCEDURE IF EXISTS  `execute_seckill` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `execute_seckill`(in v_seckill_id bigint,in v_phone bigint,
    in v_kill_time timestamp,out r_result int)
BEGIN
    DECLARE insert_count int DEFAULT 0;
    START TRANSACTION;
    insert ignore into success_killed
      (seckill_id,user_phone,create_time)
      values (v_seckill_id,v_phone,v_kill_time);
    select row_count() into insert_count;
    IF (insert_count = 0) THEN
      ROLLBACK;
      set r_result = -1;
    ELSEIF(insert_count < 0) THEN
      ROLLBACK;
      SET R_RESULT = -2;
    ELSE
      update seckill
      set number = number-1
      where seckill_id = v_seckill_id
        and end_time > v_kill_time
        and start_time < v_kill_time
        and number > 0;
      select row_count() into insert_count;
      IF (insert_count = 0) THEN
        ROLLBACK;
        set r_result = 0;
      ELSEIF (insert_count < 0) THEN
        ROLLBACK;
        set r_result = -2;
      ELSE
        COMMIT;
        set r_result = 1;
      END IF;
    END IF;
  END */$$
DELIMITER ;

/* Procedure structure for procedure `execute_seckill3` */

/*!50003 DROP PROCEDURE IF EXISTS  `execute_seckill3` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `execute_seckill3`(IN v_seckillId BIGINT,IN v_phone BIGINT,IN v_kill_time TIMESTAMP ,OUT r_result INT)
BEGIN 
   DECLARE insert_count INT DEFAULT 0;
   START TRANSACTION ;
   INSERT IGNORE INTO success_killed(seckill_id,user_phone,create_time) VALUES(v_seckillId,v_phone,v_kill_time);
   SELECT ROW_COUNT() INTO insert_count;
   IF(insert_count = 0)THEN
      ROLLBACK;
      SET r_result = -1;
   ELSEIF (insert_count < 0)THEN
      ROLLBACK;
      SET r_result = -2;
   ELSE
      UPDATE seckill SET number = number - 1 WHERE seckill_id = v_seckillId AND end_time > v_kill_time AND start_time < v_kill_time AND number > 0;
      SELECT ROW_COUNT() INTO insert_count;
      IF(insert_count = 0) THEN
        ROLLBACK;
        SET r_result = 0;
      ELSEIF(insert_count < 0) THEN
        ROLLBACK;
        SET r_result = -2;
      ELSE
        COMMIT;
        SET r_result = 1;
      END IF;
    END IF;  
   END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
