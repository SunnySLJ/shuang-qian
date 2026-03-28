-- ----------------------------
-- Shuang-Pro 代理系统表结构
-- ----------------------------

-- ----------------------------
-- 代理用户表
-- ----------------------------
DROP TABLE IF EXISTS `agency_user`;
CREATE TABLE `agency_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `parent_agency_id` bigint DEFAULT NULL COMMENT '上级代理用户 ID',
  `level` tinyint NOT NULL DEFAULT '2' COMMENT '代理等级 (1:一级代理，2:二级代理)',
  `agency_enabled` tinyint NOT NULL DEFAULT '0' COMMENT '是否代理 (0:否 1:是)',
  `bind_mode` tinyint NOT NULL DEFAULT '2' COMMENT '绑定方式 (1:主动绑定 2:被动绑定)',
  `total_points` int NOT NULL DEFAULT '0' COMMENT '累计获得积分',
  `distributed_points` int NOT NULL DEFAULT '0' COMMENT '已分配积分',
  `direct_invite_count` int NOT NULL DEFAULT '0' COMMENT '直推人数',
  `team_total_count` int NOT NULL DEFAULT '0' COMMENT '团队总人数',
  `agency_fee` int DEFAULT NULL COMMENT '代理费（分）',
  `pay_fee_time` datetime DEFAULT NULL COMMENT '代理费支付时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_parent_agency_id` (`parent_agency_id`),
  KEY `idx_level` (`level`),
  KEY `idx_agency_enabled` (`agency_enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代理用户表';

-- ----------------------------
-- 代理配置表
-- ----------------------------
DROP TABLE IF EXISTS `agency_config`;
CREATE TABLE `agency_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` varchar(500) DEFAULT NULL COMMENT '配置值',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代理配置表';

-- ----------------------------
-- 初始化代理配置数据
-- ----------------------------
INSERT INTO `agency_config` (`config_key`, `config_value`, `description`, `enabled`) VALUES
('level1_commission_rate', '2000', '一级代理分成比例 (万分比)', 1),
('level2_commission_rate', '800', '二级代理分成比例 (万分比)', 1),
('level1_fee', '99900', '一级代理费用（分）', 1),
('level1_min_invite', '100', '一级代理最少直推人数', 1),
('recharge_point_ratio', '100', '充值积分比例 (1 元=100 积分)', 1),
('video_cost', '1000', 'AI 视频生成消耗积分', 1),
('image_cost', '200', 'AI 图片生成消耗积分', 1);

-- ----------------------------
-- 积分分配记录表
-- ----------------------------
DROP TABLE IF EXISTS `agency_point_transfer`;
CREATE TABLE `agency_point_transfer` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `from_user_id` bigint NOT NULL COMMENT '分配方用户 ID (代理)',
  `to_user_id` bigint NOT NULL COMMENT '接收方用户 ID',
  `point_amount` int NOT NULL COMMENT '积分数量',
  `order_id` bigint DEFAULT NULL COMMENT '关联订单 ID',
  `biz_type` tinyint NOT NULL COMMENT '业务类型 (1:充值分成 2:手动分配 3:退款扣回)',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_from_user_id` (`from_user_id`),
  KEY `idx_to_user_id` (`to_user_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_biz_type` (`biz_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分分配记录表';
