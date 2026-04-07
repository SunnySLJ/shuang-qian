package cn.shuang.module.ai.framework.content.agent;

import cn.shuang.module.ai.framework.content.context.ContentContext;
import cn.shuang.module.ai.framework.content.context.TrendAnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 热门内容分析 Agent
 * <p>
 * 分析用户上传的参考视频，提取爆款元素：
 * - 文案分析：提取完整台词，识别金句和情绪高潮点
 * - 视觉元素：场景、人物、道具、色彩、灯光
 * - 运镜方式：推、拉、摇、移、跟等
 * - 节奏分析：平均镜头时长、剪辑频率、节奏变化
 * - BGM 分析：音乐风格、情绪、音效使用
 */
@Slf4j
@Component
public class TrendAnalyzerAgent extends BaseContentAgent {

    public TrendAnalyzerAgent(ChatClient.Builder chatClientBuilder) {
        super("TrendAnalyzer", chatClientBuilder);
    }

    private static final String SYSTEM_PROMPT = """
        你是一名短视频内容分析专家，擅长拆解爆款视频的成功要素。

        请分析用户提供的视频内容，从以下维度输出结构化分析结果：

        1. **文案分析**
           - 提取完整台词/字幕
           - 识别金句和爆点（能引发用户共鸣或传播的句子）
           - 标记情绪高潮点及其时间位置

        2. **视觉元素分析**
           - 场景描述：室内/室外、具体环境
           - 人物：数量、特征、服装
           - 道具：关键物品及其作用
           - 色彩：主色调、色彩搭配
           - 灯光：灯光风格、光源方向

        3. **运镜方式识别**
           - 推镜头、拉镜头、摇镜头、移镜头、跟镜头
           - 景别变化：远景→全景→中景→近景→特写
           - 镜头时长和切换节奏

        4. **节奏分析**
           - 平均镜头时长
           - 剪辑频率（低/中/高）
           - 节奏变化：前慢后快/前快后慢/均匀
           - 高潮点位置

        5. **BGM 与音效分析**
           - BGM 风格和情绪
           - 音效使用点及其作用
           - 语音语速

        6. **爆款元素提炼**
           - 前 3 秒钩子是什么
           - 情绪触发点
           - 传播点设计

        请以 JSON 格式输出分析结果，确保数据可直接用于后续的内容生成。
        """;

    @Override
    public AgentStepResult execute(ContentContext context) {
        try {
            log.info("[TrendAnalyzer] 开始分析视频：{}", context.getReferenceVideoUrl());

            String userPrompt = buildUserPrompt(context);

            TrendAnalysisResult result = chatJson(SYSTEM_PROMPT, userPrompt, TrendAnalysisResult.class);

            // 如果解析失败，返回空结果
            if (result == null) {
                return result(TrendAnalysisResult.builder()
                        .script(TrendAnalysisResult.ScriptAnalysis.builder()
                                .fullText("未能解析视频内容")
                                .build())
                        .build());
            }

            log.info("[TrendAnalyzer] 分析完成，场景数：{}",
                    result.getVisuals() != null ? result.getVisuals().getScenes().size() : 0);

            return result(result);

        } catch (Exception e) {
            log.error("[TrendAnalyzer] 分析失败", e);
            return fail("视频分析失败：" + e.getMessage());
        }
    }

    private String buildUserPrompt(ContentContext context) {
        StringBuilder sb = new StringBuilder();

        if (context.getReferenceVideoUrl() != null) {
            sb.append("参考视频 URL: ").append(context.getReferenceVideoUrl()).append("\n\n");
        }

        if (context.getCategory() != null) {
            sb.append("行业分类：").append(context.getCategory()).append("\n\n");
        }

        if (context.getStyle() != null) {
            sb.append("内容风格：").append(context.getStyle()).append("\n\n");
        }

        if (context.getUserInput() != null && !context.getUserInput().isEmpty()) {
            sb.append("用户补充说明：").append(context.getUserInput()).append("\n\n");
        }

        sb.append("\n请基于以上信息进行详细分析。如果视频 URL 为空，请基于用户描述进行分析。");

        return sb.toString();
    }
}
