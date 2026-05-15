-- MySQL dump 10.13  Distrib 5.7.29, for macos10.14 (x86_64)
--
-- Host: localhost    Database: coinflow
-- ------------------------------------------------------
-- Server version	5.7.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cf_account`
--

DROP TABLE IF EXISTS `cf_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_account` (
  `id` bigint(20) NOT NULL COMMENT '账单ID（雪花算法生成）',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `account_book_id` bigint(20) NOT NULL COMMENT '所属账本ID',
  `asset_account_id` bigint(20) DEFAULT NULL COMMENT '关联的资产账户ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `type` enum('INCOME','EXPENSE') NOT NULL COMMENT '账单类型：收入/支出',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `image_url` varchar(255) DEFAULT NULL COMMENT '图片凭证 URL',
  `location` varchar(100) DEFAULT NULL COMMENT '位置记录',
  `happened_at` datetime NOT NULL COMMENT '发生时间',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签，逗号分隔',
  `is_reimbursed` tinyint(1) DEFAULT '0' COMMENT '是否可报销',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_account`
--

LOCK TABLES `cf_account` WRITE;
/*!40000 ALTER TABLE `cf_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_account_book`
--

DROP TABLE IF EXISTS `cf_account_book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_account_book` (
  `id` bigint(20) NOT NULL COMMENT '账本ID（雪花算法生成）',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `name` varchar(100) NOT NULL COMMENT '账本名称',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标样式',
  `color` varchar(20) DEFAULT NULL COMMENT '颜色标识',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否为默认账本',
  `is_archived` tinyint(1) DEFAULT '0' COMMENT '是否已归档',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账本表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_account_book`
--

LOCK TABLES `cf_account_book` WRITE;
/*!40000 ALTER TABLE `cf_account_book` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_account_book` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_account_category`
--

DROP TABLE IF EXISTS `cf_account_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_account_category` (
  `id` bigint(20) NOT NULL COMMENT '分类ID（雪花算法生成）',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID（0 表示系统默认）',
  `account_book_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '所属账本ID（0 表示通用分类）',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `type` enum('INCOME','EXPENSE') NOT NULL COMMENT '分类类型：收入/支出',
  `pid` bigint(20) DEFAULT '0' COMMENT '父分类ID（0 表示顶级分类）',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标样式',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序值',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单分类表（支持父子嵌套结构）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_account_category`
--

LOCK TABLES `cf_account_category` WRITE;
/*!40000 ALTER TABLE `cf_account_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_account_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_asset_account`
--

DROP TABLE IF EXISTS `cf_asset_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_asset_account` (
  `id` bigint(20) NOT NULL COMMENT '资产账户ID（雪花算法生成）',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `account_book_id` bigint(20) NOT NULL COMMENT '所属账本ID',
  `name` varchar(100) NOT NULL COMMENT '账户名称',
  `type` enum('CASH','BANK','ALIPAY','WECHAT','CREDIT') NOT NULL COMMENT '账户类型',
  `balance` decimal(12,2) DEFAULT '0.00' COMMENT '账户当前余额',
  `is_hidden` tinyint(1) DEFAULT '0' COMMENT '是否隐藏账户',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序值',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产账户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_asset_account`
--

LOCK TABLES `cf_asset_account` WRITE;
/*!40000 ALTER TABLE `cf_asset_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_asset_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_budget`
--

DROP TABLE IF EXISTS `cf_budget`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_budget` (
  `id` bigint(20) NOT NULL COMMENT '预算ID（雪花算法生成）',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `account_book_id` bigint(20) NOT NULL COMMENT '所属账本ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `month` varchar(7) NOT NULL COMMENT '预算月份，格式：YYYY-MM',
  `amount` decimal(10,2) NOT NULL COMMENT '预算金额',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预算设置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_budget`
--

LOCK TABLES `cf_budget` WRITE;
/*!40000 ALTER TABLE `cf_budget` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_budget` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_feedback`
--

DROP TABLE IF EXISTS `cf_feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_feedback` (
  `id` bigint(20) NOT NULL COMMENT '反馈ID（雪花算法生成）',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `content` text NOT NULL COMMENT '建议内容',
  `status` enum('PENDING','PROCESSING','DONE') DEFAULT 'PENDING' COMMENT '处理状态',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户建议/反馈表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_feedback`
--

LOCK TABLES `cf_feedback` WRITE;
/*!40000 ALTER TABLE `cf_feedback` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_notify`
--

DROP TABLE IF EXISTS `cf_notify`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_notify` (
  `id` bigint(20) NOT NULL COMMENT '通知ID（雪花算法生成）',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `type` enum('BUDGET','SYSTEM','BILL') NOT NULL COMMENT '通知类型',
  `title` varchar(100) DEFAULT NULL COMMENT '通知标题',
  `content` text COMMENT '通知内容',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知与提醒表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_notify`
--

LOCK TABLES `cf_notify` WRITE;
/*!40000 ALTER TABLE `cf_notify` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_notify` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_transfer_record`
--

DROP TABLE IF EXISTS `cf_transfer_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_transfer_record` (
  `id` bigint(20) NOT NULL COMMENT '转账记录ID（雪花算法生成）',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `account_book_id` bigint(20) NOT NULL COMMENT '所属账本ID',
  `from_account_id` bigint(20) NOT NULL COMMENT '来源账户ID',
  `to_account_id` bigint(20) NOT NULL COMMENT '目标账户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '转账金额',
  `fee` decimal(10,2) DEFAULT '0.00' COMMENT '手续费',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `transferred_at` datetime NOT NULL COMMENT '转账时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='转账记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_transfer_record`
--

LOCK TABLES `cf_transfer_record` WRITE;
/*!40000 ALTER TABLE `cf_transfer_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_transfer_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_user`
--

DROP TABLE IF EXISTS `cf_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_user` (
  `id` bigint(20) NOT NULL COMMENT '主键ID（雪花算法生成）',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `password` varchar(100) NOT NULL COMMENT '加密后的密码',
  `avatar` varchar(255) DEFAULT NULL COMMENT '用户头像URL',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_user`
--

LOCK TABLES `cf_user` WRITE;
/*!40000 ALTER TABLE `cf_user` DISABLE KEYS */;
INSERT INTO `cf_user` VALUES (724976087066611712,'testuser01','test@example.com','13800138000','$2a$10$l1AlX/qCNzaqM5qWjSw3huMQRuDjieCoIweZx381zkLyjzkvEdcLC','https://example.com/avatar.png','2025-06-23 13:16:23','2025-06-23 13:16:23');
/*!40000 ALTER TABLE `cf_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'coinflow'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

--
-- Table structure for table `cf_recurring`
--

DROP TABLE IF EXISTS `cf_recurring`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_recurring` (
  `id` bigint(20) NOT NULL COMMENT '周期性账单ID（雪花算法生成）',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `account_book_id` bigint(20) NOT NULL COMMENT '所属账本ID',
  `asset_account_id` bigint(20) DEFAULT NULL COMMENT '关联的资产账户ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `type` enum('INCOME','EXPENSE') NOT NULL COMMENT '账单类型：收入/支出',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `frequency` enum('DAILY','WEEKLY','MONTHLY','YEARLY') NOT NULL COMMENT '重复频率',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否激活',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='周期性账单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_recurring`
--

LOCK TABLES `cf_recurring` WRITE;
/*!40000 ALTER TABLE `cf_recurring` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_recurring` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_template`
--

DROP TABLE IF EXISTS `cf_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_template` (
  `id` bigint(20) NOT NULL COMMENT '模板ID（雪花算法生成）',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `account_book_id` bigint(20) NOT NULL COMMENT '所属账本ID',
  `asset_account_id` bigint(20) DEFAULT NULL COMMENT '关联的资产账户ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `type` enum('INCOME','EXPENSE') NOT NULL COMMENT '账单类型：收入/支出',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `name` varchar(100) NOT NULL COMMENT '模板名称',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_template`
--

LOCK TABLES `cf_template` WRITE;
/*!40000 ALTER TABLE `cf_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_template` ENABLE KEYS */;
UNLOCK TABLES;

-- Dump completed on 2025-06-30 22:59:49
