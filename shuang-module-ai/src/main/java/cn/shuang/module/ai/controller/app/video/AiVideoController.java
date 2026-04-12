package cn.shuang.module.ai.controller.app.video;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.framework.common.pojo.PageParam;
import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.framework.idempotent.core.annotation.Idempotent;
import cn.shuang.framework.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import cn.shuang.module.ai.controller.app.video.vo.AiVideoAnalyzeReqVO;
import cn.shuang.module.ai.controller.app.video.vo.AiVideoGenerateReqVO;
import cn.shuang.module.ai.controller.app.video.vo.AiVideoPromptGenerateReqVO;
import cn.shuang.module.ai.controller.app.video.vo.AiVideoPromptGenerateRespVO;
import cn.shuang.module.ai.controller.app.video.vo.AiVideoResolveRespVO;
import cn.shuang.module.ai.controller.admin.image.vo.AiImageRespVO;
import cn.shuang.module.ai.dal.dataobject.image.AiImageDO;
import cn.shuang.module.ai.service.prompt.AppPromptBuildUtils;
import cn.shuang.module.ai.service.video.AiVideoService;
import cn.shuang.module.ai.service.video.VideoPromptGenerateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.shuang.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.shuang.framework.common.pojo.CommonResult.success;

/**
 * AI 视频生成 Controller
 *
 * @author shuang-pro
 */
@RestController
@RequestMapping("/ai/video")
@Validated
@Tag(name = "AI 视频生成")
@Slf4j
public class AiVideoController {

    @Resource
    private AiVideoService videoService;

    @Resource
    private VideoPromptGenerateService videoPromptGenerateService;

    @PostMapping("/text-to-video")
    @Operation(summary = "文生视频")
    @Idempotent(keyArg = "'ai_video_txt2vid_' + T(cn.shuang.framework.security.core.util.SecurityFrameworkUtils).getLoginUserId() + '_' + #root.clientId",
                timeout = 30, keyResolver = ExpressionIdempotentKeyResolver.class)
    public CommonResult<Long> textToVideo(@RequestBody @Valid AiVideoGenerateReqVO reqVO) {
        Long userId = getLoginUserId();
        Map<String, Object> options = buildVideoOptions(reqVO);
        Long id = videoService.textToVideo(userId,
                AppPromptBuildUtils.buildVideoPrompt(reqVO.getPrompt(), reqVO.getDuration(), options),
                reqVO.getModelId(), reqVO.getDuration(), options);
        return success(id);
    }

    @PostMapping("/image-to-video")
    @Operation(summary = "图生视频")
    @Idempotent(keyArg = "'ai_video_img2vid_' + T(cn.shuang.framework.security.core.util.SecurityFrameworkUtils).getLoginUserId() + '_' + #root.clientId",
                timeout = 30, keyResolver = ExpressionIdempotentKeyResolver.class)
    public CommonResult<Long> imageToVideo(@RequestBody @Valid AiVideoGenerateReqVO reqVO) {
        Long userId = getLoginUserId();
        Map<String, Object> options = buildVideoOptions(reqVO);
        Long id = videoService.imageToVideo(userId, reqVO.getImageUrl(),
                AppPromptBuildUtils.buildVideoPrompt(reqVO.getPrompt(), reqVO.getDuration(), options),
                reqVO.getModelId(), reqVO.getDuration(), options);
        return success(id);
    }

    @PostMapping("/golden-6s")
    @Operation(summary = "黄金 6 秒拼接")
    @Idempotent(keyArg = "'ai_video_golden6s_' + T(cn.shuang.framework.security.core.util.SecurityFrameworkUtils).getLoginUserId() + '_' + #root.clientId",
                timeout = 30, keyResolver = ExpressionIdempotentKeyResolver.class)
    public CommonResult<Long> golden6s(@RequestBody @Valid AiVideoGenerateReqVO reqVO) {
        Long userId = getLoginUserId();
        Long id = videoService.golden6s(userId, reqVO.getPrompt(), reqVO.getModelId());
        return success(id);
    }

