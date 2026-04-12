package cn.shuang.module.ai.service.prompt;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * App 端轻量 Prompt 构建工具。
 *
 * <p>用于在服务端补齐基础结构化约束，避免前端和后端对提示词的理解完全分叉。</p>
 */
public final class AppPromptBuildUtils {

    private AppPromptBuildUtils() {}

    public static String buildImagePrompt(String rawPrompt, String style, String negativePrompt,
                                          Integer width, Integer height) {
        List<String> sections = new ArrayList<>();
        sections.add("### Instruction");
        sections.add("请将以下需求组织为适合 AI 图片生成模型执行的最终中文提示词。");
        sections.add("### Context");
        if (StrUtil.isNotBlank(style)) {
            sections.add("风格约束：" + style);
        }
        if (width != null && height != null) {
            sections.add("画幅约束：" + width + "x" + height);
        }
        sections.add("请突出主体、场景、光线、构图、材质细节，避免空泛形容词堆砌。");
        if (StrUtil.isNotBlank(negativePrompt)) {
            sections.add("规避元素：" + negativePrompt);
        }
        sections.add("### Input");
        sections.add(StrUtil.blankToDefault(rawPrompt, ""));
        sections.add("### Output Indicator");
        sections.add("输出单条中文图片 Prompt，直接给出最终画面描述，不要解释。");
        return String.join("\n", sections);
    }

    public static String buildVideoPrompt(String rawPrompt, Integer duration) {
        return buildVideoPrompt(rawPrompt, duration, null);
    }

    public static String buildVideoPrompt(String rawPrompt, Integer duration, Map<String, Object> options) {
        List<String> sections = new ArrayList<>();
        sections.add("### Instruction");
        sections.add("请将以下需求组织为适合 AI 视频生成模型执行的最终中文提示词。");
        sections.add("### Context");
        if (duration != null) {
            sections.add("时长策略：" + duration + " 秒，镜头节奏明确，重点突出。");
        }
        if (options != null) {
            addOptionSection(sections, "画幅约束", options.get("ratio"));
            addOptionSection(sections, "风格类型", options.get("styleType"));
            addOptionSection(sections, "视觉风格", options.get("visualStyle"));
            addOptionSection(sections, "运镜方式", options.get("cameraMovement"));
            addOptionSection(sections, "画面重点", options.get("shotFocus"));
            addOptionSection(sections, "质量目标", options.get("qualityLevel"));
        }
        sections.add("请明确主体、场景、镜头运动、情绪节奏、光线氛围和结尾动作，保证镜头连续可生成。");
        sections.add("### Input");
        sections.add(StrUtil.blankToDefault(rawPrompt, ""));
        sections.add("### Output Indicator");
        sections.add("输出单条中文视频 Prompt，直接给出最终镜头描述，不要解释。");
        return String.join("\n", sections);
    }

    private static void addOptionSection(List<String> sections, String label, Object value) {
        if (value == null) {
            return;
        }
        String text = String.valueOf(value).trim();
        if (StrUtil.isNotBlank(text)) {
            sections.add(label + "：" + text);
        }
    }
}
