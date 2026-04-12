package cn.shuang.module.ai.framework.ai.core.model.xiaoniao.api;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.shuang.module.ai.config.XiaoNiaoProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volcengine.service.BaseServiceImpl;
import com.volcengine.service.visual.IVisualService;
import com.volcengine.service.visual.impl.VisualServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 小云雀 AI 视频生成 API 实现。
 */
@Slf4j
public class XiaoNiaoApiImpl implements XiaoNiaoApi {

    private final XiaoNiaoProperties properties;
    private final ObjectMapper objectMapper;

    public XiaoNiaoApiImpl(XiaoNiaoProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public VideoTaskResponse submitTask(VideoTaskRequest request) {
        log.info("[XiaoNiaoApi] 提交视频生成任务 - subject: {}, duration: {}, styleType: {}",
                request.subject(), request.duration(), request.styleType());
        try {
            if (useOpenApiMode()) {
                return submitTaskByOpenApi(request);
            }
            return submitTaskByBearer(request);
        } catch (Exception e) {
            log.error("[XiaoNiaoApi] 视频生成API调用失败", e);
            return new VideoTaskResponse(-1, "API调用失败：" + e.getMessage(), null);
        }
    }

    @Override
    public TaskStatusResponse queryTask(String taskId) {
        if (StrUtil.isBlank(taskId)) {
            return new TaskStatusResponse(-1, "任务ID不能为空", null);
        }

        try {
            if (useOpenApiMode()) {
                return queryTaskByOpenApi(taskId);
            }
            return queryTaskByBearer(taskId);
        } catch (Exception e) {
            log.error("[XiaoNiaoApi] 任务状态查询API调用失败", e);
            return new TaskStatusResponse(-1, "API调用失败：" + e.getMessage(), null);
        }
    }

    @Override
    public TaskStatusResponse waitForTaskComplete(String taskId, long maxWaitTime, long pollInterval) {
        long startTime = System.currentTimeMillis();
        TaskStatusResponse lastResponse = null;

        while (System.currentTimeMillis() - startTime < maxWaitTime) {
            lastResponse = queryTask(taskId);
            if (lastResponse == null || lastResponse.data() == null) {
                sleepQuietly(pollInterval);
                continue;
            }
            String status = StrUtil.blankToDefault(lastResponse.data().status(), "");
            if ("Succeeded".equalsIgnoreCase(status)
                    || "SUCCESS".equalsIgnoreCase(status)
                    || "Failed".equalsIgnoreCase(status)
                    || "FAIL".equalsIgnoreCase(status)) {
                return lastResponse;
            }
            sleepQuietly(pollInterval);
        }

        log.warn("[XiaoNiaoApi] 任务等待超时 - taskId: {}", taskId);
        return lastResponse;
    }

    private boolean useOpenApiMode() {
        return StrUtil.isNotBlank(properties.getAccessKeyId())
                && StrUtil.isNotBlank(properties.getSecretAccessKey());
    }

    private VideoTaskResponse submitTaskByBearer(VideoTaskRequest request) throws Exception {
        String url = properties.getBaseUrl() + "/videos/submit";
        Map<String, Object> requestBody = buildCompatSubmitBody(request);
        String responseBody = HttpRequest.post(url)
                .header("Authorization", "Bearer " + properties.getApiKey())
                .header("Content-Type", "application/json")
                .header("X-App-Id", properties.getAppId())
                .body(objectMapper.writeValueAsString(requestBody))
                .timeout(60000)
                .execute()
                .body();
        return parseSubmitResponse(objectMapper.readTree(responseBody));
    }

    private TaskStatusResponse queryTaskByBearer(String taskId) throws Exception {
        String url = properties.getBaseUrl() + "/videos/query";
        String responseBody = HttpRequest.post(url)
                .header("Authorization", "Bearer " + properties.getApiKey())
                .header("Content-Type", "application/json")
                .header("X-App-Id", properties.getAppId())
                .body("{\"task_id\":\"" + taskId + "\"}")
                .timeout(30000)
                .execute()
                .body();
        return parseTaskStatusResponse(objectMapper.readTree(responseBody));
    }

    private VideoTaskResponse submitTaskByOpenApi(VideoTaskRequest request) throws Exception {
        JsonNode response = invokeOpenApi(properties.getSubmitAction(), buildCompatSubmitBody(request));
        return parseSubmitResponse(response);
    }

    private TaskStatusResponse queryTaskByOpenApi(String taskId) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("task_id", taskId);
        JsonNode response = invokeOpenApi(properties.getQueryAction(), body);
        return parseTaskStatusResponse(response);
    }

    private JsonNode invokeOpenApi(String action, Map<String, Object> body) throws Exception {
        IVisualService visualService = VisualServiceImpl.getInstance(properties.getRegion());
        BaseServiceImpl baseService = (BaseServiceImpl) visualService;
        baseService.setAccessKey(properties.getAccessKeyId());
        baseService.setSecretKey(properties.getSecretAccessKey());
        baseService.setRegion(properties.getRegion());
        baseService.setHost(properties.getEndpoint());
        baseService.setScheme("https");

        Object raw;
        if (StrUtil.equals(action, properties.getQueryAction())) {
            raw = visualService.cvSync2AsyncGetResult(body);
        } else {
            raw = visualService.cvSync2AsyncSubmitTask(body);
        }
        return objectMapper.valueToTree(raw);
    }