    @PostMapping("/ai-mix")
    @Operation(summary = "AI 超级混剪")
    @Idempotent(keyArg = "'ai_video_mix_' + T(cn.shuang.framework.security.core.util.SecurityFrameworkUtils).getLoginUserId() + '_' + #root.clientId",
                timeout = 30, keyResolver = ExpressionIdempotentKeyResolver.class)
    public CommonResult<Long> aiMix(@RequestBody @Valid AiVideoGenerateReqVO request) {
        Long userId = getLoginUserId();
        Long id = videoService.aiMix(userId, request.getPrompt(), request.getModelId());
        return success(id);
    }

    @PostMapping("/extract-script")
    @Operation(summary = "视频拆解 - 提取脚本")
    @Idempotent(keyArg = "'video_analyze_' + T(cn.shuang.module.ai.service.video.analysis.VideoAnalyzeIdempotentKeyResolver).buildKey(#root.videoUrl, T(cn.hutool.core.util.StrUtil).blankToDefault(#root.provider, 'aliyun'), 'SCRIPT', T(cn.hutool.core.util.StrUtil).blankToDefault(#root.clientId, ''))",
                timeout = 30, keyResolver = ExpressionIdempotentKeyResolver.class)
    public CommonResult<Long> extractScript(@RequestBody @Valid AiVideoAnalyzeReqVO request) {
        Long userId = getLoginUserId();
        Long id = videoService.extractScript(userId, request.getVideoUrl(), request.getModelId(), request.getProvider());
        return success(id);
    }

    @PostMapping("/analyze-elements")
    @Operation(summary = "视频拆解 - 分析元素")
    @Idempotent(keyArg = "'video_analyze_' + T(cn.shuang.module.ai.service.video.analysis.VideoAnalyzeIdempotentKeyResolver).buildKey(#root.videoUrl, T(cn.hutool.core.util.StrUtil).blankToDefault(#root.provider, 'aliyun'), 'ELEMENTS', T(cn.hutool.core.util.StrUtil).blankToDefault(#root.clientId, ''))",
                timeout = 30, keyResolver = ExpressionIdempotentKeyResolver.class)
    public CommonResult<Long> analyzeElements(@RequestBody @Valid AiVideoAnalyzeReqVO request) {
        Long userId = getLoginUserId();
        Long id = videoService.analyzeElements(userId, request.getVideoUrl(), request.getModelId(), request.getProvider());
        return success(id);
    }

    @PostMapping("/generate-prompt")
    @Operation(summary = "视频拆解 - 生成提示词")
    @Idempotent(keyArg = "'video_analyze_' + T(cn.shuang.module.ai.service.video.analysis.VideoAnalyzeIdempotentKeyResolver).buildKey(#root.videoUrl, T(cn.hutool.core.util.StrUtil).blankToDefault(#root.provider, 'aliyun'), 'PROMPT', T(cn.hutool.core.util.StrUtil).blankToDefault(#root.clientId, ''))",
                timeout = 30, keyResolver = ExpressionIdempotentKeyResolver.class)
    public CommonResult<Long> generatePrompt(@RequestBody @Valid AiVideoAnalyzeReqVO request) {
        Long userId = getLoginUserId();
        Long id = videoService.generatePrompt(userId, request.getVideoUrl(), request.getModelId(), request.getProvider());
        return success(id);
    }

    @PostMapping("/generate-prompt-from-images")
    @Operation(summary = "基于多张图片生成视频提示词")
    public CommonResult<AiVideoPromptGenerateRespVO> generatePromptFromImages(
            @RequestBody @Valid AiVideoPromptGenerateReqVO request) {
        return success(videoPromptGenerateService.generateFromImages(
                request.getImageUrls(), request.getUserPrompt(), request.getPromptLevel()));
    }

    @GetMapping("/resolve-link")
    @Operation(summary = "解析分享文案中的有效视频链接")
    public CommonResult<AiVideoResolveRespVO> resolveLink(
            @Parameter(description = "分享文案、分享链接或视频直链") @RequestParam String rawText) {
        return success(videoService.resolveVideoUrl(rawText));
    }

