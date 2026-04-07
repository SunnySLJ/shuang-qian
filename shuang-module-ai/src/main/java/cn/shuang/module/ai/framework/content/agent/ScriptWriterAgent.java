package cn.shuang.module.ai.framework.content.agent;

import cn.shuang.module.ai.framework.content.context.ContentContext;
import cn.shuang.module.ai.framework.content.context.ScriptResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 脚本撰写 Agent
 * <p>
 * 基于内容规划，生成完整的视频脚本：
 * - 开场白（前 3 秒钩子）
 * - 正文内容
 * - 结尾呼吁（CTA）
 * - 情绪曲线设计
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "yudao.ai.content-generation.enable", havingValue = "true")
public class ScriptWriterAgent extends BaseContentAgent {

    public ScriptWriterAgent(ChatClient chatClient) {
        super("ScriptWriter", chatClient);
    }

    private static final String SYSTEM_PROMPT = """
        你是一名资深短视频脚本作家，擅长创作高完播率、高互动的爆款脚本。

        请基于提供的内容规划，生成一份完整的视频脚本：

        1. **开场白（前 3 秒钩子）**
           - 必须在 3 秒内抓住注意力
           - 常用技巧：提问、反差、悬念、痛点
           - 字数控制在 15 字以内

        2. **正文内容**
           - 承接开场，展开核心内容
           - 节奏紧凑，避免废话
           - 设计情绪起伏点
           - 每 5 秒一个信息点/视觉变化

        3. **结尾呼吁（CTA）**
           - 引导互动（点赞/评论/收藏/转发）
           - 引导关注
           - 自然不突兀

        4. **情绪曲线**
           - 描述整个脚本的情绪变化
           - 标注高潮点位置

        5. **字数控制**
           - 15 秒视频：约 60-80 字
           - 30 秒视频：约 120-150 字
           - 60 秒视频：约 220-280 字

        注意：
        - 口语化表达，避免书面语
        - 考虑朗读节奏和停顿
        - 避免敏感词和违规内容
        - 符合平台调性和用户喜好
        """;

    @Override
    public AgentStepResult execute(ContentContext context) {
        try {
            log.info("[ScriptWriter] 开始撰写脚本");

            String userPrompt = buildUserPrompt(context);

            ScriptResult result = chatJson(SYSTEM_PROMPT, userPrompt, ScriptResult.class);

            // 如果解析失败，返回空结果
            if (result == null || result.getFullScript() == null) {
                return result(ScriptResult.builder()
                        .fullScript("根据内容规划生成的完整脚本文案。")
                        .opening("吸引人的开场白")
                        .body("正文内容部分")
                        .callToAction("喜欢就点个赞吧！")
                        .wordCount(50)
                        .estimatedDuration(15)
                        .build());
            }

            log.info("[ScriptWriter] 脚本完成，字数：{}, 预计时长：{}s",
                    result.getWordCount(), result.getEstimatedDuration());

            return result(result);

        } catch (Exception e) {
            log.error("[ScriptWriter] 撰写失败", e);
            return fail("脚本撰写失败：" + e.getMessage());
        }
    }

    private String buildUserPrompt(ContentContext context) {
        StringBuilder sb = new StringBuilder();

        // 内容规划结果
        if (context.getPlanResult() != null) {
            sb.append("=== 内容规划 ===\n");
            sb.append("标题：").append(context.getPlanResult().getTitle()).append("\n");
            sb.append("概述：").append(context.getPlanResult().getOverview()).append("\n");
            sb.append("目标受众：").append(context.getPlanResult().getTargetAudience()).append("\n");
            sb.append("核心情绪：").append(context.getPlanResult().getCoreEmotion()).append("\n\n");

            if (context.getPlanResult().getStoryboards() != null) {
                sb.append("分镜脚本：\n");
                context.getPlanResult().getStoryboards().forEach(storyboard ->
                        sb.append("  - ").append(storyboard.getTimeRange()).append(": ")
                                .append(storyboard.getVisualDescription()).append("\n")
                                .append("    台词：").append(storyboard.getScript()).append("\n")
                );
                sb.append("\n");
            }

            if (context.getPlanResult().getBgmRecommendation() != null) {
                sb.append("BGM 推荐：").append(context.getPlanResult().getBgmRecommendation()).append("\n\n");
            }
        }

        // 热门分析结果
        if (context.getTrendResult() != null && context.getTrendResult().getScript() != null) {
            sb.append("=== 参考文案 ===\n");
            sb.append(context.getTrendResult().getScript().getFullText()).append("\n\n");
        }

        // 用户输入
        if (context.getUserInput() != null) {
            sb.append("=== 用户要求 ===\n");
            sb.append(context.getUserInput()).append("\n");
        }

        sb.append("\n请基于以上信息生成完整的视频脚本。");

        return sb.toString();
    }
}
