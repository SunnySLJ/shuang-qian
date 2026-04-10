package cn.shuang.module.ai.service.video.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.module.ai.controller.admin.image.vo.AiImagePageReqVO;
import cn.shuang.module.ai.dal.dataobject.image.AiImageDO;
import cn.shuang.module.ai.dal.dataobject.model.AiApiKeyDO;
import cn.shuang.module.ai.dal.dataobject.model.AiModelDO;
import cn.shuang.module.ai.dal.mysql.image.AiImageMapper;
import cn.shuang.module.ai.enums.image.AiImageStatusEnum;
import cn.shuang.module.ai.enums.video.AiVideoBizTypeEnum;
import cn.shuang.module.ai.framework.ai.core.model.wumo.api.WuMoApi;
import cn.shuang.module.ai.framework.ai.core.model.wumo.api.WuMoApiImpl;
import cn.shuang.module.ai.framework.ai.core.model.wumo.api.WuMoApiConstants;
import cn.shuang.module.ai.framework.ai.core.model.xiaoniao.api.XiaoNiaoApi;
import cn.shuang.module.ai.framework.ai.core.model.xiaoniao.api.XiaoNiaoApiImpl;
import cn.shuang.module.ai.service.model.AiApiKeyService;
import cn.shuang.module.ai.service.model.AiModelService;
import cn.shuang.module.ai.service.video.AiVideoService;
import cn.shuang.module.infra.api.file.FileApi;
import cn.shuang.framework.common.biz.ShuangProProperties;
import cn.shuang.module.pay.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * AI 视频生成 Service 实现类
 *
 * @author shuang-pro
 */
@Service
@Slf4j
public class AiVideoServiceImpl implements AiVideoService {

    @Resource
    private AiModelService modelService;

    @Resource
    private AiApiKeyService apiKeyService;

    @Resource
    private AiImageMapper imageMapper;

    @Resource
    private FileApi fileApi;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private WalletService walletService;

    @Resource
    private ShuangProProperties shuangProProperties;

    // 小云雀配置
    @Value("${yudao.ai.xiaoniao.api-key:}")
    private String xiaoniaoApiKey;

    @Value("${yudao.ai.xiaoniao.app-id:}")
    private String xiaoniaoAppId;

