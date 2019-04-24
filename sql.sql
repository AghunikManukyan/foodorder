/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.18-log : Database - cakesproject
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`cakesproject` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `cakesproject`;

/*Table structure for table `address` */

DROP TABLE IF EXISTS `address`;

CREATE TABLE `address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `city` varchar(255) NOT NULL,
  `region` varchar(255) NOT NULL,
  `street` varchar(255) NOT NULL,
  `apartment` varchar(255) NOT NULL,
  `suite` int(11) NOT NULL,
  `phone` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `address_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `address` */

/*Table structure for table `category` */

DROP TABLE IF EXISTS `category`;

CREATE TABLE `category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/*Data for the table `category` */

insert  into `category`(`id`,`name`) values (6,'TEA'),(7,'CUPCAKE'),(8,'ICE CREAM'),(9,'CAKES'),(10,'COFFEE');

/*Table structure for table `menu` */

DROP TABLE IF EXISTS `menu`;

CREATE TABLE `menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Data for the table `menu` */

insert  into `menu`(`id`,`name`) values (3,'DESERT'),(4,'DRINKS');

/*Table structure for table `menu_category` */

DROP TABLE IF EXISTS `menu_category`;

CREATE TABLE `menu_category` (
  `menu_id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) NOT NULL,
  PRIMARY KEY (`menu_id`,`category_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `FKcibuohcu46dgr77uxbjpd4qks` FOREIGN KEY (`menu_id`) REFERENCES `menu` (`id`),
  CONSTRAINT `menu_category_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Data for the table `menu_category` */

insert  into `menu_category`(`menu_id`,`category_id`) values (4,6),(3,7),(3,8),(3,9),(4,10);

/*Table structure for table `order` */

DROP TABLE IF EXISTS `order`;

CREATE TABLE `order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `price` double NOT NULL,
  `date` datetime NOT NULL,
  `status` enum('PENDING','PROCESSING','COMPLETES') NOT NULL,
  `count` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `order` */

/*Table structure for table `prod_order` */

DROP TABLE IF EXISTS `prod_order`;

CREATE TABLE `prod_order` (
  `products_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  PRIMARY KEY (`products_id`,`order_id`),
  KEY `order_id` (`order_id`),
  KEY `FK7eeb0nhx8jdvdak8tu6pa1l18` (`product_id`),
  CONSTRAINT `FK7eeb0nhx8jdvdak8tu6pa1l18` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `prod_order_ibfk_1` FOREIGN KEY (`products_id`) REFERENCES `products` (`id`),
  CONSTRAINT `prod_order_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `prod_order` */

/*Table structure for table `product` */

DROP TABLE IF EXISTS `product`;

CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `price` double NOT NULL,
  `date` datetime NOT NULL,
  `menu_id` int(11) NOT NULL,
  `subcategory_id` int(11) NOT NULL,
  `pic_url` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `menu_id` (`menu_id`),
  KEY `subcategory_id` (`subcategory_id`),
  CONSTRAINT `product_ibfk_1` FOREIGN KEY (`menu_id`) REFERENCES `menu` (`id`),
  CONSTRAINT `product_ibfk_2` FOREIGN KEY (`subcategory_id`) REFERENCES `subcategory` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

/*Data for the table `product` */

insert  into `product`(`id`,`name`,`description`,`price`,`date`,`menu_id`,`subcategory_id`,`pic_url`) values (2,'Ahmad Tea - Green Tea/Jasmine Green Tea (100 Tea Bags X 200g)','Ahmad Tea Green Tea / Jasmine Green Tea (100 Tagged Tea Bags x 2g) 200g',20,'2019-04-18 13:22:58',4,12,'1555593778454_green-250g_1.png'),(3,'Meet U - T03 ','MALAYSIA ORIGINAL WHITE TEA TARIK',21,'2019-04-18 13:28:33',4,11,'1555593987394_download.jpg'),(4,'READYSTOCK THAILAND GREEN TEA','READYSTOCK THAILAND GREEN TEA',22,'2019-04-18 13:41:31',4,12,'1555594890663_cda96a6a55bf585abfb7bd925c2dd52e.jpg'),(5,'Twinings 100% Pure White Tea (20 Teabags)','Twinings 100% Pure White Tea (20 Teabags)',23,'2019-04-18 13:43:52',4,11,'1555595031880_download.jpg');

/*Table structure for table `products` */

DROP TABLE IF EXISTS `products`;

CREATE TABLE `products` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) NOT NULL,
  `count` int(11) NOT NULL,
  `price` double NOT NULL,
  `user_id` int(11) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `products_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `products` */

/*Table structure for table `subcategory` */

DROP TABLE IF EXISTS `subcategory`;

CREATE TABLE `subcategory` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `category_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `subcategory_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

/*Data for the table `subcategory` */

insert  into `subcategory`(`id`,`name`,`category_id`) values (7,'FruitCakes',9),(8,'FruitCupcake',6),(9,'FruitIceCream',8),(11,'WhiteTea',6),(12,'GreenTea',6),(13,'BlackTea',6);

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `surname` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `user_type` enum('USER','ADMIN') NOT NULL DEFAULT 'USER',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`id`,`name`,`surname`,`email`,`password`,`user_type`) values (3,'a','a','a','$2a$10$fVKLsVSJXoT8tqQF0FbnNO5PFkTxRCN0NzXgf2D/aWngXYo37xN.a','ADMIN'),(4,'b','b','b','$2a$10$oiEjT5iD/2Aw0BGhLU2QEu/waYgA0KOBwksdKrydJLq1FJtD8meH.','USER');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
