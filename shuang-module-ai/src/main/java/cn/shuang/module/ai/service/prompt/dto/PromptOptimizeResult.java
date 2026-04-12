package cn.shuang.module.ai.service.prompt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Prompt 优化结果
 *
 * @author shuang-pro
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptOptimizeResult {

    /**
     * 原始用户输入
     */
    private String originalPrompt;

    /**
     * 完整英文提示词（用于国际模型）
     */
    private String fullPromptEn;

    /**
     * 完整中文提示词（用于国内模型）
     */
    private String fullPromptZh;

    // ===== 视频专用字段 =====

    /**
     * 镜头运动
     */
    private String cameraMovement;

    /**
     * 情绪表达
     */
    private String emotion;

    /**
     * 光影效果
     */
    private String lighting;

    /**
     * 画面风格
     */
    private String style;

    /**
     * 时长建议
     */
    private String duration;

    /**
     * 主体描述
     */
    private String subject;

    /**
     * 背景描述
     */
    private String background;

    /**
     * 音频建议
     */
    private String audio;

    // ===== 图片专用字段 =====

    /**
     * 构图方式
     */
    private String composition;

    /**
     * 色彩方案
     */
    private String colorPalette;

    /**
     * 负向提示词
     */
    private String negativePrompt;

    /**
     * 质量关键词
     */
    private String qualityTags;

    // ===== 数字人专用字段 =====

    /**
     * 数字人形象描述
     */
    private String avatarDescription;

    /**
     * 说话内容
     */
    private String speechContent;

    /**
     * 手势动作
     */
    private String gesture;

    /**
     * 表情变化
     */
    private String expression;

    /**
     * 头部动作
     */
    private String headMovement;

    /**
     * 身体姿态
     */
    private String bodyPosture;

    /**
     * 镜头角度
     */
    private String cameraAngle;

    /**
     * 开头钩子
     */
    private String hook;

    /**
     * 目标人群
     */
    private String targetAudience;

    /**
     * 卖点拆解
     */
    private String sellingPoints;

    /**
     * 镜头拆解建议
     */
    private String shotBreakdown;

    /**
     * 文案策略
     */
    private String copyStrategy;

    /**
     * 平台适配建议
     */
    private String platformTips;

    /**
     * 优化说明
     */
    private String optimizationReasoning;
} 
