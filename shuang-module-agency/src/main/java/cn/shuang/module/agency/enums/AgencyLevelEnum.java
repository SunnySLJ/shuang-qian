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

    LEVEL_1(1, "一级代理", 2000),  // 20% 分成 (万分比)
    LEVEL_2(2, "二级代理", 800);   // 8% 分成 (万分比)

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

}
