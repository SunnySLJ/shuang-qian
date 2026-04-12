-- =============================================
-- agency_commission_record 表增加 level 字段
-- 用于区分一级代理（直接上级）和二级代理（间接上级）的分佣记录
-- 用途：支持二级分佣链路幂等、报表统计
-- =============================================

-- 新增 level 字段（二级分佣链路）
ALTER TABLE `agency_commission_record`
    ADD COLUMN `level` TINYINT DEFAULT NULL COMMENT '代理层级：1-一级代理（直接上级），2-二级代理（间接上级）' AFTER `commission_rate`;

-- 为现有数据设置默认值（一级代理）
UPDATE `agency_commission_record` SET `level` = 1 WHERE `level` IS NULL;

-- 新增幂等唯一索引（biz_order_no + brokerage_user_id + level），防止同一订单对同一代理同一层级重复分佣
-- 注意：对于历史数据（level=1 的），可能存在重复，需要先清理
-- 先删除可能的重复记录（保留 id 最小的一条）
DELETE t1 FROM `agency_commission_record` t1
INNER JOIN `agency_commission_record` t2
WHERE t1.id > t2.id
  AND t1.biz_order_no = t2.biz_order_no
  AND t1.brokerage_user_id = t2.brokerage_user_id
  AND (t1.level = t2.level OR (t1.level IS NULL AND t2.level IS NULL));

-- 再添加唯一索引（同一订单 + 同一代理 + 同一层级只能有一条记录）
ALTER TABLE `agency_commission_record`
    ADD UNIQUE KEY `uk_biz_order_brokerage_level` (`biz_order_no`, `brokerage_user_id`, `level`);
