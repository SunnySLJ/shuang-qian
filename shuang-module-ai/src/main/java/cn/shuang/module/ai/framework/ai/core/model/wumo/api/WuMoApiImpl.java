package cn.shuang.module.ai.framework.ai.core.model.wumo.api;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 舞墨 AI API 实现类
 *
 * @author shuang-pro
 */
@Slf4j
public class WuMoApiImpl implements WuMoApi {

    /**
     * API Key
     */
    private final String apiKey;

    /**
     * API 基础 URL
     */
    private final String baseUrl;

    public WuMoApiImpl(String apiKey) {
        this(apiKey, WuMoApiConstants.BASE_URL);
    }

    public WuMoApiImpl(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    @Override
    public ImageGenerateResponse generateImage(ImageGenerateRequest request) {
        log.info("[WuMoApi] 开始调用图片生成 API - prompt: {}, model: {}",
                request.prompt(), request.model());

        try {
            String url = baseUrl + WuMoApiConstants.IMAGE_GENERATE_ENDPOINT;
            String jsonBody = JSONUtil.toJsonStr(request);

            String responseBody = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(jsonBody)
                    .timeout(60000) // 60 秒超时
                    .execute()
                    .body();

            ImageGenerateResponse response = JSONUtil.toBean(responseBody, ImageGenerateResponse.class);
            log.info("[WuMoApi] 图片生成 API 响应 - code: {}, message: {}, taskId: {}",
                    response.code(), response.message(),
                    response.data() != null ? response.data().taskId() : "N/A");

            return response;
        } catch (Exception e) {
            log.error("[WuMoApi] 图片生成 API 调用失败", e);
            return new ImageGenerateResponse(
                    -1,
                    "API 调用失败：" + e.getMessage(),
                    null
            );
        }
    }

    @Override
    public VideoGenerateResponse generateVideo(VideoGenerateRequest request) {
        log.info("[WuMoApi] 开始调用视频生成 API - prompt: {}, model: {}",
                request.prompt(), request.model());

        try {
            String url = baseUrl + WuMoApiConstants.VIDEO_GENERATE_ENDPOINT;
            String jsonBody = JSONUtil.toJsonStr(request);

            String responseBody = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(jsonBody)
                    .timeout(120000) // 120 秒超时
                    .execute()
                    .body();

            VideoGenerateResponse response = JSONUtil.toBean(responseBody, VideoGenerateResponse.class);
            log.info("[WuMoApi] 视频生成 API 响应 - code: {}, message: {}, taskId: {}",
                    response.code(), response.message(),
                    response.data() != null ? response.data().taskId() : "N/A");

            return response;
        } catch (Exception e) {
            log.error("[WuMoApi] 视频生成 API 调用失败", e);
            return new VideoGenerateResponse(
                    -1,
                    "API 调用失败：" + e.getMessage(),
                    null
            );
        }
    }

    @Override
    public VideoGenerateResponse imageToVideo(String referenceImage, String prompt, String model) {
        log.info("[WuMoApi] 开始调用图生视频 API - model: {}", model);

        VideoGenerateRequest request = VideoGenerateRequest.builder()
                .referenceImage(referenceImage)
                .prompt(prompt)
                .model(model)
                .duration(5)
                .build();

        return generateVideo(request);
    }

    @Override
    public TaskQueryResponse queryTask(String taskId) {
        if (StrUtil.isBlank(taskId)) {
            return new TaskQueryResponse(-1, "任务 ID 不能为空", null);
        }

        try {
            String url = baseUrl + WuMoApiConstants.TASK_QUERY_ENDPOINT;
            String jsonBody = JSONUtil.toJsonStr(new TaskQueryRequest(taskId));

            String responseBody = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(jsonBody)
                    .timeout(30000)
                    .execute()
                    .body();

            TaskQueryResponse response = JSONUtil.toBean(responseBody, TaskQueryResponse.class);
            log.info("[WuMoApi] 任务查询响应 - taskId: {}, status: {}, progress: {}",
                    taskId,
                    response.data() != null ? response.data().status() : "N/A",
                    response.data() != null ? response.data().progress() : "N/A");

            return response;
        } catch (Exception e) {
            log.error("[WuMoApi] 任务查询 API 调用失败", e);
            return new TaskQueryResponse(
                    -1,
                    "API 调用失败：" + e.getMessage(),
                    null
            );
        }
    }

    /**
     * 轮询等待任务完成
     *
     * @param taskId       任务 ID
     * @param maxWaitTime  最大等待时间（毫秒）
     * @param pollInterval 轮询间隔（毫秒）
     * @return 最终任务状态
     */
    public TaskQueryResponse waitForTaskComplete(String taskId, long maxWaitTime, long pollInterval) {
        long startTime = System.currentTimeMillis();
        TaskQueryResponse lastResponse = null;

        while (System.currentTimeMillis() - startTime < maxWaitTime) {
            lastResponse = queryTask(taskId);

            if (lastResponse == null || lastResponse.data() == null) {
                log.warn("[WuMoApi] 任务查询返回空 - taskId: {}", taskId);
                try {
                    Thread.sleep(pollInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }

            String status = lastResponse.data().status();
            if ("SUCCESS".equals(status) || "FAILED".equals(status)) {
                log.info("[WuMoApi] 任务完成 - taskId: {}, status: {}, resultUrl: {}",
                        taskId, status, lastResponse.data().resultUrl());
                return lastResponse;
            }

            log.info("[WuMoApi] 任务处理中 - taskId: {}, status: {}, progress: {}%",
                    taskId, status, lastResponse.data().progress());

            try {
                Thread.sleep(pollInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        log.warn("[WuMoApi] 任务等待超时 - taskId: {}", taskId);
        return lastResponse;
    }

}
