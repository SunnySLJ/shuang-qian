package cn.shuang.module.ai.service.video.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.module.ai.config.AiVideoTaskExecutorConfiguration;
import cn.shuang.module.ai.config.XiaoNiaoProperties;
import cn.shuang.module.ai.controller.admin.image.vo.AiImagePageReqVO;
import cn.shuang.module.ai.controller.app.video.vo.AiVideoResolveRespVO;
import cn.shuang.module.ai.config.VideoAnalyzeProperties;
import cn.shuang.module.ai.dal.dataobject.image.AiImageDO;
import cn.shuang.module.ai.dal.dataobject.model.AiApiKeyDO;
import cn.shuang.module.ai.dal.dataobject.model.AiModelDO;
import cn.shuang.module.ai.dal.mysql.image.AiImageMapper;
import cn.shuang.module.ai.enums.image.AiImageStatusEnum;
import cn.shuang.module.ai.enums.model.AiModelTypeEnum;
import cn.shuang.module.ai.enums.video.AiVideoBizTypeEnum;
import cn.shuang.module.ai.framework.ai.core.model.wumo.api.WuMoApi;
import cn.shuang.module.ai.framework.ai.core.model.wumo.api.WuMoApiImpl;
import cn.shuang.module.ai.framework.ai.core.model.wumo.api.WuMoApiConstants;
import cn.shuang.module.ai.framework.ai.core.model.xiaoniao.api.XiaoNiaoApi;
import cn.shuang.module.ai.framework.ai.core.model.xiaoniao.api.XiaoNiaoApiImpl;
import cn.shuang.module.ai.service.video.analysis.OpenAiCompatibleVideoAnalyzeClient;
import cn.shuang.module.ai.service.video.analysis.VideoAnalyzeProvider;
import cn.shuang.module.ai.service.model.AiApiKeyService;
import cn.shuang.module.ai.service.model.AiModelService;
import cn.shuang.module.ai.service.video.AiVideoService;
import cn.shuang.module.ai.service.video.ShareLinkResolveService;
import cn.shuang.module.infra.api.file.FileApi;
import cn.shuang.module.pay.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * AI 视频生成 Service 实现类
 *
 * @author shuang-pro
 */
@Service
@Slf4j
@DependsOn(AiVideoTaskExecutorConfiguration.VIDEO_TASK_EXECUTOR)
public class AiVideoServiceImpl implements AiVideoService {

    private static final int MAX_ERROR_MESSAGE_LENGTH = 500;

    /** 视频生成 bizType 前缀，用于生成唯一幂等 key */
    private static final String BIZ_ORDER_PREFIX = "video:";

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
    private OpenAiCompatibleVideoAnalyzeClient openAiCompatibleVideoAnalyzeClient;

    @Resource
    private VideoAnalyzeProperties videoAnalyzeProperties;

    @Resource
    private ShareLinkResolveService shareLinkResolveService;

    @Resource
    private XiaoNiaoProperties xiaoniaoProperties;

    /** AI 视频专用线程池，替代原生 Thread.start() */
    @Resource
    @Qualifier(AiVideoTaskExecutorConfiguration.VIDEO_TASK_EXECUTOR)
    private Executor videoTaskExecutor;

    @Value("${yudao.shuang-pro.points.video-5s-cost:20}")
    private Integer video5sCost;

    @Value("${yudao.shuang-pro.points.video-15s-cost:40}")
    private Integer video15sCost;

    @Value("${yudao.shuang-pro.points.video-30s-cost:60}")
    private Integer video30sCost;

    @Value("${yudao.shuang-pro.points.video-analyze-cost:5}")
    private Integer videoAnalyzeCost;

    private static final long DEFAULT_MAX_WAIT_TIME = 300000;
    private static final long DEFAULT_POLL_INTERVAL = 5000;

    private XiaoNiaoApi xiaoNiaoApi;