    private Map<String, Object> buildCompatSubmitBody(VideoTaskRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        putIfNotBlank(body, "req_key", properties.getReqKey());
        putIfNotBlank(body, "prompt", request.subject());
        putIfNotBlank(body, "video_prompt", request.subject());
        putIfNotBlank(body, "model", properties.getModel());
        putIfNotBlank(body, "aspect_ratio", StrUtil.blankToDefault(request.ratio(), properties.getDefaultRatio()));
        putIfNotBlank(body, "ratio", StrUtil.blankToDefault(request.ratio(), properties.getDefaultRatio()));
        if (request.duration() != null) {
            body.put("duration", request.duration());
        }
        putIfNotBlank(body, "style_type", StrUtil.blankToDefault(request.styleType(), properties.getDefaultStyleType()));
        putIfNotBlank(body, "quality_level", properties.getDefaultQualityLevel());
        if (request.extra() != null && !request.extra().isEmpty()) {
            body.putAll(request.extra());
        }
        return body;
    }

    private VideoTaskResponse parseSubmitResponse(JsonNode root) {
        String errorMessage = extractErrorMessage(root);
        if (StrUtil.isNotBlank(errorMessage)) {
            return new VideoTaskResponse(-1, errorMessage, null);
        }
        String taskId = firstText(root,
                "/data/task_id", "/data/taskId",
                "/Result/task_id", "/Result/taskId",
                "/result/task_id", "/result/taskId");
        Integer estimate = firstInt(root,
                "/data/estimated_time", "/data/estimatedTime",
                "/Result/estimated_time", "/Result/estimatedTime");
        if (StrUtil.isBlank(taskId)) {
            return new VideoTaskResponse(-1, "未获取到任务ID", null);
        }
        return new VideoTaskResponse(0, "OK", new VideoTaskResponse.VideoTaskData(taskId, estimate));
    }

    private TaskStatusResponse parseTaskStatusResponse(JsonNode root) {
        String errorMessage = extractErrorMessage(root);
        if (StrUtil.isNotBlank(errorMessage)) {
            return new TaskStatusResponse(-1, errorMessage, null);
        }
        String taskId = firstText(root, "/data/task_id", "/Result/task_id", "/result/task_id");
        String status = normalizeStatus(firstText(root,
                "/data/status", "/Result/status", "/result/status",
                "/data/task_status", "/Result/task_status", "/result/task_status"));
        String videoUrl = firstText(root,
                "/data/video_url", "/data/videoUrl",
                "/Result/video_url", "/Result/videoUrl",
                "/result/video_url", "/result/videoUrl");
        String coverUrl = firstText(root,
                "/data/cover_url", "/data/coverUrl",
                "/Result/cover_url", "/Result/coverUrl",
                "/result/cover_url", "/result/coverUrl");
        String failReason = firstText(root,
                "/data/fail_reason", "/data/failReason",
                "/Result/fail_reason", "/Result/failReason",
                "/result/fail_reason", "/result/failReason");
        Integer progress = firstInt(root,
                "/data/progress", "/Result/progress", "/result/progress");
        return new TaskStatusResponse(0, "OK",
                new TaskStatusResponse.TaskStatusData(taskId, status, videoUrl, coverUrl, failReason,
                        progress, null, null));
    }

    private String extractErrorMessage(JsonNode root) {
        return firstText(root,
                "/ResponseMetadata/Error/Message",
                "/responseMetadata/error/message",
                "/error/message",
                "/message");
    }

    private String normalizeStatus(String status) {
        if (StrUtil.isBlank(status)) {
            return "Pending";
        }
        if ("done".equalsIgnoreCase(status) || "success".equalsIgnoreCase(status)) {
            return "Succeeded";
        }
        if ("failed".equalsIgnoreCase(status) || "fail".equalsIgnoreCase(status)) {
            return "Failed";
        }
        if ("running".equalsIgnoreCase(status) || "processing".equalsIgnoreCase(status)) {
            return "Running";
        }
        return status;
    }

    private String firstText(JsonNode root, String... pointers) {
        for (String pointer : pointers) {
            JsonNode node = root.at(pointer);
            if (!node.isMissingNode() && !node.isNull()) {
                String text = node.asText();
                if (StrUtil.isNotBlank(text)) {
                    return text;
                }
            }
        }
        return null;
    }

    private Integer firstInt(JsonNode root, String... pointers) {
        for (String pointer : pointers) {
            JsonNode node = root.at(pointer);
            if (!node.isMissingNode() && !node.isNull()) {
                return node.asInt();
            }
        }
        return null;
    }

    private void putIfNotBlank(Map<String, Object> target, String key, String value) {
        if (StrUtil.isNotBlank(value)) {
            target.put(key, value);
        }
    }

    private void sleepQuietly(long pollInterval) {
        try {
            Thread.sleep(pollInterval);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
