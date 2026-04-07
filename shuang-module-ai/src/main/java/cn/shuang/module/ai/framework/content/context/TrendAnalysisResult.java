package cn.shuang.module.ai.framework.content.context;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 热门内容分析结果
 * <p>
 * TrendAnalyzerAgent 的输出结果，包含对参考视频的完整分析
 */
@Data
@Builder
public class TrendAnalysisResult {

    /**
     * 文案分析结果
     */
    private ScriptAnalysis script;

    /**
     * 视觉元素分析
     */
    private VisualAnalysis visuals;

    /**
     * 音频分析
     */
    private AudioAnalysis audio;

    /**
     * 节奏分析
     */
    private RhythmAnalysis rhythm;

    /**
     * 生成的提示词列表
     */
    private List<String> prompts;

    // ==================== 内部类 ====================

    /**
     * 文案分析结果
     */
    @Data
    @Builder
    public static class ScriptAnalysis {
        /**
         * 完整台词
         */
        private String fullText;

        /**
         * 金句/爆点列表
         */
        private List<String> keyLines;

        /**
         * 情绪高潮点
         */
        private List<EmotionalPeak> emotionalPeaks;
    }

    /**
     * 情绪高潮点
     */
    @Data
    @Builder
    public static class EmotionalPeak {
        /**
         * 时间戳（秒）
         */
        @JsonProperty("timestamp_seconds")
        private Double timestamp;

        /**
         * 对应文案
         */
        private String text;

        /**
         * 情绪强度（1-10）
         */
        @JsonProperty("intensity_score")
        private Integer intensity;
    }

    /**
     * 视觉分析结果
     */
    @Data
    @Builder
    public static class VisualAnalysis {
        /**
         * 场景列表
         */
        private List<SceneDescription> scenes;

        /**
         * 色彩 palette
         */
        private List<String> colorPalette;

        /**
         * 灯光风格
         */
        private String lightingStyle;

        /**
         * 主要元素列表
         */
        private List<String> elements;
    }

    /**
     * 场景描述
     */
    @Data
    @Builder
    public static class SceneDescription {
        /**
         * 时间范围（如 "00:00-00:05"）
         */
        private String timeRange;

        /**
         * 场景描述
         */
        private String description;

        /**
         * 画面元素
         */
        private List<String> elements;

        /**
         * 运镜方式（推/拉/摇/移/跟）
         */
        private String cameraMovement;
    }

    /**
     * 音频分析结果
     */
    @Data
    @Builder
    public static class AudioAnalysis {
        /**
         * BGM 名称
         */
        private String bgmName;

        /**
         * BGM 情绪
         */
        private String bgmMood;

        /**
         * 语速（如 "1.2x"）
         */
        private String voiceSpeed;

        /**
         * 音效列表
         */
        private List<String> soundEffects;
    }

    /**
     * 节奏分析结果
     */
    @Data
    @Builder
    public static class RhythmAnalysis {
        /**
         * 平均镜头时长（秒）
         */
        @JsonProperty("avg_shot_duration")
        private Double avgShotDuration;

        /**
         * 剪辑频率（低/中/高）
         */
        private String cutFrequency;

        /**
         * 节奏分析
         */
        private String paceAnalysis;
    }
}
