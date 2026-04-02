package cn.shuang.module.ai.service.video.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.module.ai.controller.admin.image.vo.AiImagePageReqVO;
import cn.shuang.module.ai.dal.dataobject.image.AiImageDO;
import cn.shuang.module.ai.dal.dataobject.model.AiModelDO;
import cn.shuang.module.ai.dal.mysql.image.AiImageMapper;
import cn.shuang.module.ai.enums.image.AiImageStatusEnum;
import cn.shuang.module.ai.enums.model.AiPlatformEnum;
import cn.shuang.module.ai.enums.video.AiVideoBizTypeEnum;
import cn.shuang.module.ai.framework.ai.core.model.wumo.api.WuMoApi;
import cn.shuang.module.ai.framework.ai.core.model.wumo.api.WuMoApiImpl;
import cn.shuang.module.ai.framework.ai.core.model.wumo.api.WuMoApiConstants;
import cn.shuang.module.ai.service.model.AiModelService;
import cn.shuang.module.ai.service.video.AiVideoService;
import cn.shuang.module.infra.api.file.FileApi;
import cn.shuang.module.pay.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private AiImageMapper imageMapper;

    @Resource
    private FileApi fileApi;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private WuMoApi wuMoApi;

    @Resource
    private WalletService walletService;

    private static final long DEFAULT_MAX_WAIT_TIME = 300000; // 5 分钟
    private static final long DEFAULT_POLL_INTERVAL = 5000; // 5 秒

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long textToVideo(Long userId, String prompt, Long modelId, Integer duration) {
        log.info("[AiVideoService] 开始生成视频 - userId: {}, prompt: {}, modelId: {}", userId, prompt, modelId);

        // 1. 验证模型
        AiModelDO model = modelService.validateModel(modelId);

        // 2. 扣减积分
        walletService.deductPoints(userId, 100, "AI 视频生成");

        // 3. 创建记录
        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(model.getId());
        image.setPrompt(prompt);
        image.setGenerationType(AiVideoBizTypeEnum.TEXT_TO_VIDEO.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        imageMapper.insert(image);

        // 4. 异步执行
        executeVideoGeneration(image, prompt, null, model, duration);

        return image.getId();
    }

    @Override
    public Long imageToVideo(Long userId, String imageUrl, String prompt, Long modelId, Integer duration) {
        log.info("[AiVideoService] 开始图生视频 - userId: {}, imageUrl: {}, prompt: {}", userId, imageUrl, prompt);

        // 1. 验证模型
        AiModelDO model = modelService.validateModel(modelId);

        // 2. 扣减积分
        walletService.deductPoints(userId, 100, "图生视频");

        // 3. 创建记录
        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(model.getId());
        image.setPrompt(prompt);
        image.setGenerationType(AiVideoBizTypeEnum.IMAGE_TO_VIDEO.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        imageMapper.insert(image);

        // 4. 异步执行
        executeVideoGeneration(image, prompt, imageUrl, model, duration);

        return image.getId();
    }

    @Override
    public Long analyzeElements(Long userId, String videoUrl, Long modelId) {
        log.info("[AiVideoService] 开始视频解析 - userId: {}, videoUrl: {}", userId, videoUrl);

        // 1. 验证模型
        AiModelDO model = modelService.validateModel(modelId);

        // 2. 扣减积分
        walletService.deductPoints(userId, 100, "视频解析");

        // 3. 创建记录
        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(model.getId());
        image.setPrompt("[视频解析]" + videoUrl);
        image.setGenerationType(AiVideoBizTypeEnum.ANALYZE_ELEMENTS.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        imageMapper.insert(image);

        // TODO: 实现视频解析逻辑

        return image.getId();
    }

    @Override
    public Long golden6s(Long userId, String prompt, Long modelId) {
        log.info("[AiVideoService] 开始黄金 6 秒拼接 - userId: {}, prompt: {}", userId, prompt);

        // 1. 验证模型
        AiModelDO model = modelService.validateModel(modelId);

        // 2. 扣减积分
        walletService.deductPoints(userId, 100, "黄金 6 秒拼接");

        // 3. 创建记录
        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(model.getId());
        image.setPrompt(prompt);
        image.setGenerationType(AiVideoBizTypeEnum.GOLDEN_6S.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        imageMapper.insert(image);

        // 4. 异步执行
        executeVideoGeneration(image, prompt, null, model, 6);

        return image.getId();
    }

    @Override
    public Long aiMix(Long userId, String prompt, Long modelId) {
        log.info("[AiVideoService] 开始 AI 超级混剪 - userId: {}, prompt: {}", userId, prompt);

        // 1. 验证模型
        AiModelDO model = modelService.validateModel(modelId);

        // 2. 扣减积分
        walletService.deductPoints(userId, 100, "AI 超级混剪");

        // 3. 创建记录
        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(model.getId());
        image.setPrompt(prompt);
        image.setGenerationType(AiVideoBizTypeEnum.AI_MIX.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        imageMapper.insert(image);

        // 4. 异步执行
        executeVideoGeneration(image, prompt, null, model, 5);

        return image.getId();
    }

    @Override
    public Long extractScript(Long userId, String videoUrl, Long modelId) {
        log.info("[AiVideoService] 开始视频拆解 - 提取脚本 - userId: {}, videoUrl: {}", userId, videoUrl);

        // 1. 验证模型
        AiModelDO model = modelService.validateModel(modelId);

        // 2. 扣减积分
        walletService.deductPoints(userId, 100, "视频拆解 - 提取脚本");

        // 3. 创建记录
        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(model.getId());
        image.setPrompt("[提取脚本]" + videoUrl);
        image.setGenerationType(AiVideoBizTypeEnum.EXTRACT_SCRIPT.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        imageMapper.insert(image);

        // TODO: 实现视频脚本提取逻辑

        return image.getId();
    }

    @Override
    public Long generatePrompt(Long userId, String videoUrl, Long modelId) {
        log.info("[AiVideoService] 开始视频拆解 - 生成提示词 - userId: {}, videoUrl: {}", userId, videoUrl);

        // 1. 验证模型
        AiModelDO model = modelService.validateModel(modelId);

        // 2. 扣减积分
        walletService.deductPoints(userId, 100, "视频拆解 - 生成提示词");

        // 3. 创建记录
        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(model.getId());
        image.setPrompt("[生成提示词]" + videoUrl);
        image.setGenerationType(AiVideoBizTypeEnum.GENERATE_PROMPT.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        imageMapper.insert(image);

        // TODO: 实现提示词生成逻辑

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

        // 获取 WuMo API
        AiModelDO model = modelService.validateModel(image.getModelId());
        WuMoApi wuMoApi = getWuMoApi(model);

        // 查询任务状态
        WuMoApi.TaskQueryResponse response = wuMoApi.queryTask(taskId);
        if (response.code() != 0 || response.data() == null) {
            log.warn("[AiVideoService] 查询任务状态失败 - id: {}, taskId: {}, message: {}",
                    id, taskId, response.message());
            return false;
        }

        // 更新状态
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

        // 处理中，更新进度
        imageMapper.updateById(new AiImageDO().setId(id)
                .setOptions(new HashMap<String, Object>() {{
                    put("progress", response.data().progress());
                }}));
        return true;
    }

    /**
     * 异步执行视频生成
     */
    private void executeVideoGeneration(AiImageDO image, String prompt, String imageUrl, AiModelDO model, Integer duration) {
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                // 获取 WuMo API
                WuMoApi api = getWuMoApi(model);

                // 构建请求
                WuMoApi.VideoGenerateRequest request = WuMoApi.VideoGenerateRequest.builder()
                        .prompt(prompt)
                        .referenceImage(StrUtil.isNotBlank(imageUrl) ? downloadAndConvertToBase64(imageUrl) : null)
                        .model(model.getModel())
                        .duration(duration != null ? duration : 5)
                        .build();

                // 调用 API
                WuMoApi.VideoGenerateResponse response = api.generateVideo(request);
                if (response.code() != 0 || response.data() == null) {
                    throw new RuntimeException("API 调用失败：" + response.message());
                }

                // 更新 taskId
                String taskId = response.data().taskId();
                imageMapper.updateById(new AiImageDO().setId(image.getId()).setTaskId(taskId));
                log.info("[AiVideoService] 视频任务提交成功 - id: {}, taskId: {}", image.getId(), taskId);

                // 轮询等待完成
                WuMoApi.TaskQueryResponse finalResponse = api.waitForTaskComplete(
                        taskId, DEFAULT_MAX_WAIT_TIME, DEFAULT_POLL_INTERVAL);

                if (finalResponse == null || finalResponse.data() == null) {
                    throw new RuntimeException("任务状态查询失败");
                }

                // 处理结果
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
     * 获取 WuMo API 实例
     * TODO: 需要从 AiApiKeyDO 获取真实的 apiKey，这里暂时使用硬编码
     */
    private WuMoApi getWuMoApi(AiModelDO model) {
        // TODO: 需要通过 model.getKeyId() 查询 AiApiKeyDO 获取真实的 apiKey
        String apiKey = "placeholder-api-key"; // 需要从 AiApiKeyDO 获取
        String baseUrl = WuMoApiConstants.BASE_URL;
        return new WuMoApiImpl(apiKey, baseUrl);
    }

}
