package cn.shuang.module.ai.enums.video;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 视频业务类型枚举
 *
 * @author shuang-pro
 */
@Getter
@AllArgsConstructor
public enum AiVideoBizTypeEnum {

    /**
     * AI 文生视频
     */
    TEXT_TO_VIDEO(-2, 2000, "AI 文生视频"),

    /**
     * AI 图生视频
     */
    IMAGE_TO_VIDEO(-3, 2000, "AI 图生视频"),

    /**
     * 黄金 6 秒拼接
     */
    GOLDEN_6S(-4, 3000, "黄金 6 秒拼接"),

    /**
     * AI 混剪
     */
    AI_MIX(-5, 5000, "AI 混剪"),

    /**
     * 视频拆解 - 提取脚本
     */
    EXTRACT_SCRIPT(-6, 2000, "视频拆解 - 提取脚本"),

    /**
     * 视频拆解 - 分析元素
     */
    ANALYZE_ELEMENTS(-7, 2000, "视频拆解 - 分析元素"),

    /**
     * 视频拆解 - 生成提示词
     */
    GENERATE_PROMPT(-8, 2000, "视频拆解 - 生成提示词");

    /**
     * 业务类型（负数表示支出）
     */
    private final Integer bizType;

    /**
     * 所需积分（单位：分）
     */
    private final Integer points;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据 bizType 获取枚举
     */
    public static AiVideoBizTypeEnum valueOfBizType(Integer bizType) {
        for (AiVideoBizTypeEnum value : values()) {
            if (ObjUtil.equal(value.bizType, bizType)) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知的视频业务类型：" + bizType);
    }

    /**
     * 根据 generationType 获取枚举
     */
    public static AiVideoBizTypeEnum valueOfGenerationType(Integer generationType) {
        for (AiVideoBizTypeEnum value : values()) {
            if (ObjUtil.equal(value.bizType, -generationType)) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知的生成类型：" + generationType);
    }

}
