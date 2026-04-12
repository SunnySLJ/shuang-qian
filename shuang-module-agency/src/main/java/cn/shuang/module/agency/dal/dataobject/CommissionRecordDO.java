package cn.shuang.module.agency.dal.dataobject;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 佣金记录 DO
 *
 * @author shuang-pro
 */
@TableName("agency_commission_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CommissionRecordDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 被分成用户 ID（谁的消费/充值）
     */
    private Long userId;

    /**
     * 分成获得用户 ID（哪个代理获得分成）
     */
    private Long brokerageUserId;

    /**
     * 业务类型：1-充值分成，2-消费分成
     */
    private Integer bizType;

    /**
     * 业务订单号
     */
    private String bizOrderNo;

    /**
     * 关联订单 ID
     */
    private Long orderId;

    /**
     * 分成金额（积分，单位：分）
     */
    private Integer amount;

    /**
     * 分成比例（万分比）
     */
    private Integer commissionRate;

    /**
     * 代理层级：1-一级代理（直接上级），2-二级代理（间接上级）
     * 用于区分同一笔订单对不同层级的分佣记录
     */
    private Integer level;

    /**
     * 状态：0-待结算，1-已结算，2-已取消
     */
    private Integer status;

    /**
     * 结算时间
     */
    private LocalDateTime settleTime;

    /**
     * 备注
     */
    private String remark;

}
