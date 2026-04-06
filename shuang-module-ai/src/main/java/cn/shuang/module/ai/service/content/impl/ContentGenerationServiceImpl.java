package cn.shuang.module.ai.service.content.impl;

import cn.hutool.core.util.StrUtil;
import cn.shuang.framework.common.exception.ServiceException;
import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.framework.common.util.json.JsonUtils;
import cn.shuang.module.ai.controller.admin.content.vo.ContentGenerateReqVO;
import cn.shuang.module.ai.controller.admin.content.vo.ContentGenerateRespVO;
import cn.shuang.module.ai.controller.admin.content.vo.ContentGenerationPageReqVO;
import cn.shuang.module.ai.dal.dataobject.content.ContentGenerationRecordDO;
import cn.shuang.module.ai.dal.mysql.content.ContentGenerationRecordMapper;
import cn.shuang.module.ai.enums.ErrorCodeConstants;
import cn.shuang.module.ai.framework.content.context.ContentContext;
import cn.shuang.module.ai.framework.content.context.CriticResult;
import cn.shuang.module.ai.framework.content.context.ScriptResult;
import cn.shuang.module.ai.framework.content.context.TrendAnalysisResult;
import cn.shuang.module.ai.framework.content.result.ContentGenerationResult;
import cn.shuang.module.ai.service.content.ContentGenerationService;
import cn.shuang.module.ai.service.content.ContentOrchestratorService;
import cn.shuang.module.pay.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.shuang.module.ai.enums.ErrorCodeConstants.*;

