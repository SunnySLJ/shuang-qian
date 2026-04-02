package cn.shuang.module.pay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付方式枚举
 *
 * @author shuang-pro
 */
@Getter
@AllArgsConstructor
public enum PaymentMethodEnum {

    WECHAT(1, "微信支付"),
    ALIPAY(2, "支付宝");

    private final Integer type;
    private final String name;

    /**
     * 根据 type 获取枚举
     */
    public static PaymentMethodEnum valueOfType(Integer type) {
        for (PaymentMethodEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知的支付方式：" + type);
    }

}
