package cn.shuang.module.agency.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 积分分配业务类型枚举
 *
 * @author shuang-pro
 */
@Getter
@AllArgsConstructor
public enum PointTransferBizTypeEnum {

    RECHARGE_COMMISSION(1, "充值分成"),
    MANUAL_TRANSFER(2, "手动分配"),
    REFUND_DEDUCT(3, "退款扣回");

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 名称
     */
    private final String name;

}
