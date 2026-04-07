package cn.shuang.module.ai.framework.content.agent;

import cn.shuang.module.ai.framework.content.context.ContentContext;
import cn.shuang.module.ai.framework.content.context.ContentPlanResult;
import cn.shuang.module.ai.framework.content.context.ContentPlanResult.Storyboard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容规划 Agent
 * <p>
 * 基于热门分析结果，生成内容规划：
 * - 内容大纲和结构
 * - 分镜脚本设计
 * - BGM 和特效推荐
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "yudao.ai.content-generation.enable", havingValue = "true")
public class ContentPlannerAgent extends BaseContentAgent {

    public ContentPlannerAgent(ChatClient chatClient) {
        super("ContentPlanner", chatClient);
    }

    private static final String SYSTEM_PROMPT = """
        你是一名资深短视频内容策划专家，擅长根据爆款拆解结果规划新的内容。

        请基于提供的热门分析结果，生成一份完整的内容规划方案：

        1. **内容定位**
           - 标题：吸引眼球且符合平台调性
           - 概述：用一句话说清楚内容核心
           - 目标受众：明确内容给谁看
           - 核心情绪：内容要传达的主要情绪（励志/搞笑/治愈/知识等）

        2. **分镜脚本设计**（至少 5 个分镜）
           每个分镜包含：
           - 序号和时间范围
           - 画面描述（具体、可执行）
           - 台词/文案
           - 运镜方式
           - 景别（远景/全景/中景/近景/特写）

        3. **BGM 推荐**
           - 音乐风格和具体推荐
           - 情绪基调
           - 音量处理建议

        4. **特效建议**
           - 转场特效
           - 画面特效
           - 文字特效

        5. **时长预估**
           - 预计总时长（秒）

        注意：
        - 分镜设计要具体，能够直接用于视频生成
        - 考虑平台的用户喜好和算法偏好
        - 保持内容的连贯性和节奏感
        """;

    @Override
    public AgentStepResult execute(ContentContext context) {
        try {
            log.info("[ContentPlanner] 开始规划内容，风格：{}", context.getStyle());

            String userPrompt = buildUserPrompt(context);

            ContentPlanResult result = chatJson(SYSTEM_PROMPT, userPrompt, ContentPlanResult.class);

            // 如果解析失败，返回空结果
            if (result == null || result.getStoryboards() == null) {
                List<Storyboard> defaultStoryboards = new ArrayList<>();
                defaultStoryboards.add(Storyboard.builder()
                        .sequence(1)
                        .timeRange("00:00-00:05")
                        .visualDescription("开场画面")
                        .script("吸引注意力的开场白")
                        .cameraMovement("缓慢推进")
                        .shotSize("中景")
                        .build());

                return result(ContentPlanResult.builder()
                        .title("内容规划")
                        .overview("基于用户输入生成的内容")
                        .storyboards(defaultStoryboards)
                        .estimatedDuration(30)
                        .build());
            }

            log.info("[ContentPlanner] 规划完成，分镜数：{}, 预计时长：{}s",
                    result.getStoryboards().size(), result.getEstimatedDuration());

            return result(result);

        } catch (Exception e) {
            log.error("[ContentPlanner] 规划失败", e);
            return fail("内容规划失败：" + e.getMessage());
        }
    }

    private String buildUserPrompt(ContentContext context) {
        StringBuilder sb = new StringBuilder();

        // 热门分析结果
        if (context.getTrendResult() != null) {
            sb.append("=== 热门分析结果 ===\n");
            if (context.getTrendResult().getScript() != null) {
                sb.append("原文案：").append(context.getTrendResult().getScript().getFullText()).append("\n\n");
            }
            if (context.getTrendResult().getVisuals() != null) {
                sb.append("视觉元素：").append(context.getTrendResult().getVisuals().getElements()).append("\n\n");
            }
            if (context.getTrendResult().getRhythm() != null) {
                sb.append("节奏分析：").append(context.getTrendResult().getRhythm().getPaceAnalysis()).append("\n\n");
            }
        }

        // 用户输入
        if (context.getUserInput() != null) {
            sb.append("=== 用户需求 ===\n");
            sb.append(context.getUserInput()).append("\n\n");
        }

        // 风格和分类
        if (context.getStyle() != null) {
            sb.append("内容风格：").append(context.getStyle()).append("\n");
        }
        if (context.getCategory() != null) {
            sb.append("行业分类：").append(context.getCategory()).append("\n");
        }

        sb.append("\n请基于以上信息生成内容规划方案。");

        return sb.toString();
    }
}