    @Value("${yudao.ai.xiaoniao.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String xiaoniaoBaseUrl;

    @Value("${yudao.ai.xiaoniao.model:video-01-20250515}")
    private String xiaoniaoModel;

    private static final long DEFAULT_MAX_WAIT_TIME = 300000;
    private static final long DEFAULT_POLL_INTERVAL = 5000;

    private XiaoNiaoApi xiaoNiaoApi;

    @PostConstruct
    public void init() {
        if (StrUtil.isNotBlank(xiaoniaoApiKey)) {
            cn.shuang.module.ai.config.XiaoNiaoProperties props = new cn.shuang.module.ai.config.XiaoNiaoProperties();
            props.setApiKey(xiaoniaoApiKey);
            props.setAppId(xiaoniaoAppId);
            props.setBaseUrl(xiaoniaoBaseUrl);
            props.setModel(xiaoniaoModel);
            this.xiaoNiaoApi = new XiaoNiaoApiImpl(props, objectMapper);
            log.info("[AiVideoService] 小云雀 API 初始化成功");
        } else {
            log.warn("[AiVideoService] 未配置小云雀 API Key，将使用舞墨 API");
        }
    }

    // ========== 积分配置 ==========

    /**
     * 获取视频生成消耗积分（根据时长）
     */
    private Integer getVideoCost(Integer duration) {
        if (duration == null || duration <= 5) {
            return shuangProProperties.getPoints().getVideo5sCost();
        } else if (duration <= 15) {
            return shuangProProperties.getPoints().getVideo15sCost();
        } else {
            return shuangProProperties.getPoints().getVideo30sCost();
        }
    }

    private Integer getVideoAnalyzeCost() {
        return shuangProProperties.getPoints().getVideoAnalyzeCost();
    }

    // ========== 视频生成方法 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long textToVideo(Long userId, String prompt, Long modelId, Integer duration) {
        log.info("[AiVideoService] 开始生成视频 - userId: {}, prompt: {}, modelId: {}", userId, prompt, modelId);

        // 1. 验证模型
        AiModelDO model = modelService.validateModel(modelId);

        // 2. 获取积分消耗
        int cost = getVideoCost(duration);

        // 3. 扣减积分
        walletService.deductPoints(userId, cost, "AI 视频生成");

        // 4. 创建记录
        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(model.getId());
        image.setPrompt(prompt);
        image.setGenerationType(AiVideoBizTypeEnum.TEXT_TO_VIDEO.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        imageMapper.insert(image);

        // 5. 异步执行
        executeVideoGeneration(image, prompt, null, model, duration);

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long imageToVideo(Long userId, String imageUrl, String prompt, Long modelId, Integer duration) {
        log.info("[AiVideoService] 开始图生视频 - userId: {}, imageUrl: {}, prompt: {}", userId, imageUrl, prompt);

        AiModelDO model = modelService.validateModel(modelId);
        int cost = getVideoCost(duration);
        walletService.deductPoints(userId, cost, "图生视频");

        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(model.getId());
        image.setPrompt(prompt);
        image.setGenerationType(AiVideoBizTypeEnum.IMAGE_TO_VIDEO.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        imageMapper.insert(image);

        executeVideoGeneration(image, prompt, imageUrl, model, duration);

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long analyzeElements(Long userId, String videoUrl, Long modelId) {
        log.info("[AiVideoService] 开始视频解析 - 分析元素 - userId: {}, videoUrl: {}", userId, videoUrl);

        AiModelDO model = modelService.validateModel(modelId);
        int cost = getVideoAnalyzeCost();
        walletService.deductPoints(userId, cost, "视频拆解 - 分析元素");

        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(model.getId());
        image.setPrompt("[视频拆解 - 分析元素]" + videoUrl);
        image.setInputVideoUrl(videoUrl);
        image.setGenerationType(AiVideoBizTypeEnum.ANALYZE_ELEMENTS.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        imageMapper.insert(image);

        executeVideoAnalysis(image, videoUrl, "ELEMENTS", model);

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long golden6s(Long userId, String prompt, Long modelId) {
        log.info("[AiVideoService] 开始黄金 6 秒拼接 - userId: {}, prompt: {}", userId, prompt);

        AiModelDO model = modelService.validateModel(modelId);
        int cost = getVideoCost(5);
        walletService.deductPoints(userId, cost, "黄金 6 秒拼接");

        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(model.getId());
        image.setPrompt(prompt);
        image.setGenerationType(AiVideoBizTypeEnum.GOLDEN_6S.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        imageMapper.insert(image);

        executeVideoGeneration(image, prompt, null, model, 5);

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long aiMix(Long userId, String prompt, Long modelId) {
        log.info("[AiVideoService] 开始 AI 超级混剪 - userId: {}, prompt: {}", userId, prompt);

        AiModelDO model = modelService.validateModel(modelId);
        int cost = getVideoCost(5);
        walletService.deductPoints(userId, cost, "AI 超级混剪");

        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(model.getId());
        image.setPrompt(prompt);
        image.setGenerationType(AiVideoBizTypeEnum.AI_MIX.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        imageMapper.insert(image);

        executeVideoGeneration(image, prompt, null, model, 5);

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long extractScript(Long userId, String videoUrl, Long modelId) {
        log.info("[AiVideoService] 开始视频拆解 - 提取脚本 - userId: {}, videoUrl: {}", userId, videoUrl);

        AiModelDO model = modelService.validateModel(modelId);
        int cost = getVideoAnalyzeCost();
        walletService.deductPoints(userId, cost, "视频拆解 - 提取脚本");

        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(model.getId());
        image.setPrompt("[提取脚本]" + videoUrl);
        image.setInputVideoUrl(videoUrl);
        image.setGenerationType(AiVideoBizTypeEnum.EXTRACT_SCRIPT.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        imageMapper.insert(image);

        executeVideoAnalysis(image, videoUrl, "SCRIPT", model);

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generatePrompt(Long userId, String videoUrl, Long modelId) {
        log.info("[AiVideoService] 开始视频拆解 - 生成提示词 - userId: {}, videoUrl: {}", userId, videoUrl);

        AiModelDO model = modelService.validateModel(modelId);
        int cost = getVideoAnalyzeCost();
        walletService.deductPoints(userId, cost, "视频拆解 - 生成提示词");

        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(model.getId());
        image.setPrompt("[生成提示词]" + videoUrl);
        image.setInputVideoUrl(videoUrl);
        image.setGenerationType(AiVideoBizTypeEnum.GENERATE_PROMPT.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        imageMapper.insert(image);

        executeVideoAnalysis(image, videoUrl, "PROMPT", model);

        return image.getId();
    }

    @Override
    public PageResult<AiImageDO> getVideoPage(Long userId, AiImagePageReqVO pageReqVO) {
        return imageMapper.selectPageMy(userId, pageReqVO);
    }

    @Override
    public AiImageDO getVideoDetail(Long id) {
        return imageMapper.selectById(id);
    }

    @Override
    public boolean syncVideoStatus(Long id) {
        AiImageDO image = imageMapper.selectById(id);
        if (image == null || image.getStatus() != AiImageStatusEnum.IN_PROGRESS.getStatus()) {
            return false;
        }

        String taskId = image.getTaskId();
        if (StrUtil.isBlank(taskId)) {
            return false;
        }

        AiModelDO model = modelService.validateModel(image.getModelId());
        WuMoApi wuMoApi = getWuMoApi(model);

        WuMoApi.TaskQueryResponse response = wuMoApi.queryTask(taskId);
        if (response.code() != 0 || response.data() == null) {
            log.warn("[AiVideoService] 查询任务状态失败 - id: {}, taskId: {}, message: {}",
                    id, taskId, response.message());
            return false;
        }

        String status = response.data().status();
        if ("SUCCESS".equals(status)) {
            String resultUrl = response.data().resultUrl();
            imageMapper.updateById(new AiImageDO().setId(id)
                    .setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                    .setOutputUrl(resultUrl)
                    .setFinishTime(LocalDateTime.now()));
            log.info("[AiVideoService] 视频生成成功 - id: {}, url: {}", id, resultUrl);
            return true;
        } else if ("FAILED".equals(status)) {
            imageMapper.updateById(new AiImageDO().setId(id)
                    .setStatus(AiImageStatusEnum.FAIL.getStatus())
                    .setErrorMessage(response.data().failReason())
                    .setFinishTime(LocalDateTime.now()));
            log.warn("[AiVideoService] 视频生成失败 - id: {}, reason: {}", id, response.data().failReason());
            return true;
        }

        imageMapper.updateById(new AiImageDO().setId(id)
                .setOptions(new HashMap<String, Object>() {{
                    put("progress", response.data().progress());
                }}));
        return true;
    }

    // ========== 异步执行方法 ==========

    /**
     * 异步执行视频分析
     */
    private void executeVideoAnalysis(AiImageDO image, String videoUrl, String analyzeType, AiModelDO model) {
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                WuMoApi api = getWuMoApi(model);
                WuMoApi.VideoAnalyzeResponse response = api.analyzeVideo(videoUrl, analyzeType, model.getModel());

                if (response.code() != 0 || response.data() == null) {
                    throw new RuntimeException("API 调用失败：" + response.message());
                }

                String taskId = response.data().taskId();
                imageMapper.updateById(new AiImageDO().setId(image.getId()).setTaskId(taskId));
                log.info("[AiVideoService] 视频分析任务提交成功 - id: {}, taskId: {}", image.getId(), taskId);

                WuMoApi.TaskQueryResponse finalResponse = api.waitForTaskComplete(
                        taskId, DEFAULT_MAX_WAIT_TIME, DEFAULT_POLL_INTERVAL);

                if (finalResponse == null || finalResponse.data() == null) {
                    throw new RuntimeException("任务状态查询失败");
                }

                String status = finalResponse.data().status();
                if ("SUCCESS".equals(status)) {
                    Map<String, Object> options = new HashMap<>();
                    options.put("analyzeType", analyzeType);
                    options.put("result", finalResponse.data().resultUrl());
                    imageMapper.updateById(new AiImageDO().setId(image.getId())
                            .setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                            .setOptions(options)
                            .setFinishTime(LocalDateTime.now()));
                    log.info("[AiVideoService] 视频分析成功 - id: {}, result: {}", image.getId(), finalResponse.data().resultUrl());
                } else {
                    imageMapper.updateById(new AiImageDO().setId(image.getId())
                            .setStatus(AiImageStatusEnum.FAIL.getStatus())
                            .setErrorMessage(finalResponse.data().failReason())
                            .setFinishTime(LocalDateTime.now()));
                    log.warn("[AiVideoService] 视频分析失败 - id: {}, reason: {}", image.getId(), finalResponse.data().failReason());
                }
            } catch (Exception e) {
                log.error("[AiVideoService] 视频分析异常 - id: {}", image.getId(), e);
                imageMapper.updateById(new AiImageDO().setId(image.getId())
                        .setStatus(AiImageStatusEnum.FAIL.getStatus())
                        .setErrorMessage(e.getMessage())
                        .setFinishTime(LocalDateTime.now()));
            }
        });
    }

    /**
     * 异步执行视频生成
     */
    private void executeVideoGeneration(AiImageDO image, String prompt, String imageUrl, AiModelDO model, Integer duration) {
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                // 优先使用小云雀 API
                if (xiaoNiaoApi != null) {
                    executeXiaoNiaoVideoGeneration(image, prompt, imageUrl, duration);
                    return;
                }

                // 回退到舞墨 API
                WuMoApi api = getWuMoApi(model);

                WuMoApi.VideoGenerateRequest request = WuMoApi.VideoGenerateRequest.builder()
                        .prompt(prompt)
                        .referenceImage(StrUtil.isNotBlank(imageUrl) ? downloadAndConvertToBase64(imageUrl) : null)
                        .model(model.getModel())
                        .duration(duration != null ? duration : 5)
                        .build();

                WuMoApi.VideoGenerateResponse response = api.generateVideo(request);
                if (response.code() != 0 || response.data() == null) {
                    throw new RuntimeException("API 调用失败：" + response.message());
                }

                String taskId = response.data().taskId();
                imageMapper.updateById(new AiImageDO().setId(image.getId()).setTaskId(taskId));
                log.info("[AiVideoService] 视频任务提交成功 - id: {}, taskId: {}", image.getId(), taskId);

                WuMoApi.TaskQueryResponse finalResponse = api.waitForTaskComplete(
                        taskId, DEFAULT_MAX_WAIT_TIME, DEFAULT_POLL_INTERVAL);

                if (finalResponse == null || finalResponse.data() == null) {
                    throw new RuntimeException("任务状态查询失败");
                }

                String status = finalResponse.data().status();
                if ("SUCCESS".equals(status)) {
                    String resultUrl = finalResponse.data().resultUrl();
                    imageMapper.updateById(new AiImageDO().setId(image.getId())
                            .setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                            .setOutputUrl(resultUrl)
                            .setFinishTime(LocalDateTime.now()));
                    log.info("[AiVideoService] 视频生成成功 - id: {}, url: {}", image.getId(), resultUrl);
                } else {
                    imageMapper.updateById(new AiImageDO().setId(image.getId())
                            .setStatus(AiImageStatusEnum.FAIL.getStatus())
                            .setErrorMessage(finalResponse.data().failReason())
                            .setFinishTime(LocalDateTime.now()));
                    log.warn("[AiVideoService] 视频生成失败 - id: {}, reason: {}", image.getId(), finalResponse.data().failReason());
                }
            } catch (Exception e) {
                log.error("[AiVideoService] 视频生成异常 - id: {}", image.getId(), e);
                imageMapper.updateById(new AiImageDO().setId(image.getId())
                        .setStatus(AiImageStatusEnum.FAIL.getStatus())
                        .setErrorMessage(e.getMessage())
                        .setFinishTime(LocalDateTime.now()));
            }
        });
    }

    /**
     * 使用小云雀 API 生成视频
     */
    private void executeXiaoNiaoVideoGeneration(AiImageDO image, String prompt, String imageUrl, Integer duration) {
        try {
            XiaoNiaoApi.VideoTaskRequest request = new XiaoNiaoApi.VideoTaskRequest(
                    prompt,
                    duration != null ? duration : 15,
                    "16:9",
                    "vlog_travel",
                    null,
                    null,
                    null,
                    null,
                    null
            );

            XiaoNiaoApi.VideoTaskResponse response = xiaoNiaoApi.submitTask(request);
            if (response.code() == null || response.code() != 0 || response.data() == null) {
                throw new RuntimeException("小云雀 API 调用失败：" + response.message());
            }

            String taskId = response.data().taskId();
            imageMapper.updateById(new AiImageDO().setId(image.getId()).setTaskId(taskId));
            log.info("[AiVideoService] 小云雀视频任务提交成功 - id: {}, taskId: {}", image.getId(), taskId);

            XiaoNiaoApi.TaskStatusResponse finalResponse = xiaoNiaoApi.waitForTaskComplete(
                    taskId, DEFAULT_MAX_WAIT_TIME, DEFAULT_POLL_INTERVAL);

            if (finalResponse == null || finalResponse.data() == null) {
                throw new RuntimeException("小云雀任务状态查询失败");
            }

            String status = finalResponse.data().status();
            if ("Succeeded".equals(status)) {
                String videoUrl = finalResponse.data().videoUrl();
                imageMapper.updateById(new AiImageDO().setId(image.getId())
                        .setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                        .setOutputUrl(videoUrl)
                        .setPicUrl(finalResponse.data().coverUrl())
                        .setFinishTime(LocalDateTime.now()));
                log.info("[AiVideoService] 小云雀视频生成成功 - id: {}, url: {}", image.getId(), videoUrl);
            } else {
                imageMapper.updateById(new AiImageDO().setId(image.getId())
                        .setStatus(AiImageStatusEnum.FAIL.getStatus())
                        .setErrorMessage(finalResponse.data().failReason())
                        .setFinishTime(LocalDateTime.now()));
                log.warn("[AiVideoService] 小云雀视频生成失败 - id: {}, reason: {}", image.getId(), finalResponse.data().failReason());
            }
        } catch (Exception e) {
            log.error("[AiVideoService] 小云雀视频生成异常 - id: {}", image.getId(), e);
            imageMapper.updateById(new AiImageDO().setId(image.getId())
                    .setStatus(AiImageStatusEnum.FAIL.getStatus())
                    .setErrorMessage(e.getMessage())
                    .setFinishTime(LocalDateTime.now()));
        }
    }

    /**
     * 下载图片并转换为 Base64
     */
    private String downloadAndConvertToBase64(String imageUrl) {
        try {
            byte[] imageBytes = java.net.HttpURLConnection.class
                    .cast(new java.net.URL(imageUrl).openConnection())
                    .getInputStream().readAllBytes();
            return Base64.encode(imageBytes);
        } catch (Exception e) {
            log.warn("[AiVideoService] 图片下载失败 - url: {}", imageUrl, e);
            return null;
        }
    }

    /**
     * 获取舞墨 API 实例
     */
    private WuMoApi getWuMoApi(AiModelDO model) {
        String apiKey = null;
        String baseUrl = WuMoApiConstants.BASE_URL;

        if (model.getKeyId() != null) {
            try {
                AiApiKeyDO apiKeyDO = apiKeyService.getApiKey(model.getKeyId());
                if (apiKeyDO != null && apiKeyDO.getApiKey() != null) {
                    apiKey = apiKeyDO.getApiKey();
                    if (apiKeyDO.getUrl() != null) {
                        baseUrl = apiKeyDO.getUrl();
                    }
                }
            } catch (Exception e) {
                log.warn("[AiVideoService] 从数据库获取 API Key 失败 - keyId: {}", model.getKeyId(), e);
            }
        }

        if (apiKey == null) {
            apiKey = System.getenv("WUMO_API_KEY");
            String envBaseUrl = System.getenv("WUMO_API_BASE_URL");
            if (envBaseUrl != null && !envBaseUrl.isBlank()) {
                baseUrl = envBaseUrl;
            }
        }

        if (apiKey == null) {
            apiKey = "XQuRvI6ZUoVg37e0PpcYDheRfY";
            log.warn("[AiVideoService] 未配置 API Key，使用默认值（仅开发测试）");
        }

            return new WuMoApiImpl(apiKey, baseUrl);
    }
}
