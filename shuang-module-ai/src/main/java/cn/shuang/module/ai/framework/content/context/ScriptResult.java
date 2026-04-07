package cn.shuang.module.ai.framework.content.context;

import lombok.Builder;
import lombok.Data;

/**
 * 脚本撰写结果
 * <p>
 * ScriptWriterAgent 的输出结果，包含完整的视频脚本
 */
@Data
@Builder
public class ScriptResult {

    /**
     * 完整脚本文案
     */
    private String fullScript;

    /**
     * 开场白（前 3 秒钩子）
     */
    private String opening;

    /**
     * 正文内容
     */
    private String body;

    /**
     * 结尾呼吁（CTA）
     */
    private String callToAction;

    /**
     * 情绪曲线描述
     */
    private String emotionalArc;

    /**
     * 字数统计
     */
    private Integer wordCount;

    /**
     * 预计朗读时长（秒）
     */
    private Integer estimatedDuration;
}
