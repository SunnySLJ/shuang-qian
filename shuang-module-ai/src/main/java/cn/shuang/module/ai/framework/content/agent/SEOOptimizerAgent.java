package cn.shuang.module.ai.framework.content.agent;

import cn.shuang.module.ai.framework.content.context.ContentContext;
import cn.shuang.module.ai.framework.content.context.SEOResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * SEO 优化 Agent
 * <p>
 * 基于视频脚本和内容规划，生成 SEO 优化的标题、标签、描述等，提升视频在平台的搜索排名和曝光度
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "yudao.ai.content-generation.enable", havingValue = "true")
public class SEOOptimizerAgent extends BaseContentAgent {

    public SEOOptimizerAgent(ChatClient chatClient) {
        super("SEOOptimizer", chatClient);
    }

    private static final String SYSTEM_PROMPT = """
        你是一名资深 SEO 专家，专注于短视频平台的搜索优化。

        请基于提供的视频脚本和内容规划，生成 SEO 优化建议：

        1. **优化标题**
           - 包含核心关键词
           - 吸引点击但不标题党
           - 长度控制在 20-30 字
           - 可使用数字、疑问、对比等技巧

        2. **视频描述**
           - 补充标题未表达的信息
           - 包含 2-3 个核心关键词
           - 长度 50-100 字
           - 自然流畅，避免堆砌

        3. **标签建议**
           - 5-8 个标签
           - 包含泛标签和精准标签
           - 考虑平台热门标签

        4. **封面文案**
           - 4-8 字短句
           - 冲击力强，吸引点击
           - 与视频内容一致

        5. **关键词列表**
           - 3-5 个核心搜索词
           - 考虑用户搜索习惯

        6. **发布时间建议**
           - 基于目标受众活跃时间
           - 格式：HH:mm

        注意：
        - 避免敏感词和违规内容
        - 符合平台 SEO 规则
        - 保持真实不夸大
        """;

    @Override
    public AgentStepResult execute(ContentContext context) {
        try {
            log.info("[SEOOptimizer] 开始 SEO 优化");

            String userPrompt = buildUserPrompt(context);

            SEOResult result = chatJson(SYSTEM_PROMPT, userPrompt, SEOResult.class);

            // 如果解析失败，返回默认结果
            if (result == null || result.getOptimizedTitle() == null) {
                result = SEOResult.builder()
                        .optimizedTitle(context.getPlanResult() != null ? context.getPlanResult().getTitle() : "视频标题")
                        .description("视频描述")
                        .tags(java.util.List.of("热门", "推荐"))
                        .coverText("精彩推荐")
                        .keywords(java.util.List.of("关键词"))
                        .build();
            }

            log.info("[SEOOptimizer] SEO 优化完成，标题：{}", result.getOptimizedTitle());

            return result(result);

        } catch (Exception e) {
            log.error("[SEOOptimizer] 优化失败", e);
            return fail("SEO 优化失败：" + e.getMessage());
        }
    }

    private String buildUserPrompt(ContentContext context) {
        StringBuilder sb = new StringBuilder();

        // 内容规划结果
        if (context.getPlanResult() != null) {
            sb.append("=== 内容规划 ===\n");
            sb.append("标题：").append(context.getPlanResult().getTitle()).append("\n");
            sb.append("目标受众：").append(context.getPlanResult().getTargetAudience()).append("\n");
            sb.append("核心情绪：").append(context.getPlanResult().getCoreEmotion()).append("\n\n");
        }

        // 脚本结果
        if (context.getScriptResult() != null) {
            sb.append("=== 视频脚本 ===\n");
            sb.append("开场白：").append(context.getScriptResult().getOpening()).append("\n");
            sb.append("正文：").append(context.getScriptResult().getBody()).append("\n");
            sb.append("结尾：").append(context.getScriptResult().getCallToAction()).append("\n");
            sb.append("总字数：").append(context.getScriptResult().getWordCount()).append("\n\n");
        }

        // 行业分类
        if (context.getCategory() != null) {
            sb.append("=== 行业分类 ===\n");
            sb.append(context.getCategory()).append("\n\n");
        }

        // 用户输入
        if (context.getUserInput() != null) {
            sb.append("=== 用户要求 ===\n");
            sb.append(context.getUserInput()).append("\n");
        }

        sb.append("\n请基于以上信息生成 SEO 优化建议。");

        return sb.toString();
    }
}
