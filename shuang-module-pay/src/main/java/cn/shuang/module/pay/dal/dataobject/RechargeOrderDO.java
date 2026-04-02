package cn.shuang.module.pay.dal.dataobject;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 充值订单 DO
 *
 * @author shuang-pro
 */
@TableName("pay_recharge_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RechargeOrderDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 充值金额（单位：分）
     */
    private Integer amount;

    /**
     * 获得积分（单位：分）
     */
    private Integer points;

    /**
     * 赠送积分（单位：分）
     */
    private Integer bonusPoints;

    /**
     * 总计积分（单位：分）
     */
    private Integer totalPoints;

    /**
     * 支付方式：1-微信，2-支付宝
     */
    private Integer paymentMethod;

    /**
     * 状态：0-待支付，1-已支付，2-已关闭，3-已退款
     */
    private Integer status;

    /**
     * 第三方支付流水号
     */
    private String transactionId;

    /**
     * 支付时间
     */
    private LocalDateTime paidTime;

    /**
     * 过期时间
     */
    private LocalDateTime expiredTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 扩展数据
     */
    private String extraData;

}
