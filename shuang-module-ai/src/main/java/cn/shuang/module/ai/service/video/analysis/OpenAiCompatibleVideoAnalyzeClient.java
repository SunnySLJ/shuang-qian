package cn.shuang.module.ai.service.video.analysis;

import cn.hutool.core.util.StrUtil;
import cn.shuang.module.ai.config.VideoAnalyzeProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * 基于 OpenAI 兼容协议的视频拆解客户端。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAiCompatibleVideoAnalyzeClient {

    private static final String DEFAULT_SYSTEM_PROMPT = "你是一名短视频爆款拆解助手，擅长输出结构清晰、可直接复用的中文分析结果。";

    /** 视频转 Base64 的最大文件大小：50MB，防止 OOM */
    private static final long MAX_VIDEO_SIZE_BYTES = 50 * 1024 * 1024L;

    private final ObjectMapper objectMapper;
    private final VideoAnalyzeProperties properties;

    public String analyze(String videoUrl, String analyzeType, VideoAnalyzeProvider provider) {
        ProviderConfig config = getConfig(provider);
        String normalizedBaseUrl = normalizeBaseUrl(config.baseUrl());
        validateVideoUrl(videoUrl);
        Duration timeout = Duration.ofSeconds(properties.getTimeoutSeconds());
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);
        RestClient restClient = RestClient.builder()
                .baseUrl(normalizedBaseUrl)
                .defaultHeader("Authorization", "Bearer " + config.apiKey())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(requestFactory)
                .build();

        List<Map<String, Object>> candidateBodies = buildCandidateBodies(provider, config.model(), videoUrl, analyzeType);
        List<String> errors = new ArrayList<>();
        for (Map<String, Object> body : candidateBodies) {
            try {
                JsonNode response = restClient.post()
                        .uri("/chat/completions")
                        .body(body)
                        .retrieve()
                        .body(JsonNode.class);
                String content = extractContent(response);
                if (StrUtil.isNotBlank(content)) {
                    return content;
                }
                errors.add("响应缺少可解析内容");
            } catch (RestClientResponseException ex) {
                String message = StrUtil.blankToDefault(ex.getResponseBodyAsString(), ex.getMessage());
                errors.add(message);
                log.warn("[VideoAnalyze] {} 调用失败，尝试下一种消息格式: {}", provider.getCode(), message);
            } catch (Exception ex) {
                errors.add(ex.getMessage());
                log.warn("[VideoAnalyze] {} 调用异常，尝试下一种消息格式", provider.getCode(), ex);
            }
        }

        throw new RuntimeException(provider.getCode() + " 视频拆解调用失败: " + String.join(" | ", errors));
    }

    private ProviderConfig getConfig(VideoAnalyzeProvider provider) {
        VideoAnalyzeProperties.Provider rawConfig = switch (provider) {
            case ALIYUN -> properties.getAliyun();
            case DOUBAO -> properties.getDoubao();
            default -> null;
        };
        if (rawConfig == null || !rawConfig.isEnabled()) {
            throw new RuntimeException("视频拆解供应商未启用: " + provider.getCode());
        }
        if (StrUtil.hasBlank(rawConfig.getApiKey(), rawConfig.getBaseUrl(), rawConfig.getModel())) {
            throw new RuntimeException("视频拆解供应商配置不完整: " + provider.getCode());
        }
        return new ProviderConfig(rawConfig.getApiKey(), rawConfig.getBaseUrl(), rawConfig.getModel());
    }

    private List<Map<String, Object>> buildCandidateBodies(VideoAnalyzeProvider provider, String model,
                                                           String videoPayload, String analyzeType) {
        String userPrompt = buildAnalyzePrompt(analyzeType);
        List<Map<String, Object>> candidates = new ArrayList<>();
        List<Map<String, Object>> contentVariants = buildContentVariants(provider, videoPayload, userPrompt);
        for (Map<String, Object> contentWrapper : contentVariants) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", model);
            body.put("temperature", 0.2);
            body.put("max_tokens", 2200);
            body.put("messages", List.of(
                    Map.of("role", "system", "content", DEFAULT_SYSTEM_PROMPT),
                    Map.of("role", "user", "content", List.of(contentWrapper, textItem(userPrompt)))
            ));
            candidates.add(body);
        }
        return candidates;
    }

    private List<Map<String, Object>> buildContentVariants(VideoAnalyzeProvider provider, String videoPayload, String userPrompt) {
        List<Map<String, Object>> variants = new ArrayList<>();
        variants.add(Map.of("type", "video_url", "video_url", Map.of("url", videoPayload)));
        String dataUrl = null;
        if (provider == VideoAnalyzeProvider.DOUBAO || provider == VideoAnalyzeProvider.ALIYUN) {
            dataUrl = toVideoDataUrl(videoPayload);
            variants.add(Map.of("type", "video_url", "video_url", Map.of("url", dataUrl)));
        }
        if (provider == VideoAnalyzeProvider.ALIYUN) {
            variants.add(Map.of("type", "video", "video", List.of(videoPayload)));
            if (dataUrl != null) {
                variants.add(Map.of("type", "video", "video", List.of(dataUrl)));
            }
        }
        return variants;
    }

    private Map<String, Object> textItem(String prompt) {
        return Map.of("type", "text", "text", prompt);
    }

    private String buildAnalyzePrompt(String analyzeType) {
        return switch (StrUtil.blankToDefault(analyzeType, "ELEMENTS").toUpperCase()) {
            case "SCRIPT" -> """
                    请根据输入视频，尽可能提取其中的口播文案、字幕文案和叙事顺序。
                    若音频或字幕无法完全识别，请明确标注“不确定”而不是编造。
                    输出格式：
                    1. 视频主题
                    2. 画面流程
                    3. 可识别文案/字幕
                    4. 关键钩子句
                    5. 不确定信息
                    """;
            case "PROMPT" -> """
                    请把这个视频拆成可复刻的 AI 创作提示词。
                    输出格式：
                    1. 核心题材
                    2. 主体设定
                    3. 场景与环境
                    4. 运镜与镜头语言
                    5. 光线、色调、氛围
                    6. 文案/字幕风格
                    7. 可直接复制的图像提示词
                    8. 可直接复制的视频提示词
                    """;
            default -> """
                    请从短视频运营复盘的视角，对视频进行爆款拆解。
                    输出必须结构化，且每一项都要尽量具体。
                    输出格式：
                    1. 视频主题与目标受众
                    2. 开头3秒钩子
                    3. 画面场景与人物
                    4. 镜头/运镜/节奏
                    5. 文案/字幕/口播重点
                    6. 情绪与氛围营造
                    7. 爆点元素总结
                    8. 可复刻建议
                    """;
        };
    }

    private String extractContent(JsonNode response) {
        if (response == null) {
            return null;
        }
        JsonNode choices = response.path("choices");
        if (!choices.isArray() || choices.isEmpty()) {
            return null;
        }
        JsonNode contentNode = choices.get(0).path("message").path("content");
        if (contentNode.isTextual()) {
            return contentNode.asText();
        }
        if (contentNode.isArray()) {
            StringBuilder builder = new StringBuilder();
            for (JsonNode item : contentNode) {
                JsonNode textNode = item.path("text");
                if (textNode.isTextual()) {
                    if (!builder.isEmpty()) {
                        builder.append('\n');
                    }
                    builder.append(textNode.asText());
                }
            }
            return builder.toString();
        }
        return null;
    }

    private String normalizeBaseUrl(String baseUrl) {
        String trimmed = StrUtil.removeSuffix(StrUtil.trim(baseUrl), "/");
        if (trimmed.endsWith("/chat/completions")) {
            return StrUtil.removeSuffix(trimmed, "/chat/completions");
        }
        return trimmed;
    }

    private void validateVideoUrl(String videoUrl) {
        URI uri = URI.create(videoUrl);
        String scheme = StrUtil.blankToDefault(uri.getScheme(), "").toLowerCase();
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            throw new RuntimeException("视频链接必须是 http/https 公网地址");
        }
        String host = StrUtil.blankToDefault(uri.getHost(), "");
        if (host.isEmpty() || isPrivateHost(host)) {
            throw new RuntimeException("当前供应商仅支持公网可访问的视频链接，本地上传文件需先接入 OSS/CDN 等公网存储");
        }
    }

    private boolean isPrivateHost(String host) {
        if ("localhost".equalsIgnoreCase(host)) {
            return true;
        }
        try {
            InetAddress address = InetAddress.getByName(host);
            return address.isAnyLocalAddress()
                    || address.isLoopbackAddress()
                    || address.isSiteLocalAddress();
        } catch (Exception ex) {
            return false;
        }
    }

    private String toVideoDataUrl(String videoUrl) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(videoUrl).toURL().openConnection();
            connection.setConnectTimeout((int) Duration.ofSeconds(properties.getTimeoutSeconds()).toMillis());
            connection.setReadTimeout((int) Duration.ofSeconds(properties.getTimeoutSeconds()).toMillis());
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.connect();

            // 检查 Content-Length，防止大文件导致 OOM
            String contentLengthHeader = connection.getHeaderField("Content-Length");
            if (contentLengthHeader != null) {
                long contentLength = Long.parseLong(contentLengthHeader);
                if (contentLength > MAX_VIDEO_SIZE_BYTES) {
                    throw new RuntimeException("视频文件过大（" + contentLength / 1024 / 1024 + "MB），超过 50MB 限制，请使用更小的视频或压缩后上传");
                }
            }

            String contentType = StrUtil.blankToDefault(connection.getContentType(), inferMimeType(videoUrl));
            try (InputStream raw = connection.getInputStream();
                 InputStream input = isGzip(connection) ? new GZIPInputStream(raw) : raw;
                 ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                // 边读边检查大小，超过限制则中断
                byte[] buffer = new byte[8192];
                long totalRead = 0;
                int read;
                while ((read = input.read(buffer)) != -1) {
                    totalRead += read;
                    if (totalRead > MAX_VIDEO_SIZE_BYTES) {
                        throw new RuntimeException("视频文件超过 50MB 限制，无法处理");
                    }
                    output.write(buffer, 0, read);
                }
                return "data:" + contentType + ";base64," +
                        java.util.Base64.getEncoder().encodeToString(output.toByteArray());
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("视频 Content-Length 格式错误: " + e.getMessage(), e);
        } catch (Exception ex) {
            throw new RuntimeException("下载视频失败，无法提交给多模态模型: " + ex.getMessage(), ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private boolean isGzip(HttpURLConnection connection) {
        return StrUtil.containsIgnoreCase(connection.getContentEncoding(), "gzip");
    }

    private String inferMimeType(String videoUrl) {
        String lower = StrUtil.blankToDefault(videoUrl, "").toLowerCase();
        if (lower.contains(".mov")) {
            return "video/quicktime";
        }
        if (lower.contains(".webm")) {
            return "video/webm";
        }
        if (lower.contains(".avi")) {
            return "video/x-msvideo";
        }
        return "video/mp4";
    }

    private record ProviderConfig(String apiKey, String baseUrl, String model) {
    }

}