    @GetMapping("/page")
    @Operation(summary = "获取视频生成记录列表")
    public CommonResult<PageResult<AiImageRespVO>> getVideoPage(@Validated PageParam pageParam) {
        Long userId = getLoginUserId();
        cn.shuang.module.ai.controller.admin.image.vo.AiImagePageReqVO reqVO =
                new cn.shuang.module.ai.controller.admin.image.vo.AiImagePageReqVO();
        reqVO.setPageNo(pageParam.getPageNo());
        reqVO.setPageSize(pageParam.getPageSize());
        PageResult<AiImageDO> pageResult = videoService.getVideoPage(userId, reqVO);
        List<AiImageRespVO> list = pageResult.getList().stream().map(image -> {
            AiImageRespVO vo = new AiImageRespVO();
            vo.setId(image.getId());
            vo.setUserId(image.getUserId());
            vo.setPrompt(image.getPrompt());
            vo.setPlatform(image.getPlatform());
            vo.setModel(image.getModel());
            vo.setStatus(image.getStatus());
            vo.setPicUrl(image.getPicUrl());
            vo.setOutputUrl(image.getOutputUrl());
            vo.setCoverUrl(image.getCoverUrl());
            vo.setGenerationType(image.getGenerationType());
            vo.setInputVideoUrl(image.getInputVideoUrl());
            vo.setInputImageUrl(image.getInputImageUrl());
            vo.setErrorMessage(image.getErrorMessage());
            vo.setCreateTime(image.getCreateTime());
            vo.setFinishTime(image.getFinishTime());
            vo.setDuration(image.getDuration());
            vo.setBizOptions(image.getOptions());
            vo.setGenerationType(image.getGenerationType());
            return vo;
        }).toList();
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

    @GetMapping("/detail")
    @Operation(summary = "获取视频详情")
    public CommonResult<AiImageRespVO> getVideoDetail(
            @Parameter(description = "记录 ID") @RequestParam Long id) {
        Long userId = getLoginUserId();
        AiImageDO image = videoService.getVideoDetail(id);
        if (image == null || !Objects.equals(image.getUserId(), userId)) {
            return CommonResult.error(1, "记录不存在");
        }
        AiImageRespVO vo = new AiImageRespVO();
        vo.setId(image.getId());
        vo.setUserId(image.getUserId());
        vo.setPrompt(image.getPrompt());
        vo.setStatus(image.getStatus());
        vo.setPicUrl(image.getPicUrl());
        vo.setOutputUrl(image.getOutputUrl());
        vo.setCoverUrl(image.getCoverUrl());
        vo.setGenerationType(image.getGenerationType());
        vo.setErrorMessage(image.getErrorMessage());
        vo.setInputVideoUrl(image.getInputVideoUrl());
        vo.setDuration(image.getDuration());
        vo.setCreateTime(image.getCreateTime());
        vo.setFinishTime(image.getFinishTime());
        vo.setTaskId(image.getTaskId());
        vo.setBizOptions(image.getOptions());
        return success(vo);
    }

    @PostMapping("/sync-status")
    @Operation(summary = "同步视频状态")
    public CommonResult<Boolean> syncVideoStatus(
            @Parameter(description = "记录 ID") @RequestParam Long id) {
        boolean result = videoService.syncVideoStatus(id);
        return success(result);
    }

    private Map<String, Object> buildVideoOptions(AiVideoGenerateReqVO reqVO) {
        Map<String, Object> options = new LinkedHashMap<>();
        putIfPresent(options, "ratio", reqVO.getRatio());
        putIfPresent(options, "styleType", reqVO.getStyleType());
        putIfPresent(options, "visualStyle", reqVO.getVisualStyle());
        putIfPresent(options, "cameraMovement", reqVO.getCameraMovement());
        putIfPresent(options, "shotFocus", reqVO.getShotFocus());
        putIfPresent(options, "qualityLevel", reqVO.getQualityLevel());
        return options;
    }

    private void putIfPresent(Map<String, Object> options, String key, String value) {
        if (value != null && !value.isBlank()) {
            options.put(key, value);
        }
    }
}
