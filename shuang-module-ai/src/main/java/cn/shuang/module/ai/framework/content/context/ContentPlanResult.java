package cn.shuang.module.ai.framework.content.context;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 内容规划结果
 * <p>
 * ContentPlannerAgent 的输出结果，包含内容大纲和分镜设计
 */
@Data
@Builder
public class ContentPlanResult {

    /**
     * 内容标题
     */
    private String title;

    /**
     * 内容概述
     */
    private String overview;

    /**
     * 目标受众
     */
    private String targetAudience;

    /**
     * 核心情绪/氛围
     */
    private String coreEmotion;

    /**
     * 分镜脚本列表
     */
    private List<Storyboard> storyboards;

    /**
     * BGM 推荐
     */
    private String bgmRecommendation;

    /**
     * 特效建议
     */
    private List<String> effectSuggestions;

    /**
     * 预计时长（秒）
     */
    private Integer estimatedDuration;

    /**
     * 分镜
     */
    @Data
    @Builder
    public static class Storyboard {
        /**
         * 序号
         */
        private Integer sequence;

        /**
         * 时间范围
         */
        private String timeRange;

        /**
         * 画面描述
         */
        private String visualDescription;

        /**
         * 台词/文案
         */
        private String script;

        /**
         * 运镜方式
         */
        private String cameraMovement;

        /**
         * 景别（远景/全景/中景/近景/特写）
         */
        private String shotSize;
    }
}
