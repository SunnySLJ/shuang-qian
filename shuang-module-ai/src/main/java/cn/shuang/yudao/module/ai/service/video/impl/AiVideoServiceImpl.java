package cn.shuang.module.ai.service.video.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.module.ai.dal.dataobject.image.AiImageDO;
import cn.shuang.module.ai.dal.dataobject.model.AiModelDO;
import cn.shuang.module.ai.dal.mysql.image.AiImageMapper;
import cn.shuang.module.ai.enums.image.AiImageStatusEnum;
import cn.shuang.module.ai.enums.model.AiPlatformEnum;
import cn.shuang.module.ai.enums.video.AiVideoBizTypeEnum;
import cn.shuang.module.ai.framework.ai.core.model.wumo.api.WuMoApi;
import cn.shuang.module.ai.framework.ai.core.model.wumo.api.WuMoApiConstants;
import cn.shuang.module.ai.service.model.AiModelService;
import cn.shuang.module.ai.service.video.AiVideoService;
import cn.shuang.module.infra.api.file.FileApi;
import cn.shuang.module.pay.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

    /**
     * 默认轮询间隔（毫秒）
     */
    private static final long DEFAULT_POLL_INTERVAL = 3000L;

    /**
     * 默认最大等待时间（5 分钟）
     */
    private static final long DEFAULT_MAX_WAIT_TIME = 300000L;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long textToVideo(Long userId, String prompt, Long modelId, Integer duration) {
        log.info("[AiVideoService] 开始文生视频 - userId: {}, prompt: {}, modelId: {}", userId, prompt, modelId);

        // 1. 校验模型
        AiModelDO model = modelService.validateModel(modelId);
        if (!AiPlatformEnum.WU_MO.getPlatform().equals(model.getPlatform())) {
            throw new IllegalArgumentException("仅支持舞墨 AI 平台");
        }

        // 2. 获取业务类型和积分配置
        AiVideoBizTypeEnum bizType = AiVideoBizTypeEnum.TEXT_TO_VIDEO;

        // 3. 检查并扣减积分
        walletService.hasEnoughPoints(userId, bizType.getPoints());
        String bizOrderNo = generateBizOrderNo(userId, bizType.getBizType());
        walletService.deductPoints(userId, bizType.getPoints(), bizType.getBizType(), bizOrderNo, bizType.getDesc());

        // 4. 创建记录
        AiImageDO image = AiImageDO.builder()
                .userId(userId)
                .prompt(prompt)
                .modelId(modelId)
                .model(model.getModel())
                .platform(model.getPlatform())
                .generationType(2) // 文生视频
                .status(AiImageStatusEnum.IN_PROGRESS.getStatus())
                .publicStatus(false)
                .build();
        imageMapper.insert(image);

        // 5. 异步调用 API
        executeVideoGeneration(image, prompt, null, model, duration);

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long imageToVideo(Long userId, String imageUrl, String prompt, Long modelId, Integer duration) {
        log.info("[AiVideoService] 开始图生视频 - userId: {}, imageUrl: {}, prompt: {}", userId, imageUrl, prompt);

        // 1. 校验模型
        AiModelDO model = modelService.validateModel(modelId);
        if (!AiPlatformEnum.WU_MO.getPlatform().equals(model.getPlatform())) {
            throw new IllegalArgumentException("仅支持舞墨 AI 平台");
        }

        // 2. 获取业务类型和积分配置
        AiVideoBizTypeEnum bizType = AiVideoBizTypeEnum.IMAGE_TO_VIDEO;

        // 3. 检查并扣减积分
        walletService.hasEnoughPoints(userId, bizType.getPoints());
        String bizOrderNo = generateBizOrderNo(userId, bizType.getBizType());
        walletService.deductPoints(userId, bizType.getPoints(), bizType.getBizType(), bizOrderNo, bizType.getDesc());

        // 4. 创建记录
        AiImageDO image = AiImageDO.builder()
                .userId(userId)
                .prompt(prompt)
                .inputImageUrl(imageUrl)
                .modelId(modelId)
                .model(model.getModel())
                .platform(model.getPlatform())
                .generationType(3) // 图生视频
                .status(AiImageStatusEnum.IN_PROGRESS.getStatus())
                .publicStatus(false)
                .build();
        imageMapper.insert(image);

        // 5. 异步调用 API
        executeVideoGeneration(image, prompt, imageUrl, model, duration);

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long golden6s(Long userId, String prompt, Long modelId) {
        log.info("[AiVideoService] 开始黄金 6 秒拼接 - userId: {}, prompt: {}", userId, prompt);

        // 1. 校验模型
        AiModelDO model = modelService.validateModel(modelId);

        // 2. 获取业务类型和积分配置
        AiVideoBizTypeEnum bizType = AiVideoBizTypeEnum.GOLDEN_6S;

        // 3. 检查并扣减积分
        walletService.hasEnoughPoints(userId, bizType.getPoints());
        String bizOrderNo = generateBizOrderNo(userId, bizType.getBizType());
        walletService.deductPoints(userId, bizType.getPoints(), bizType.getBizType(), bizOrderNo, bizType.getDesc());

        // 4. 创建记录
        AiImageDO image = AiImageDO.builder()
                .userId(userId)
                .prompt(prompt)
                .modelId(modelId)
                .model(model.getModel())
                .platform(model.getPlatform())
                .generationType(4) // 黄金 6 秒
                .status(AiImageStatusEnum.IN_PROGRESS.getStatus())
                .publicStatus(false)
                .build();
        imageMapper.insert(image);

        // TODO: 实现黄金 6 秒拼接逻辑

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long aiMix(Long userId, String prompt, Long modelId) {
        log.info("[AiVideoService] 开始 AI 混剪 - userId: {}, prompt: {}", userId, prompt);

        // 1. 校验模型
        AiModelDO model = modelService.validateModel(modelId);

        // 2. 获取业务类型和积分配置
        AiVideoBizTypeEnum bizType = AiVideoBizTypeEnum.AI_MIX;

        // 3. 检查并扣减积分
        walletService.hasEnoughPoints(userId, bizType.getPoints());
        String bizOrderNo = generateBizOrderNo(userId, bizType.getBizType());
        walletService.deductPoints(userId, bizType.getPoints(), bizType.getBizType(), bizOrderNo, bizType.getDesc());

        // 4. 创建记录
        AiImageDO image = AiImageDO.builder()
                .userId(userId)
                .prompt(prompt)
                .modelId(modelId)
                .model(model.getModel())
                .platform(model.getPlatform())
                .generationType(5) // AI 混剪
                .status(AiImageStatusEnum.IN_PROGRESS.getStatus())
                .publicStatus(false)
                .build();
        imageMapper.insert(image);

        // TODO: 实现 AI 混剪逻辑

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long extractScript(Long userId, String videoUrl, Long modelId) {
        log.info("[AiVideoService] 开始视频拆解 - 提取脚本 - userId: {}, videoUrl: {}", userId, videoUrl);

        // 1. 校验模型
        AiModelDO model = modelService.validateModel(modelId);

        // 2. 获取业务类型和积分配置
        AiVideoBizTypeEnum bizType = AiVideoBizTypeEnum.EXTRACT_SCRIPT;

        // 3. 检查并扣减积分
        walletService.hasEnoughPoints(userId, bizType.getPoints());
        String bizOrderNo = generateBizOrderNo(userId, bizType.getBizType());
        walletService.deductPoints(userId, bizType.getPoints(), bizType.getBizType(), bizOrderNo, bizType.getDesc());

        // 4. 创建记录
        AiImageDO image = AiImageDO.builder()
                .userId(userId)
                .inputVideoUrl(videoUrl)
                .modelId(modelId)
                .model(model.getModel())
                .platform(model.getPlatform())
                .generationType(6) // 视频拆解 - 提取脚本
                .status(AiImageStatusEnum.IN_PROGRESS.getStatus())
                .publicStatus(false)
                .build();
        imageMapper.insert(image);

        // TODO: 实现视频拆解逻辑

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long analyzeElements(Long userId, String videoUrl, Long modelId) {
        log.info("[AiVideoService] 开始视频拆解 - 分析元素 - userId: {}, videoUrl: {}", userId, videoUrl);

        // 1. 校验模型
        AiModelDO model = modelService.validateModel(modelId);

        // 2. 获取业务类型和积分配置
        AiVideoBizTypeEnum bizType = AiVideoBizTypeEnum.ANALYZE_ELEMENTS;

        // 3. 检查并扣减积分
        walletService.hasEnoughPoints(userId, bizType.getPoints());
        String bizOrderNo = generateBizOrderNo(userId, bizType.getBizType());
        walletService.deductPoints(userId, bizType.getPoints(), bizType.getBizType(), bizOrderNo, bizType.getDesc());

        // 4. 创建记录
        AiImageDO image = AiImageDO.builder()
                .userId(userId)
                .inputVideoUrl(videoUrl)
                .modelId(modelId)
                .model(model.getModel())
                .platform(model.getPlatform())
                .generationType(7) // 视频拆解 - 分析元素
                .status(AiImageStatusEnum.IN_PROGRESS.getStatus())
                .publicStatus(false)
                .build();
        imageMapper.insert(image);

        // TODO: 实现视频拆解逻辑

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generatePrompt(Long userId, String videoUrl, Long modelId) {
        log.info("[AiVideoService] 开始视频拆解 - 生成提示词 - userId: {}, videoUrl: {}", userId, videoUrl);

        // 1. 校验模型
        AiModelDO model = modelService.validateModel(modelId);

        // 2. 获取业务类型和积分配置
        AiVideoBizTypeEnum bizType = AiVideoBizTypeEnum.GENERATE_PROMPT;

        // 3. 检查并扣减积分
        walletService.hasEnoughPoints(userId, bizType.getPoints());
        String bizOrderNo = generateBizOrderNo(userId, bizType.getBizType());
        walletService.deductPoints(userId, bizType.getPoints(), bizType.getBizType(), bizOrderNo, bizType.getDesc());

        // 4. 创建记录
        AiImageDO image = AiImageDO.builder()
                .userId(userId)
                .inputVideoUrl(videoUrl)
                .modelId(modelId)
                .model(model.getModel())
                .platform(model.getPlatform())
                .generationType(8) // 视频拆解 - 生成提示词
                .status(AiImageStatusEnum.IN_PROGRESS.getStatus())
                .publicStatus(false)
                .build();
        imageMapper.insert(image);

        // TODO: 实现视频拆解逻辑

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
        if (response.getCode() != 0 || response.getData() == null) {
            log.warn("[AiVideoService] 查询任务状态失败 - id: {}, taskId: {}, message: {}",
                    id, taskId, response.getMessage());
            return false;
        }

        // 更新状态
        String status = response.getData().getStatus();
        if ("SUCCESS".equals(status)) {
            String resultUrl = response.getData().getResultUrl();
            imageMapper.updateById(new AiImageDO().setId(id)
                    .setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                    .setOutputUrl(resultUrl)
                    .setFinishTime(LocalDateTime.now()));
            log.info("[AiVideoService] 视频生成成功 - id: {}, url: {}", id, resultUrl);
            return true;
        } else if ("FAILED".equals(status)) {
            imageMapper.updateById(new AiImageDO().setId(id)
                    .setStatus(AiImageStatusEnum.FAIL.getStatus())
                    .setErrorMessage(response.getData().getFailReason())
                    .setFinishTime(LocalDateTime.now()));
            log.warn("[AiVideoService] 视频生成失败 - id: {}, reason: {}", id, response.getData().getFailReason());
            return true;
        }

        // 处理中，更新进度
        imageMapper.updateById(new AiImageDO().setId(id)
                .setOptions(new HashMap<String, Object>() {{
                    put("progress", response.getData().getProgress());
                }}));
        return true;
    }

    /**
     * 异步执行视频生成
     */
    private void executeVideoGeneration(AiImageDO image, String prompt, String imageUrl, AiModelDO model, Integer duration) {
        new Thread(() -> {
            try {
                // 获取 WuMo API
                WuMoApi wuMoApi = getWuMoApi(model);

                // 构建请求
                WuMoApi.VideoGenerateRequest request = WuMoApi.VideoGenerateRequest.builder()
                        .prompt(prompt)
                        .referenceImage(StrUtil.isNotBlank(imageUrl) ? downloadAndConvertToBase64(imageUrl) : null)
                        .model(model.getModel())
                        .duration(duration != null ? duration : 5)
                        .build();

                // 调用 API
                WuMoApi.VideoGenerateResponse response = wuMoApi.generateVideo(request);
                if (response.getCode() != 0 || response.getData() == null) {
                    throw new RuntimeException("API 调用失败：" + response.getMessage());
                }

                // 更新 taskId
                String taskId = response.getData().getTaskId();
                imageMapper.updateById(new AiImageDO().setId(image.getId()).setTaskId(taskId));
                log.info("[AiVideoService] 视频任务提交成功 - id: {}, taskId: {}", image.getId(), taskId);

                // 轮询等待完成
                WuMoApi.TaskQueryResponse finalResponse = wuMoApi.waitForTaskComplete(
                        taskId, DEFAULT_MAX_WAIT_TIME, DEFAULT_POLL_INTERVAL);

                if (finalResponse == null || finalResponse.getData() == null) {
                    throw new RuntimeException("任务状态查询失败");
                }

                // 处理结果
                String status = finalResponse.getData().getStatus();
                if ("SUCCESS".equals(status)) {
                    String resultUrl = finalResponse.getData().getResultUrl();
                    imageMapper.updateById(new AiImageDO().setId(image.getId())
                            .setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                            .setOutputUrl(resultUrl)
                            .setFinishTime(LocalDateTime.now()));
                    log.info("[AiVideoService] 视频生成成功 - id: {}, url: {}", image.getId(), resultUrl);
                } else {
                    imageMapper.updateById(new AiImageDO().setId(image.getId())
                            .setStatus(AiImageStatusEnum.FAIL.getStatus())
                            .setErrorMessage(finalResponse.getData().getFailReason())
                            .setFinishTime(LocalDateTime.now()));
                    log.warn("[AiVideoService] 视频生成失败 - id: {}, reason: {}", image.getId(), finalResponse.getData().getFailReason());
                }
            } catch (Exception e) {
                log.error("[AiVideoService] 视频生成异常 - id: {}", image.getId(), e);
                imageMapper.updateById(new AiImageDO().setId(image.getId())
                        .setStatus(AiImageStatusEnum.FAIL.getStatus())
                        .setErrorMessage(e.getMessage())
                        .setFinishTime(LocalDateTime.now()));
            }
        }).start();
    }

    /**
     * 下载图片并转换为 Base64
     */
    private String downloadAndConvertToBase64(String imageUrl) {
        try {
            byte[] imageBytes = cn.hutool.http.HttpUtil.downloadBytes(imageUrl);
            return Base64.encode(imageBytes);
        } catch (Exception e) {
            log.error("[downloadAndConvertToBase64] 下载图片失败 - imageUrl: {}", imageUrl, e);
            return null;
        }
    }

    /**
     * 获取 WuMo API 实例
     */
    private WuMoApi getWuMoApi(AiModelDO model) {
        // 使用注入的 WuMoApi bean
        return wuMoApi;
    }

    /**
     * 生成业务订单号
     *
     * @param userId  用户 ID
     * @param bizType 业务类型
     * @return 业务订单号
     */
    private String generateBizOrderNo(Long userId, Integer bizType) {
        return String.format("AI-VID-%d-%d-%d", userId, bizType, System.currentTimeMillis());
    }

}
