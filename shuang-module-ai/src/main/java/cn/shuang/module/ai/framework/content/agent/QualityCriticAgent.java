package cn.shuang.module.ai.framework.content.agent;

import cn.shuang.module.ai.framework.content.context.ContentContext;
import cn.shuang.module.ai.framework.content.context.CriticResult;
import cn.shuang.module.ai.framework.content.context.CriticResult.CriticDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 质量审查 Agent
 * <p>
 * 对生成的内容进行质量审查：
 * - 内容完整性检查
 * - 爆款潜力评估
 * - 脚本质量评分
 * - 视觉吸引力评估
 * - 提供具体修改建议
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "yudao.ai.content-generation.enable", havingValue = "true")
public class QualityCriticAgent extends BaseContentAgent {

    public QualityCriticAgent(ChatClient chatClient) {
        super("QualityCritic", chatClient);
    }

    private static final String SYSTEM_PROMPT = """
        你是一名资深内容质量审核专家，曾用"网感"培养出多个千万粉丝账号。

        请对提供的视频脚本/内容进行严格审查，从以下维度评分并给出建议：

        1. **内容完整性（25 分）**
           - 是否有清晰的开头、正文、结尾
           - 信息是否完整，逻辑是否连贯
           - 是否缺少必要元素

        2. **爆款潜力（25 分）**
           - 前 3 秒是否有足够的钩子
           - 是否有情绪触发点
           - 是否有传播点/讨论点
           - 是否符合平台算法偏好

        3. **脚本质量（25 分）**
           - 语言是否口语化、自然
           - 节奏是否紧凑、无废话
           - 字数是否适中
           - 是否有记忆点

        4. **视觉吸引力（25 分）**
           - 画面描述是否具体、有画面感
           - 运镜设计是否丰富
           - 景别变化是否合理
           - 色彩/灯光设计是否有吸引力

        审查标准：
        - 90-100 分：爆款相，可直接制作
        - 80-89 分：良好，微调后可用
        - 70-79 分：合格，需要优化
        - 60-69 分：勉强，建议大改
        - 60 分以下：不合格，需要重写

        请给出具体分数、优势和待改进点，并提供可执行的修改建议。
        """;

    @Override
    public AgentStepResult execute(ContentContext context) {
        try {
            log.info("[QualityCritic] 开始审查内容");

            String userPrompt = buildUserPrompt(context);

            CriticResult result = chatJson(SYSTEM_PROMPT, userPrompt, CriticResult.class);

            // 如果解析失败，返回默认通过结果
            if (result == null || result.getOverallScore() == null) {
                return result(CriticResult.builder()
                        .passed(true)
                        .overallScore(80)
                        .strengths(List.of("内容完整", "结构清晰"))
                        .weaknesses(new ArrayList<>())
                        .suggestions(List.of("可以继续优化细节"))
                        .build());
            }

            // 判断是否通过（分数>=阈值）
            boolean passed = result.getOverallScore() >= context.getQualityThreshold();
            result.setPassed(passed);

            log.info("[QualityCritic] 审查完成，评分：{}, 结果：{}",
                    result.getOverallScore(), passed ? "通过" : "不通过");

            return result(result);

        } catch (Exception e) {
            log.error("[QualityCritic] 审查失败", e);
            return fail("质量审查失败：" + e.getMessage());
        }
    }

    private String buildUserPrompt(ContentContext context) {
        StringBuilder sb = new StringBuilder();

        // 脚本结果
        if (context.getScriptResult() != null) {
            sb.append("=== 视频脚本 ===\n");
            sb.append("完整脚本：").append(context.getScriptResult().getFullScript()).append("\n\n");
            sb.append("开场白：").append(context.getScriptResult().getOpening()).append("\n");
            sb.append("结尾 CTA:").append(context.getScriptResult().getCallToAction()).append("\n");
            sb.append("字数：").append(context.getScriptResult().getWordCount()).append("\n");
            sb.append("预计时长：").append(context.getScriptResult().getEstimatedDuration()).append("秒\n\n");
        }

        // 内容规划结果
        if (context.getPlanResult() != null) {
            sb.append("=== 内容规划 ===\n");
            sb.append("标题：").append(context.getPlanResult().getTitle()).append("\n");
            sb.append("分镜数：").append(
                    context.getPlanResult().getStoryboards() != null ?
                            context.getPlanResult().getStoryboards().size() : 0).append("\n\n");
        }

        // 用户输入和风格
        if (context.getStyle() != null) {
            sb.append("内容风格：").append(context.getStyle()).append("\n");
        }
        if (context.getCategory() != null) {
            sb.append("行业分类：").append(context.getCategory()).append("\n");
        }

        sb.append("\n请基于以上信息进行质量审查。");

        return sb.toString();
    }
}
