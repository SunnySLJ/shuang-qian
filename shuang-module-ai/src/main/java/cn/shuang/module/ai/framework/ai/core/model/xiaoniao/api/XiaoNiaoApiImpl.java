package cn.shuang.module.ai.framework.ai.core.model.xiaoniao.api;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shuang.module.ai.config.XiaoNiaoProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 小云雀 AI 视频生成 API 实现
 * <p>
 * 基于火山引擎即梦AI的小云雀营销成片Agent
 * 文档：https://www.volcengine.com/docs/85621/2283654
 *
 * @author shuang-pro
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
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("subject", request.subject());
            requestBody.put("duration", request.duration() != null ? request.duration() : 15);
            requestBody.put("ratio", request.ratio() != null ? request.ratio() : "16:9");
            requestBody.put("style_type", request.styleType() != null ? request.styleType() : "vlog_travel");

            if (StrUtil.isNotBlank(request.script())) {
                requestBody.put("script", request.script());
            }
            if (StrUtil.isNotBlank(request.bgmUrl())) {
                requestBody.put("bgm_url", request.bgmUrl());
            }
            if (StrUtil.isNotBlank(request.watermarkText())) {
                requestBody.put("watermark_text", request.watermarkText());
            }
            if (StrUtil.isNotBlank(request.callbackUrl())) {
                requestBody.put("callback_url", request.callbackUrl());
            }
            if (request.extra() != null) {
                requestBody.putAll(request.extra());
            }

            String url = properties.getBaseUrl() + "/videos/submit";
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            String responseBody = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .header("Content-Type", "application/json")
                    .header("X-App-Id", properties.getAppId())
                    .body(jsonBody)
                    .timeout(60000)
                    .execute()
                    .body();

            VideoTaskResponse response = objectMapper.readValue(responseBody, VideoTaskResponse.class);
            log.info("[XiaoNiaoApi] 任务提交响应 - code: {}, message: {}, taskId: {}",
                    response.code(), response.message(),
                    response.data() != null ? response.data().taskId() : "N/A");

            return response;
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
            String url = properties.getBaseUrl() + "/videos/query";
            String requestBody = "{\"task_id\":\"" + taskId + "\"}";

            String responseBody = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .header("Content-Type", "application/json")
                    .header("X-App-Id", properties.getAppId())
                    .body(requestBody)
                    .timeout(30000)
                    .execute()
                    .body();

            TaskStatusResponse response = objectMapper.readValue(responseBody, TaskStatusResponse.class);
            log.info("[XiaoNiaoApi] 任务状态查询 - taskId: {}, status: {}, progress: {}%",
                    taskId,
                    response.data() != null ? response.data().status() : "N/A",
                    response.data() != null ? response.data().progress() : "N/A");

            return response;
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
                log.warn("[XiaoNiaoApi] 任务查询返回空 - taskId: {}", taskId);
                try {
                    Thread.sleep(pollInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }

            String status = lastResponse.data().status();
            if ("Succeeded".equals(status) || "Failed".equals(status)) {
                log.info("[XiaoNiaoApi] 任务完成 - taskId: {}, status: {}, videoUrl: {}",
                        taskId, status, lastResponse.data().videoUrl());
                return lastResponse;
            }

            log.info("[XiaoNiaoApi] 任务处理中 - taskId: {}, status: {}, progress: {}%",
                    taskId, status, lastResponse.data().progress());

            try {
                Thread.sleep(pollInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        log.warn("[XiaoNiaoApi] 任务等待超时 - taskId: {}", taskId);
        return lastResponse;
    }
}
