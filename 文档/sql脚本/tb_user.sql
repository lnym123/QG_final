/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80300
 Source Host           : localhost:3306
 Source Schema         : db1

 Target Server Type    : MySQL
 Target Server Version : 80300
 File Encoding         : 65001

 Date: 07/05/2024 19:19:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `PhoneNumber` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `groupid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `authority` int NULL DEFAULT NULL,
  `Personalfunds` int NULL DEFAULT NULL,
  `Groupfunds` int NULL DEFAULT NULL,
  `Locked` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 258 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES (1, 'zhangsan', '123', '虚构街道', '13111111111', '蓝图环保集团', 'C:\\Users\\y\\IdeaProjects\\QG_final\\QG_final\\项目\\src\\main\\webapp\\images\\wa.jpg', 1, 2032, 431, 'false');
INSERT INTO `tb_user` VALUES (2, 'lisi', '234', '虚构街道', '13122222222', '创智科技有限公司', 'C:\\Users\\y\\IdeaProjects\\QG_final\\QG_final\\项目\\src\\main\\webapp\\images\\t1.jpg', 2, 5400, 0, 'false');
INSERT INTO `tb_user` VALUES (3, 'wangwu', '1234', '虚构街道', '13232323222', '星辰文化传媒有限公司', NULL, 2, 63027, 700, 'false');
INSERT INTO `tb_user` VALUES (32, 'youke', 'null', '', '', '', NULL, 0, 0, 0, 'false');
INSERT INTO `tb_user` VALUES (233, 'admin', 'admin', NULL, NULL, NULL, NULL, 3, 0, 0, 'false');
INSERT INTO `tb_user` VALUES (234, 'user001', 'password123', '虚构街道', '13800138000', '智慧医疗集团', NULL, 2, 10000, 1000, 'false');
INSERT INTO `tb_user` VALUES (235, 'user002', 'qwerty456', '虚构街道', '13900139000', '蓝天生态农业有限公司', NULL, 2, 1234, 1000, 'false');
INSERT INTO `tb_user` VALUES (236, 'user003', 'abcdef789', '虚构街道', '13700137000', '创新物流集团', NULL, 2, 10000, 10000, 'false');
INSERT INTO `tb_user` VALUES (257, 'test', '12345', '广工', '13111111111', '创智科技有限公司', 'C:\\Users\\y\\IdeaProjects\\QG_final\\QG_final\\项目\\src\\main\\webapp\\images\\t1.jpg', 1, 900, 0, 'false');

SET FOREIGN_KEY_CHECKS = 1;
