-- 用户资产相关表
-- Date: 2026-04-07

SET NAMES utf8mb4;

-- ----------------------------
-- Table structure for ai_user_asset
-- ----------------------------
DROP TABLE IF EXISTS `ai_user_asset`;
CREATE TABLE `ai_user_asset` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户 ID',
    `asset_type` tinyint NOT NULL COMMENT '资产类型：1-图片，2-视频，3-音频',
    `resource_url` varchar(512) NOT NULL COMMENT '资源 URL',
    `thumbnail_url` varchar(512) COMMENT '缩略图 URL',
    `title` varchar(128) COMMENT '标题',
    `description` varchar(512) COMMENT '描述',
    `file_size` bigint COMMENT '文件大小（字节）',
    `duration` int COMMENT '时长（秒，视频/音频）',
    `width` int COMMENT '宽度',
    `height` int COMMENT '高度',
    `group_id` bigint COMMENT '分组 ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_asset_type` (`asset_type`),
    INDEX `idx_group_id` (`group_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户资产表';

-- ----------------------------
-- Table structure for ai_material_group
-- ----------------------------
DROP TABLE IF EXISTS `ai_material_group`;
CREATE TABLE `ai_material_group` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户 ID',
    `name` varchar(64) NOT NULL COMMENT '分组名称',
    `sort_order` int DEFAULT 0 COMMENT '排序',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '素材分组表';

-- ----------------------------
-- 初始化默认分组数据
-- ----------------------------
INSERT INTO `ai_material_group` (`user_id`, `name`, `sort_order`) VALUES
(1, '默认分组', 0);