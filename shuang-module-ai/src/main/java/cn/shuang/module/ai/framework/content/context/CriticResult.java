package cn.shuang.module.ai.framework.content.context;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 质量审查结果
 * <p>
 * QualityCriticAgent 的输出结果
 */
@Data
@Builder
public class CriticResult {

    /**
     * 是否通过审查
     */
    private boolean passed;

    /**
     * 综合评分（0-100）
     */
    private Integer overallScore;

    /**
     * 优势列表
     */
    private List<String> strengths;

    /**
     * 待改进点列表
     */
    private List<String> weaknesses;

    /**
     * 具体修改建议
     */
    private List<String> suggestions;

    /**
     * 审查详情
     */
    private CriticDetails details;

    @Data
    @Builder
    public static class CriticDetails {
        /**
         * 内容完整性评分（0-100）
         */
        private Integer completenessScore;

        /**
         * 爆款潜力评分（0-100）
         */
        private Integer viralPotentialScore;

        /**
         * 脚本质量评分（0-100）
         */
        private Integer scriptQualityScore;

        /**
         * 视觉吸引力评分（0-100）
         */
        private Integer visualAppealScore;
    }
}
