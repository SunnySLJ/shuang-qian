package cn.shuang.module.agency.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 代理等级枚举
 *
 * @author shuang-pro
 */
@Getter
@AllArgsConstructor
public enum AgencyLevelEnum {

    LEVEL_1(1, "一级代理", 2000),   // 20% 分成 (万分比) —— 直接上级的分成比例
    LEVEL_2(2, "二级代理", 800);    // 8% 分成 (万分比) —— 二级代理的定义比例（非分佣比例）

    /**
     * 等级
     */
    private final Integer level;

    /**
     * 等级名称
     */
    private final String name;

    /**
     * 分成比例 (万分比)
     */
    private final Integer commissionRate;

    /**
     * 根据等级获取分成比例
     *
     * @param level 等级
     * @return 分成比例 (万分比)
     */
    public static Integer getCommissionRateByLevel(Integer level) {
        for (AgencyLevelEnum item : values()) {
            if (item.getLevel().equals(level)) {
                return item.getCommissionRate();
            }
        }
        return 0;
    }

    // ===================== 以下为二级分佣专用比例 =====================
    // 二级分佣：当"直接充值用户的代理"拿到分成后，其上级（间接上级）也按此比例分佣
    // 例：用户充值100元，直接上级拿20%，间接上级拿5%（总计25%，非叠加）
    private static final Integer SECONDARY_COMMISSION_RATE = 500; // 5% = 500/10000

    /**
     * 获取二级分佣比例（间接上级的分佣比例）
     * 当前用户的上级拿到分成后，其上级的上级按此比例再分佣
     *
     * @return 二级分佣比例 (万分比)，当前固定 5%
     */
    public static Integer getSecondaryCommissionRate() {
        return SECONDARY_COMMISSION_RATE;
    }

}
