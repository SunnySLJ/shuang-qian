package cn.shuang.module.ai.service.video;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.shuang.module.ai.config.VideoAnalyzeProperties;
import cn.shuang.module.ai.controller.app.video.vo.AiVideoPromptGenerateRespVO;
import cn.shuang.module.ai.service.prompt.PromptOptimizeService;
import cn.shuang.module.ai.service.prompt.dto.PromptOptimizeResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class VideoPromptGenerateService {

    @Resource
    private VideoAnalyzeProperties videoAnalyzeProperties;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private PromptOptimizeService promptOptimizeService;

    public AiVideoPromptGenerateRespVO generateFromImages(List<String> imageUrls, String userPrompt, String promptLevel) {
        VideoAnalyzeProperties.Provider provider = videoAnalyzeProperties.getDoubao();
        if (provider == null || !provider.isEnabled() || StrUtil.hasBlank(provider.getApiKey(), provider.getBaseUrl(), provider.getModel())) {
            throw new IllegalStateException("豆包图片理解能力未配置");
        }

        String content = callDoubaoVision(provider, imageUrls, userPrompt, promptLevel);
        JsonNode resultNode = parseContent(content);
        String visualSummary = textOf(resultNode, "visual_summary");
        String basePrompt = textOf(resultNode, "base_prompt");
        String optimizedPrompt = textOf(resultNode, "optimized_prompt");
        if (StrUtil.isBlank(basePrompt)) {
            basePrompt = StrUtil.blankToDefault(content, userPrompt);
        }
        if (StrUtil.isBlank(optimizedPrompt)) {
            PromptOptimizeResult optimizeResult = promptOptimizeService.optimizeHotVideoPrompt(
                    basePrompt, null, null, "小云雀");
            optimizedPrompt = StrUtil.blankToDefault(optimizeResult.getFullPromptZh(), basePrompt);
        }
        return new AiVideoPromptGenerateRespVO(visualSummary, basePrompt, optimizedPrompt);
    }

    private String callDoubaoVision(VideoAnalyzeProperties.Provider provider, List<String> imageUrls,
                                    String userPrompt, String promptLevel) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", provider.getModel());
            body.put("temperature", 0.4);
            body.put("messages", buildMessages(imageUrls, userPrompt, promptLevel));

            String baseUrl = provider.getBaseUrl().endsWith("/") ? provider.getBaseUrl().substring(0, provider.getBaseUrl().length() - 1) : provider.getBaseUrl();
            String url = baseUrl + "/chat/completions";
            String responseBody = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + provider.getApiKey())
                    .header("Content-Type", ContentType.JSON.getValue())
                    .body(objectMapper.writeValueAsString(body))
                    .timeout(videoAnalyzeProperties.getTimeoutSeconds() * 1000)
                    .execute()
                    .body();

            JsonNode root = objectMapper.readTree(responseBody);
            String content = extractMessageContent(root.path("choices").path(0).path("message").path("content"));
            if (StrUtil.isBlank(content)) {
                throw new IllegalStateException("豆包未返回有效提示词");
            }
            return content;
        } catch (Exception e) {
            log.error("[VideoPromptGenerate] 豆包多图理解失败", e);
            throw new IllegalStateException("豆包看图生成提示词失败：" + e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> buildMessages(List<String> imageUrls, String userPrompt, String promptLevel) {
        List<Map<String, Object>> messages = new ArrayList<>();

        Map<String, Object> system = new LinkedHashMap<>();
        system.put("role", "system");
        system.put("content", """
                你是一名顶级广告导演、分镜师和 AI 视频提示词工程师。
                你要综合理解多张图片中的主体、场景、风格、镜头关系和商业表达意图。
                目标：输出适合短视频生成模型的电影级中文提示词。
                必须输出 JSON：
                {
                  "visual_summary": "对多张图片的统一视觉理解摘要",
                  "base_prompt": "基于图片理解生成的基础视频提示词",
                  "optimized_prompt": "电影级、镜头语言完整、适合视频生成的成品提示词"
                }
                约束：
                1. 不是简单描述图片，而是提炼可用于视频生成的连续镜头语言
                2. 要写出主体、场景、镜头运动、光线、氛围、节奏、转场和结尾动作
                3. 优先输出适合营销成片、广告片、电影预告级别的高质量中文提示词
                4. 只返回 JSON
                """);
        messages.add(system);

        Map<String, Object> user = new LinkedHashMap<>();
        user.put("role", "user");
        List<Map<String, Object>> content = new ArrayList<>();

        Map<String, Object> textPart = new LinkedHashMap<>();
        textPart.put("type", "text");
        textPart.put("text", "请综合理解这些图片，生成 " + StrUtil.blankToDefault(promptLevel, "cinematic")
                + " 风格的视频提示词。用户补充需求：" + StrUtil.blankToDefault(userPrompt, "无额外补充"));
        content.add(textPart);

        for (String imageUrl : imageUrls) {
            Map<String, Object> imagePart = new LinkedHashMap<>();
            imagePart.put("type", "image_url");
            Map<String, Object> imageUrlNode = new LinkedHashMap<>();
            imageUrlNode.put("url", imageUrl);
            imagePart.put("image_url", imageUrlNode);
            content.add(imagePart);
        }
        user.put("content", content);
        messages.add(user);
        return messages;
    }

    private JsonNode parseContent(String content) {
        try {
            int start = content.indexOf('{');
            int end = content.lastIndexOf('}');
            String json = start >= 0 && end > start ? content.substring(start, end + 1) : content;
            return objectMapper.readTree(json);
        } catch (Exception e) {
            ObjectNodeBuilder builder = new ObjectNodeBuilder();
            builder.put("visual_summary", "");
            builder.put("base_prompt", content);
            builder.put("optimized_prompt", "");
            return builder.build(objectMapper);
        }
    }

    private String textOf(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() ? "" : value.asText("");
    }

    private String extractMessageContent(JsonNode contentNode) {
        if (contentNode == null || contentNode.isMissingNode() || contentNode.isNull()) {
            return "";
        }
        if (contentNode.isTextual()) {
            return contentNode.asText();
        }
        if (contentNode.isArray()) {
            StringBuilder builder = new StringBuilder();
            for (JsonNode node : contentNode) {
                String text = node.path("text").asText("");
                if (StrUtil.isBlank(text)) {
                    text = node.asText("");
                }
                if (StrUtil.isNotBlank(text)) {
                    if (builder.length() > 0) {
                        builder.append('\n');
                    }
                    builder.append(text);
                }
            }
            return builder.toString();
        }
        return contentNode.asText("");
    }

    private static final class ObjectNodeBuilder {

        private final Map<String, String> values = new LinkedHashMap<>();

        public void put(String key, String value) {
            values.put(key, value);
        }

        public JsonNode build(ObjectMapper objectMapper) {
            return objectMapper.valueToTree(values);
        }
    }
}