/**
 * 内容生成 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentGenerationServiceImpl implements ContentGenerationService {

    private final ContentOrchestratorService orchestrator;
    private final ContentGenerationRecordMapper generationRecordMapper;
    private final WalletService walletService;

    /**
     * 内容生成所需积分（可通过配置调整）
     */
    private static final int POINTS_PER_GENERATION = 50;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContentGenerateRespVO generate(ContentGenerateReqVO reqVO, Long userId) {
        log.info("[ContentGeneration] 开始生成内容，用户 ID: {}, 类型：{}", userId, reqVO.getStyle());

        try {
            // 1. 扣减积分
            walletService.deductPoints(userId, POINTS_PER_GENERATION, "AI 内容生成 - " + reqVO.getStyle());

            // 2. 创建生成记录（状态：处理中）
            ContentGenerationRecordDO record = createGenerationRecord(reqVO, userId);
            generationRecordMapper.insert(record);

            // 3. 构建上下文并执行编排器
            ContentContext context = buildContext(reqVO, userId);
            ContentGenerationResult result = orchestrator.generate(context);

            // 4. 更新生成记录
            updateGenerationRecord(record.getId(), result);

            // 5. 转换为 Response VO
            ContentGenerateRespVO respVO = new ContentGenerateRespVO();
            respVO.setTaskId(record.getId());
            respVO.setStatus(result.getStatus());
            respVO.setVideoUrl(result.getVideoUrl());
            respVO.setThumbnailUrl(result.getThumbnailUrl());
            respVO.setScriptContent(result.getScriptContent());
            respVO.setSeoTitle(result.getSeoTitle());
            respVO.setSeoTags(result.getSeoTags());
            respVO.setCostPoints(POINTS_PER_GENERATION);
            respVO.setQualityScore(result.getQualityScore());
            respVO.setErrorMessage(result.getErrorMessage());
            respVO.setElapsedMs(result.getElapsedMs());

            log.info("[ContentGeneration] 生成完成，任务 ID: {}, 状态：{}", record.getId(), result.getStatus());

            return respVO;

        } catch (Exception e) {
            log.error("[ContentGeneration] 生成失败", e);
            throw new ServiceException(AI_CONTENT_GENERATE_FAIL.getCode(), e.getMessage());
        }
    }

    @Override
    public ContentGenerationRecordDO getGenerationDetail(Long taskId, Long userId) {
        ContentGenerationRecordDO record = generationRecordMapper.selectById(taskId);
        if (record == null) {
            throw new ServiceException(AI_CONTENT_NOT_EXISTS);
        }
        // 权限检查：只能查看自己的记录
        if (!record.getUserId().equals(userId)) {
            throw new ServiceException(AI_CONTENT_PERMISSION_DENIED);
        }
        return record;
    }

    @Override
    public cn.shuang.framework.common.pojo.PageResult<ContentGenerationRecordDO> getGenerationPage(ContentGenerationPageReqVO pageReqVO) {
        return generationRecordMapper.selectPage(
                pageReqVO.getUserId(),
                pageReqVO.getGenerationType(),
                pageReqVO.getStatus(),
                null); // createTime 暂时不传
    }

    @Override
    public java.util.List<ContentGenerationRecordDO> getUserGenerationList(Long userId, Integer limit) {
        return generationRecordMapper.selectByUserId(userId, limit);
    }

    // ==================== 私有方法 ====================

    /**
     * 创建生成记录
     */
    private ContentGenerationRecordDO createGenerationRecord(ContentGenerateReqVO reqVO, Long userId) {
        return ContentGenerationRecordDO.builder()
                .userId(userId)
                .generationType(determineGenerationType(reqVO))
                .inputText(reqVO.getUserInput())
                .inputImageUrl(reqVO.getInputImageUrl())
                .inputVideoUrl(reqVO.getReferenceVideoUrl())
                .templateId(reqVO.getTemplateId())
                .status(0) // 处理中
                .costPoints(POINTS_PER_GENERATION)
                .retryCount(0)
                .build();
    }

    /**
     * 判断生成类型
     */
    private Integer determineGenerationType(ContentGenerateReqVO reqVO) {
        if (StrUtil.isNotBlank(reqVO.getReferenceVideoUrl())) {
            return 1; // 爆款拆解
        }
        if (StrUtil.isNotBlank(reqVO.getInputImageUrl())) {
            return 3; // 图生视频
        }
        return 2; // 文生视频
    }

    /**
     * 构建内容上下文
     */
    private ContentContext buildContext(ContentGenerateReqVO reqVO, Long userId) {
        ContentContext context = new ContentContext();
        context.setUserId(userId);
        context.setUserInput(reqVO.getUserInput());
        context.setReferenceVideoUrl(reqVO.getReferenceVideoUrl());
        context.setInputImageUrl(reqVO.getInputImageUrl());
        context.setStyle(reqVO.getStyle());
        context.setCategory(reqVO.getCategory());
        context.setMaxRetryCount(3);

        if (reqVO.getQualityThreshold() != null) {
            context.setQualityThreshold(reqVO.getQualityThreshold());
        }

        return context;
    }

    /**
     * 更新生成记录
     */
    private void updateGenerationRecord(Long recordId, ContentGenerationResult result) {
        ContentGenerationRecordDO update = new ContentGenerationRecordDO();
        update.setId(recordId);
        update.setStatus(result.getStatus());
        update.setOutputUrl(result.getVideoUrl());
        update.setOutputThumbnail(result.getThumbnailUrl());
        update.setDurationMs(Math.toIntExact(result.getElapsedMs()));

        // 保存脚本内容
        if (result.getScript() != null) {
            ScriptResult script = result.getScript();
            update.setScriptContent(script.getFullScript());
        }

        // 保存 SEO 信息
        update.setSeoTitle(result.getSeoTitle());
        if (result.getSeoRecommendation() != null && result.getSeoRecommendation().getTags() != null) {
            update.setSeoTags(StrUtil.join(",", result.getSeoRecommendation().getTags()));
        }

        // 保存分析结果（JSON）
        if (result.getTrendAnalysis() != null) {
            update.setAnalysisResult(JsonUtils.toJsonString(result.getTrendAnalysis()));
        }

        // 错误信息
        if (StrUtil.isNotBlank(result.getErrorMessage())) {
            update.setErrorMessage(result.getErrorMessage());
        }

        generationRecordMapper.updateById(update);
    }
}
