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

 Date: 07/05/2024 19:18:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_group
-- ----------------------------
DROP TABLE IF EXISTS `tb_group`;
CREATE TABLE `tb_group`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `groupname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `number` int NULL DEFAULT NULL,
  `scale` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `direction` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `visiable` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `Locked` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `publicfunds` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_group
-- ----------------------------
INSERT INTO `tb_group` VALUES (1, '创智科技有限公司', 100, '大型', '人工智能', 'true', 'true', 1000);
INSERT INTO `tb_group` VALUES (2, '蓝图环保集团', 200, '中型', '环保技术研发与污染治理', 'true', 'true', 1000);
INSERT INTO `tb_group` VALUES (3, '星辰文化传媒有限公司', 80, '小型', '影视制作与文化传播', 'true', 'false', 1000);
INSERT INTO `tb_group` VALUES (4, '智慧医疗集团', 1500, '超大型', '医疗设备研发与医疗服务提供', 'true', 'false', 1000);
INSERT INTO `tb_group` VALUES (5, '蓝天生态农业有限公司', 300, '中型', '有机农产品种植与销售', 'true', 'false', 1000);
INSERT INTO `tb_group` VALUES (6, '创新物流集团', 600, '大型', '智能物流解决方案提供与物流服务', 'false', 'false', 1000);

SET FOREIGN_KEY_CHECKS = 1;
