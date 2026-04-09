-- 灵感案例相关表
-- Date: 2026-04-07

SET NAMES utf8mb4;

-- ----------------------------
-- Table structure for ai_inspiration_category
-- ----------------------------
DROP TABLE IF EXISTS `ai_inspiration_category`;
CREATE TABLE `ai_inspiration_category` (
    `id` int NOT NULL COMMENT '分类ID',
    `name` varchar(64) NOT NULL COMMENT '分类名称',
    `icon` varchar(512) DEFAULT '' COMMENT '分类图标',
    `sort_order` int DEFAULT 0 COMMENT '排序',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_sort` (`sort_order`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '灵感案例分类表';

-- ----------------------------
-- Table structure for ai_inspiration_case
-- ----------------------------
DROP TABLE IF EXISTS `ai_inspiration_case`;
CREATE TABLE `ai_inspiration_case` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type` varchar(32) NOT NULL COMMENT '类型：banana/veo/grok/seedance',
    `category_id` int DEFAULT 0 COMMENT '分类ID',
    `title` varchar(128) NOT NULL COMMENT '案例标题',
    `content` text COMMENT '提示词内容',
    `image` varchar(512) DEFAULT '' COMMENT '封面图URL',
    `image_first` varchar(512) DEFAULT '' COMMENT '首帧图URL',
    `image_tail` varchar(512) DEFAULT '' COMMENT '尾帧图URL',
    `video_url` varchar(512) DEFAULT '' COMMENT '视频URL',
    `duration` int DEFAULT 0 COMMENT '视频时长(秒)',
    `like_count` int DEFAULT 0 COMMENT '点赞数',
    `view_count` int DEFAULT 0 COMMENT '浏览数',
    `use_count` int DEFAULT 0 COMMENT '使用数',
    `label` varchar(64) DEFAULT '' COMMENT '标签',
    `icon` varchar(512) DEFAULT '' COMMENT '图标URL',
    `featured` bit(1) DEFAULT b'0' COMMENT '是否精选',
    `sort_order` int DEFAULT 0 COMMENT '排序',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    INDEX `idx_category` (`category_id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_featured` (`featured`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '灵感案例表';

-- ----------------------------
-- Table structure for ai_tutorial_category
-- ----------------------------
DROP TABLE IF EXISTS `ai_tutorial_category`;
CREATE TABLE `ai_tutorial_category` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(64) NOT NULL COMMENT '分类名称',
    `description` varchar(255) DEFAULT '' COMMENT '分类描述',
    `icon_url` varchar(512) DEFAULT '' COMMENT '分类图标URL',
    `sort_order` int DEFAULT 0 COMMENT '排序',
    `video_count` int DEFAULT 0 COMMENT '视频数量',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    INDEX `idx_sort` (`sort_order`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教程视频分类表';

-- ----------------------------
-- Table structure for ai_tutorial_video
-- ----------------------------
DROP TABLE IF EXISTS `ai_tutorial_video`;
CREATE TABLE `ai_tutorial_video` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category_id` bigint NOT NULL COMMENT '分类ID',
    `name` varchar(128) NOT NULL COMMENT '教程名称',
    `cover` varchar(512) DEFAULT '' COMMENT '封面图URL',
    `video_url` varchar(512) NOT NULL COMMENT '视频URL',
    `duration` int DEFAULT 0 COMMENT '视频时长(秒)',
    `view_count` int DEFAULT 0 COMMENT '观看数',
    `like_count` int DEFAULT 0 COMMENT '点赞数',
    `is_free` bit(1) DEFAULT b'1' COMMENT '是否免费',
    `sort_order` int DEFAULT 0 COMMENT '排序',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    INDEX `idx_category` (`category_id`),
    INDEX `idx_sort` (`sort_order`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教程视频表';