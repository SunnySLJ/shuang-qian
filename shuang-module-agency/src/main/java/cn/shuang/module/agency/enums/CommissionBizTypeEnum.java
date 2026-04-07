package cn.shuang.module.agency.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 佣金业务类型枚举
 *
 * @author shuang-pro
 */
@Getter
@AllArgsConstructor
public enum CommissionBizTypeEnum {

    /**
     * 充值分成
     */
    RECHARGE(1, "充值分成"),

    /**
     * 消费分成
     */
    CONSUME(2, "消费分成");

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据 type 枚举
     */
    public static CommissionBizTypeEnum valueOfType(Integer type) {
        for (CommissionBizTypeEnum value : values()) {
            if (ObjUtil.equal(value.type, type)) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知的佣金业务类型：" + type);
    }

}