    @PostConstruct
    public void init() {
        if (xiaoniaoProperties.isEnabled()) {
            this.xiaoNiaoApi = new XiaoNiaoApiImpl(xiaoniaoProperties, objectMapper);
            log.info("[AiVideoService] 小云雀 API 初始化成功");
        } else {
            log.warn("[AiVideoService] 未启用小云雀配置，将使用舞墨 API");
        }
    }

    // ========== 积分配置 ==========

    /**
     * 获取视频生成消耗积分（根据时长）
     */
    private Integer getVideoCost(Integer duration) {
        if (duration == null || duration <= 5) {
            return video5sCost;
        } else if (duration <= 15) {
            return video15sCost;
        } else {
            return video30sCost;
        }
    }

    private Integer getVideoAnalyzeCost() {
        return videoAnalyzeCost;
    }

    // ========== 视频生成方法 ==========

    /**
     * 生成唯一的业务订单号，用于积分扣减和回滚的幂等追踪。
     * 格式：video:{bizType}:{userId}:{uuid}
     */
    private String generateBizOrderNo(int bizType, Long userId) {
        return BIZ_ORDER_PREFIX + bizType + ":" + userId + ":" + IdUtil.fastSimpleUUID();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long textToVideo(Long userId, String prompt, Long modelId, Integer duration, Map<String, Object> options) {
        log.info("[AiVideoService] 开始生成视频 - userId: {}, prompt: {}, modelId: {}", userId, prompt, modelId);

        // 1. 验证模型
        AiModelDO model = modelService.validateModel(modelId);

        // 2. 获取积分消耗
        int cost = getVideoCost(duration);

        // 3. 生成幂等订单号
        String bizOrderNo = generateBizOrderNo(AiVideoBizTypeEnum.TEXT_TO_VIDEO.getBizType(), userId);

        // 4. 扣减积分（传入 bizOrderNo 用于回滚追踪）
        walletService.deductPoints(userId, cost, AiVideoBizTypeEnum.TEXT_TO_VIDEO.getBizType(),
                bizOrderNo, "AI 文生视频");

        // 5. 创建记录
        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(resolvePersistedModelId(model));
        image.setPrompt(prompt);
        image.setOptions(options);
        image.setGenerationType(AiVideoBizTypeEnum.TEXT_TO_VIDEO.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        image.setBizOrderNo(bizOrderNo);
        image.setDuration(duration);
        imageMapper.insert(image);

        // 6. 事务提交后异步执行
        executeAfterCommit(image.getId(), bizOrderNo, cost, () ->
                executeVideoGeneration(image, prompt, null, model, duration, options));

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long imageToVideo(Long userId, String imageUrl, String prompt, Long modelId, Integer duration,
                             Map<String, Object> options) {
        log.info("[AiVideoService] 开始图生视频 - userId: {}, imageUrl: {}, prompt: {}", userId, imageUrl, prompt);

        AiModelDO model = modelService.validateModel(modelId);
        int cost = getVideoCost(duration);
        String bizOrderNo = generateBizOrderNo(AiVideoBizTypeEnum.IMAGE_TO_VIDEO.getBizType(), userId);

        walletService.deductPoints(userId, cost, AiVideoBizTypeEnum.IMAGE_TO_VIDEO.getBizType(),
                bizOrderNo, "图生视频");

        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(resolvePersistedModelId(model));
        image.setPrompt(prompt);
        image.setInputImageUrl(imageUrl);
        image.setOptions(options);
        image.setGenerationType(AiVideoBizTypeEnum.IMAGE_TO_VIDEO.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        image.setBizOrderNo(bizOrderNo);
        image.setDuration(duration);
        imageMapper.insert(image);

        executeAfterCommit(image.getId(), bizOrderNo, cost, () ->
                executeVideoGeneration(image, prompt, imageUrl, model, duration, options));

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long analyzeElements(Long userId, String videoUrl, Long modelId, String providerCode) {
        log.info("[AiVideoService] 开始视频解析 - 分析元素 - userId: {}, videoUrl: {}, provider: {}", userId, videoUrl, providerCode);

        VideoAnalyzeProvider provider = resolveAnalyzeProvider(providerCode);
        AiModelDO model = resolveAnalyzeModel(modelId, provider);
        int cost = getVideoAnalyzeCost();
        String bizOrderNo = generateBizOrderNo(AiVideoBizTypeEnum.ANALYZE_ELEMENTS.getBizType(), userId);

        walletService.deductPoints(userId, cost, AiVideoBizTypeEnum.ANALYZE_ELEMENTS.getBizType(),
                bizOrderNo, "视频拆解 - 分析元素");

        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(resolvePersistedModelId(model));
        image.setPrompt("[视频拆解 - 分析元素]" + videoUrl);
        image.setInputVideoUrl(videoUrl);
        image.setGenerationType(AiVideoBizTypeEnum.ANALYZE_ELEMENTS.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        image.setBizOrderNo(bizOrderNo);
        imageMapper.insert(image);

        executeAfterCommit(image.getId(), bizOrderNo, cost, () ->
                executeVideoAnalysis(image, videoUrl, "ELEMENTS", model, provider,
                        AiVideoBizTypeEnum.ANALYZE_ELEMENTS.getBizType()));

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long golden6s(Long userId, String prompt, Long modelId) {
        log.info("[AiVideoService] 开始黄金 6 秒拼接 - userId: {}, prompt: {}", userId, prompt);

        AiModelDO model = modelService.validateModel(modelId);
        int cost = getVideoCost(5);
        String bizOrderNo = generateBizOrderNo(AiVideoBizTypeEnum.GOLDEN_6S.getBizType(), userId);

        walletService.deductPoints(userId, cost, AiVideoBizTypeEnum.GOLDEN_6S.getBizType(),
                bizOrderNo, "黄金 6 秒拼接");

        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(resolvePersistedModelId(model));
        image.setPrompt(prompt);
        image.setGenerationType(AiVideoBizTypeEnum.GOLDEN_6S.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        image.setBizOrderNo(bizOrderNo);
        imageMapper.insert(image);

        executeAfterCommit(image.getId(), bizOrderNo, cost, () ->
                executeVideoGeneration(image, prompt, null, model, 5, null));

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long aiMix(Long userId, String prompt, Long modelId) {
        log.info("[AiVideoService] 开始 AI 超级混剪 - userId: {}, prompt: {}", userId, prompt);

        AiModelDO model = modelService.validateModel(modelId);
        int cost = getVideoCost(5);
        String bizOrderNo = generateBizOrderNo(AiVideoBizTypeEnum.AI_MIX.getBizType(), userId);

        walletService.deductPoints(userId, cost, AiVideoBizTypeEnum.AI_MIX.getBizType(),
                bizOrderNo, "AI 超级混剪");

        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(resolvePersistedModelId(model));
        image.setPrompt(prompt);
        image.setGenerationType(AiVideoBizTypeEnum.AI_MIX.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        image.setBizOrderNo(bizOrderNo);
        imageMapper.insert(image);

        executeAfterCommit(image.getId(), bizOrderNo, cost, () ->
                executeVideoGeneration(image, prompt, null, model, 5, null));

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long extractScript(Long userId, String videoUrl, Long modelId, String providerCode) {
        log.info("[AiVideoService] 开始视频拆解 - 提取脚本 - userId: {}, videoUrl: {}, provider: {}", userId, videoUrl, providerCode);

        VideoAnalyzeProvider provider = resolveAnalyzeProvider(providerCode);
        AiModelDO model = resolveAnalyzeModel(modelId, provider);
        int cost = getVideoAnalyzeCost();
        String bizOrderNo = generateBizOrderNo(AiVideoBizTypeEnum.EXTRACT_SCRIPT.getBizType(), userId);

        walletService.deductPoints(userId, cost, AiVideoBizTypeEnum.EXTRACT_SCRIPT.getBizType(),
                bizOrderNo, "视频拆解 - 提取脚本");

        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(resolvePersistedModelId(model));
        image.setPrompt("[提取脚本]" + videoUrl);
        image.setInputVideoUrl(videoUrl);
        image.setGenerationType(AiVideoBizTypeEnum.EXTRACT_SCRIPT.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        image.setBizOrderNo(bizOrderNo);
        imageMapper.insert(image);

        executeAfterCommit(image.getId(), bizOrderNo, cost, () ->
                executeVideoAnalysis(image, videoUrl, "SCRIPT", model, provider,
                        AiVideoBizTypeEnum.EXTRACT_SCRIPT.getBizType()));

        return image.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generatePrompt(Long userId, String videoUrl, Long modelId, String providerCode) {
        log.info("[AiVideoService] 开始视频拆解 - 生成提示词 - userId: {}, videoUrl: {}, provider: {}", userId, videoUrl, providerCode);

        VideoAnalyzeProvider provider = resolveAnalyzeProvider(providerCode);
        AiModelDO model = resolveAnalyzeModel(modelId, provider);
        int cost = getVideoAnalyzeCost();
        String bizOrderNo = generateBizOrderNo(AiVideoBizTypeEnum.GENERATE_PROMPT.getBizType(), userId);

        walletService.deductPoints(userId, cost, AiVideoBizTypeEnum.GENERATE_PROMPT.getBizType(),
                bizOrderNo, "视频拆解 - 生成提示词");

        AiImageDO image = new AiImageDO();
        image.setUserId(userId);
        image.setModelId(resolvePersistedModelId(model));
        image.setPrompt("[生成提示词]" + videoUrl);
        image.setInputVideoUrl(videoUrl);
        image.setGenerationType(AiVideoBizTypeEnum.GENERATE_PROMPT.getBizType());
        image.setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        image.setPublicStatus(false);
        image.setBizOrderNo(bizOrderNo);
        imageMapper.insert(image);

        executeAfterCommit(image.getId(), bizOrderNo, cost, () ->
                executeVideoAnalysis(image, videoUrl, "PROMPT", model, provider,
                        AiVideoBizTypeEnum.GENERATE_PROMPT.getBizType()));

        return image.getId();
    }

    // ========== 事务后异步执行 ==========

    /**
     * 在事务提交后执行异步任务，避免事务未提交时异步线程访问不到数据。
     * 使用 Spring TransactionSynchronization 保证事务已提交再执行。
     */
    private void executeAfterCommit(Long imageId, String bizOrderNo, int cost, Runnable task) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    submitVideoTask(imageId, bizOrderNo, cost, task);
                }
            });
        } else {
            // 无事务上下文时直接提交
            submitVideoTask(imageId, bizOrderNo, cost, task);
        }
    }

    /**
     * 提交视频任务到线程池执行。
     * 使用线程池替代原生 Thread.start()，复用线程、支持优雅停机。
     */
    private void submitVideoTask(Long imageId, String bizOrderNo, int cost, Runnable task) {
        videoTaskExecutor.execute(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error("[AiVideoService] 视频任务执行异常 - imageId: {}", imageId, e);
                refundVideoGenerationCost(imageId);
            }
        });
    }

    // ========== 异步执行方法 ==========

    /**
     * 异步执行视频分析（带 Provider 降级链）。
     * 注意：此方法在事务提交后的线程中执行。
     */
    private void executeVideoAnalysis(AiImageDO image, String videoUrl, String analyzeType,
                                      AiModelDO model, VideoAnalyzeProvider provider, Integer bizType) {
        try {
            if (provider == VideoAnalyzeProvider.WUMO) {
                executeWuMoVideoAnalysis(image, videoUrl, analyzeType, model);
                return;
            }

            // 降级链：先尝试主供应商，失败后尝试备用供应商
            String result = analyzeWithFallback(provider, analyzeType, videoUrl);
            Map<String, Object> options = new HashMap<>();
            options.put("analyzeType", analyzeType);
            options.put("provider", provider.getCode());
            options.put("result", result);
            options.put("sourceVideoUrl", videoUrl);
            imageMapper.updateById(new AiImageDO().setId(image.getId())
                    .setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                    .setOutputUrl(videoUrl)
                    .setOptions(options)
                    .setFinishTime(LocalDateTime.now()));
            log.info("[AiVideoService] 视频分析成功 - id: {}, provider: {}", image.getId(), provider.getCode());
        } catch (Exception e) {
            log.error("[AiVideoService] 视频分析异常 - id: {}", image.getId(), e);
            // 分析失败不扣积分（由 Controller 层幂等保证不重复扣，这里只更新状态）
            imageMapper.updateById(new AiImageDO().setId(image.getId())
                    .setStatus(AiImageStatusEnum.FAIL.getStatus())
                    .setErrorMessage(buildSafeErrorMessage(e))
                    .setFinishTime(LocalDateTime.now()));
        }
    }

    /**
     * 带降级链的视频分析：主供应商失败后尝试备用供应商。
     */
    private String analyzeWithFallback(VideoAnalyzeProvider primary, String analyzeType, String videoUrl) {
        VideoAnalyzeProvider fallback = resolveFallbackProvider(primary);
        if (fallback == null) {
            // 无可用备用，直接调用主供应商
            return openAiCompatibleVideoAnalyzeClient.analyze(videoUrl, analyzeType, primary);
        }

        try {
            return openAiCompatibleVideoAnalyzeClient.analyze(videoUrl, analyzeType, primary);
        } catch (Exception primaryEx) {
            log.warn("[AiVideoService] 主供应商 {} 分析失败，尝试备用供应商 {}", primary.getCode(), fallback.getCode());
            try {
                return openAiCompatibleVideoAnalyzeClient.analyze(videoUrl, analyzeType, fallback);
            } catch (Exception fallbackEx) {
                throw new RuntimeException(primary.getCode() + " 和 " + fallback.getCode() + " 均失败: " + primaryEx.getMessage(), primaryEx);
            }
        }
    }

    /**
     * 解析备用供应商配置，跳过 WUMO 和与主供应商相同的配置。
     */
    private VideoAnalyzeProvider resolveFallbackProvider(VideoAnalyzeProvider primary) {
        String fallbackCode = videoAnalyzeProperties.getFallbackProvider();
        try {
            VideoAnalyzeProvider fallback = VideoAnalyzeProvider.fromCodeOrNull(fallbackCode);
            if (fallback == null || fallback == primary || fallback == VideoAnalyzeProvider.WUMO) {
                return null;
            }
            return fallback;
        } catch (IllegalArgumentException e) {
            log.warn("[AiVideoService] 备用供应商配置无效: {}，跳过降级", fallbackCode);
            return null;
        }
    }

    private void executeWuMoVideoAnalysis(AiImageDO image, String videoUrl, String analyzeType, AiModelDO model) {
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
            options.put("provider", VideoAnalyzeProvider.WUMO.getCode());
            options.put("result", finalResponse.data().resultUrl());
            options.put("sourceVideoUrl", videoUrl);
            imageMapper.updateById(new AiImageDO().setId(image.getId())
                    .setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                    .setOutputUrl(videoUrl)
                    .setOptions(options)
                    .setFinishTime(LocalDateTime.now()));
            log.info("[AiVideoService] 视频分析成功 - id: {}, result: {}", image.getId(), finalResponse.data().resultUrl());
        } else {
            throw new RuntimeException(StrUtil.blankToDefault(finalResponse.data().failReason(), "视频分析失败"));
        }
    }

    /**
     * 异步执行视频生成。
     * 失败时使用与扣减时相同的 bizOrderNo 进行回滚，保证幂等。
     */
    private void executeVideoGeneration(AiImageDO image, String prompt, String imageUrl, AiModelDO model,
                                        Integer duration, Map<String, Object> options) {
        try {
            if (shouldUseXiaoNiao(model)) {
                if (xiaoNiaoApi == null) {
                    throw new RuntimeException("已选择小云雀模型，但小云雀配置未启用");
                }
                executeXiaoNiaoVideoGeneration(image, prompt, imageUrl, duration, options);
                return;
            }

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
                        .setErrorMessage(buildSafeErrorMessage(finalResponse.data().failReason()))
                        .setFinishTime(LocalDateTime.now()));
                log.warn("[AiVideoService] 视频生成失败 - id: {}, reason: {}", image.getId(), finalResponse.data().failReason());
            }
        } catch (Exception e) {
            log.error("[AiVideoService] 视频生成异常 - id: {}", image.getId(), e);
            imageMapper.updateById(new AiImageDO().setId(image.getId())
                    .setStatus(AiImageStatusEnum.FAIL.getStatus())
                    .setErrorMessage(buildSafeErrorMessage(e))
                    .setFinishTime(LocalDateTime.now()));
            // 生成失败，回滚积分（从 AiImageDO.bizOrderNo 查找原始扣减流水，退回相同金额）
            refundVideoGenerationCost(image.getId());
        }
    }

    private boolean shouldUseXiaoNiao(AiModelDO model) {
        if (model == null) {
            return false;
        }
        return "XiaoNiao".equalsIgnoreCase(model.getPlatform())
                || "xiao-niao-marketing-video".equalsIgnoreCase(model.getModel());
    }

    /**
     * 使用小云雀 API 生成视频
     */
    private void executeXiaoNiaoVideoGeneration(AiImageDO image, String prompt, String imageUrl, Integer duration,
                                                Map<String, Object> options) {
        try {
            String ratio = getOption(options, "ratio", xiaoniaoProperties.getDefaultRatio());
            String styleType = getOption(options, "styleType", xiaoniaoProperties.getDefaultStyleType());
            String qualityLevel = getOption(options, "qualityLevel", xiaoniaoProperties.getDefaultQualityLevel());
            Map<String, Object> extra = new HashMap<>();
            putIfNotBlank(extra, "visual_style", getOption(options, "visualStyle", null));
            putIfNotBlank(extra, "camera_movement", getOption(options, "cameraMovement", null));
            putIfNotBlank(extra, "shot_focus", getOption(options, "shotFocus", null));
            putIfNotBlank(extra, "quality_level", qualityLevel);
            putIfNotBlank(extra, "reference_image_url", imageUrl);

            XiaoNiaoApi.VideoTaskRequest request = new XiaoNiaoApi.VideoTaskRequest(
                    prompt,
                    duration != null ? duration : 15,
                    ratio,
                    styleType,
                    prompt,
                    null,
                    null,
                    null,
                    extra.isEmpty() ? null : extra
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
                        .setErrorMessage(buildSafeErrorMessage(finalResponse.data().failReason()))
                        .setFinishTime(LocalDateTime.now()));
                log.warn("[AiVideoService] 小云雀视频生成失败 - id: {}, reason: {}", image.getId(), finalResponse.data().failReason());
            }
        } catch (Exception e) {
            log.error("[AiVideoService] 小云雀视频生成异常 - id: {}", image.getId(), e);
            imageMapper.updateById(new AiImageDO().setId(image.getId())
                    .setStatus(AiImageStatusEnum.FAIL.getStatus())
                    .setErrorMessage(buildSafeErrorMessage(e))
                    .setFinishTime(LocalDateTime.now()));
            // 生成失败，回滚积分
            refundVideoGenerationCost(image.getId());
        }
    }

    private String getOption(Map<String, Object> options, String key, String defaultValue) {
        if (options == null) {
            return defaultValue;
        }
        Object value = options.get(key);
        if (value == null) {
            return defaultValue;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? defaultValue : text;
    }

    private void putIfNotBlank(Map<String, Object> target, String key, String value) {
        if (StrUtil.isNotBlank(value)) {
            target.put(key, value);
        }
    }

    /**
     * 回滚视频生成消耗的积分。
     * 通过 AiImageDO.bizOrderNo 查找原始扣减流水，退回相同金额。
     * 退款 bizType = 5（按项目规范：正数=收入，5=退款）。
     */
    private void refundVideoGenerationCost(Long recordId) {
        try {
            AiImageDO record = imageMapper.selectById(recordId);
            if (record == null) {
                log.warn("[refundVideoGenerationCost] 记录不存在 - recordId={}", recordId);
                return;
            }
            String bizOrderNo = record.getBizOrderNo();
            if (bizOrderNo == null || bizOrderNo.isBlank()) {
                log.warn("[refundVideoGenerationCost] 记录无 bizOrderNo，无法退款 - recordId={}", recordId);
                return;
            }
            // 查找原始扣减流水，获得精确退款金额
            cn.shuang.module.pay.dal.dataobject.WalletTransactionDO transaction =
                    walletService.getTransactionByBizOrderNo(bizOrderNo);
            if (transaction == null) {
                log.warn("[refundVideoGenerationCost] 未找到原始扣减流水，bizOrderNo={}", bizOrderNo);
                return;
            }
            // 原始扣减金额存储为负数（支出），退款取绝对值
            int refundAmount = Math.abs(transaction.getAmount());
            if (refundAmount <= 0) {
                log.warn("[refundVideoGenerationCost] 原始扣减金额为0，跳过 - bizOrderNo={}", bizOrderNo);
                return;
            }
            walletService.addPoints(record.getUserId(), refundAmount, 5,
                    "refund:" + bizOrderNo, "视频生成失败退回积分");
            log.info("[refundVideoGenerationCost] 积分退回成功 - recordId={}, amount={}, bizOrderNo={}",
                    recordId, refundAmount, bizOrderNo);
        } catch (Exception ex) {
            log.error("[refundVideoGenerationCost] 积分退回失败 - recordId={}", recordId, ex);
        }
    }

    @Override
    public PageResult<AiImageDO> getVideoPage(Long userId, AiImagePageReqVO pageReqVO) {
        return imageMapper.selectVideoPageMy(userId, pageReqVO);
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
        if (shouldUseXiaoNiao(model) && xiaoNiaoApi != null) {
            return syncXiaoNiaoVideoStatus(id, taskId);
        }
        return syncWuMoVideoStatus(id, taskId, model);
    }

    private boolean syncXiaoNiaoVideoStatus(Long id, String taskId) {
        XiaoNiaoApi.TaskStatusResponse response = xiaoNiaoApi.queryTask(taskId);
        if (response.code() == null || response.code() != 0 || response.data() == null) {
            log.warn("[AiVideoService] 查询小云雀任务状态失败 - id: {}, taskId: {}, message: {}",
                    id, taskId, response.message());
            return false;
        }

        String status = response.data().status();
        if ("Succeeded".equals(status)) {
            imageMapper.updateById(new AiImageDO().setId(id)
                    .setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                    .setOutputUrl(response.data().videoUrl())
                    .setPicUrl(response.data().coverUrl())
                    .setFinishTime(LocalDateTime.now()));
            log.info("[AiVideoService] 小云雀视频生成成功 - id: {}, url: {}", id, response.data().videoUrl());
            return true;
        } else if ("Failed".equals(status)) {
            imageMapper.updateById(new AiImageDO().setId(id)
                    .setStatus(AiImageStatusEnum.FAIL.getStatus())
                    .setErrorMessage(buildSafeErrorMessage(response.data().failReason()))
                    .setFinishTime(LocalDateTime.now()));
            log.warn("[AiVideoService] 小云雀视频生成失败 - id: {}, reason: {}", id, response.data().failReason());
            return true;
        }

        imageMapper.updateById(new AiImageDO().setId(id)
                .setOptions(new HashMap<String, Object>() {{
                    put("progress", response.data().progress());
                }}));
        return true;
    }

    private boolean syncWuMoVideoStatus(Long id, String taskId, AiModelDO model) {
        WuMoApi wuMoApi = getWuMoApi(model);

        WuMoApi.TaskQueryResponse response = wuMoApi.queryTask(taskId);
        if (response.code() != 0 || response.data() == null) {
            log.warn("[AiVideoService] 查询舞墨任务状态失败 - id: {}, taskId: {}, message: {}",
                    id, taskId, response.message());
            return false;
        }

        String status = response.data().status();
        if ("SUCCESS".equals(status)) {
            imageMapper.updateById(new AiImageDO().setId(id)
                    .setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                    .setOutputUrl(response.data().resultUrl())
                    .setFinishTime(LocalDateTime.now()));
            log.info("[AiVideoService] 舞墨视频生成成功 - id: {}, url: {}", id, response.data().resultUrl());
            return true;
        } else if ("FAILED".equals(status)) {
            imageMapper.updateById(new AiImageDO().setId(id)
                    .setStatus(AiImageStatusEnum.FAIL.getStatus())
                    .setErrorMessage(buildSafeErrorMessage(response.data().failReason()))
                    .setFinishTime(LocalDateTime.now()));
            log.warn("[AiVideoService] 舞墨视频生成失败 - id: {}, reason: {}", id, response.data().failReason());
            return true;
        }

        imageMapper.updateById(new AiImageDO().setId(id)
                .setOptions(new HashMap<String, Object>() {{
                    put("progress", response.data().progress());
                }}));
        return true;
    }

    @Override
    public AiVideoResolveRespVO resolveVideoUrl(String rawText) {
        return shareLinkResolveService.resolve(rawText);
    }

    // ========== 辅助方法 ==========

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

    private VideoAnalyzeProvider resolveAnalyzeProvider(String providerCode) {
        VideoAnalyzeProvider defaultProvider = VideoAnalyzeProvider.fromCode(
                videoAnalyzeProperties.getDefaultProvider(), VideoAnalyzeProvider.ALIYUN);
        return VideoAnalyzeProvider.fromCode(providerCode, defaultProvider);
    }

    private AiModelDO resolveAnalyzeModel(Long modelId, VideoAnalyzeProvider provider) {
        if (provider == VideoAnalyzeProvider.WUMO) {
            if (modelId != null) {
                return modelService.validateModel(modelId);
            }
            try {
                return modelService.getRequiredDefaultModel(AiModelTypeEnum.VIDEO.getType());
            } catch (Exception ex) {
                log.warn("[AiVideoService] 未找到新版视频模型定义，回退旧版 type=2 配置", ex);
                return modelService.getRequiredDefaultModel(2);
            }
        }
        if (modelId == null) {
            return AiModelDO.builder().id(0L).model(provider.getCode()).build();
        }
        try {
            return modelService.validateModel(modelId);
        } catch (Exception ex) {
            log.warn("[AiVideoService] 指定模型不可用，继续使用供应商直连配置 - modelId: {}", modelId, ex);
            return AiModelDO.builder().id(modelId).model(provider.getCode()).build();
        }
    }

    private Long resolvePersistedModelId(AiModelDO model) {
        return model != null && model.getId() != null && model.getId() > 0 ? model.getId() : null;
    }

    private String buildSafeErrorMessage(Throwable throwable) {
        return buildSafeErrorMessage(throwable != null ? throwable.getMessage() : null);
    }

    private String buildSafeErrorMessage(String message) {
        return StrUtil.maxLength(StrUtil.blankToDefault(message, "处理失败"), MAX_ERROR_MESSAGE_LENGTH);
    }
}
