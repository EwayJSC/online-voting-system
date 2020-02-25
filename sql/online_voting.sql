/*
 Navicat Premium Data Transfer

 Source Server         : docker-mysql
 Source Server Type    : MySQL
 Source Server Version : 50723
 Source Host           : localhost:3306
 Source Schema         : online_voting

 Target Server Type    : MySQL
 Target Server Version : 50723
 File Encoding         : 65001

 Date: 25/02/2020 14:17:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for candidates
-- ----------------------------
DROP TABLE IF EXISTS `candidates`;
CREATE TABLE `candidates` (
  `id` char(50) NOT NULL,
  `name` char(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of candidates
-- ----------------------------
BEGIN;
INSERT INTO `candidates` VALUES ('7f3201c0-d376-4124-a12f-c3b832c3e780', 'Nguyen Van C');
INSERT INTO `candidates` VALUES ('87a4a7bc-e931-4811-96a3-be7d55824986', 'Nguyen Van D');
INSERT INTO `candidates` VALUES ('e5a85ef0-28aa-45cb-8527-377d88a83582', 'Nguyen Van A');
INSERT INTO `candidates` VALUES ('fa10fe89-7752-4fd7-84e1-24f1cfbd799f', 'Nguyen Van B');
COMMIT;

-- ----------------------------
-- Table structure for votes
-- ----------------------------
DROP TABLE IF EXISTS `votes`;
CREATE TABLE `votes` (
  `id` char(50) NOT NULL,
  `voter_id` char(50) DEFAULT NULL,
  `candidate_id` char(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fkey_candidate_id` (`candidate_id`),
  CONSTRAINT `votes_ibfk_1` FOREIGN KEY (`candidate_id`) REFERENCES `candidates` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of votes
-- ----------------------------
BEGIN;
INSERT INTO `votes` VALUES ('31908ba9-e346-4bc4-aa61-1ed6efb83609', 'd9288625-3a29-47b0-b397-f79171843821', '7f3201c0-d376-4124-a12f-c3b832c3e780');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
