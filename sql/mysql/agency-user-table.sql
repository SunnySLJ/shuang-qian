-- =============================================
-- agency_user 表 - 代理用户关系表
-- 用于存储代理层级关系和累计佣金
-- =============================================

USE shuang_pro;

DROP TABLE IF EXISTS `agency_user`;
CREATE TABLE `agency_user` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id`             BIGINT       NOT NULL COMMENT '用户 ID',
    `parent_agency_id`    BIGINT       DEFAULT NULL COMMENT '上级代理用户 ID',
    `level`               TINYINT      NOT NULL DEFAULT 2 COMMENT '代理等级 (1:一级代理，2:二级代理)',
    `agency_enabled`      BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否代理 (0:否 1:是)',
    `bind_mode`           TINYINT      NOT NULL DEFAULT 2 COMMENT '绑定方式 (1:主动绑定 2:被动绑定)',
    `total_points`        INT          NOT NULL DEFAULT 0 COMMENT '累计获得积分',
    `distributed_points`  INT          NOT NULL DEFAULT 0 COMMENT '已分配积分',
    `total_commission`    INT          NOT NULL DEFAULT 0 COMMENT '累计分成收入（积分，单位：分）',
    `direct_invite_count` INT          NOT NULL DEFAULT 0 COMMENT '直推人数',
    `team_total_count`    INT          NOT NULL DEFAULT 0 COMMENT '团队总人数',
    `agency_fee`          INT          DEFAULT NULL COMMENT '代理费（分）',
    `pay_fee_time`        DATETIME     DEFAULT NULL COMMENT '代理费支付时间',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             BIT(1)       NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_parent_agency_id` (`parent_agency_id`),
    KEY `idx_level` (`level`),
    KEY `idx_agency_enabled` (`agency_enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代理用户关系表';

-- 添加索引以优化查询性能
CREATE INDEX idx_commission_query ON `agency_user` (`user_id`, `parent_agency_id`, `agency_enabled`);
