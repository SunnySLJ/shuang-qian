package cn.shuang.module.ai.controller.app.video;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.framework.common.pojo.PageParam;
import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.module.ai.controller.app.video.vo.AiVideoAnalyzeReqVO;
import cn.shuang.module.ai.controller.app.video.vo.AiVideoGenerateReqVO;
import cn.shuang.module.ai.controller.admin.image.vo.AiImageRespVO;
import cn.shuang.module.ai.dal.dataobject.image.AiImageDO;
import cn.shuang.module.ai.service.video.AiVideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

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
public class AiVideoController {

    @Resource
    private AiVideoService videoService;

    @PostMapping("/text-to-video")
    @Operation(summary = "文生视频")
    public CommonResult<Long> textToVideo(@RequestBody @Valid AiVideoGenerateReqVO reqVO) {
        Long id = videoService.textToVideo(reqVO.getUserId(), reqVO.getPrompt(), reqVO.getModelId(), reqVO.getDuration());
        return success(id);
    }

    @PostMapping("/image-to-video")
    @Operation(summary = "图生视频")
    public CommonResult<Long> imageToVideo(@RequestBody @Valid AiVideoGenerateReqVO reqVO) {
        Long id = videoService.imageToVideo(reqVO.getUserId(), reqVO.getImageUrl(), reqVO.getPrompt(), reqVO.getModelId(), reqVO.getDuration());
        return success(id);
    }

    @PostMapping("/golden-6s")
    @Operation(summary = "黄金 6 秒拼接")
    public CommonResult<Long> golden6s(@RequestBody @Valid AiVideoGenerateReqVO reqVO) {
        Long id = videoService.golden6s(reqVO.getUserId(), reqVO.getPrompt(), reqVO.getModelId());
        return success(id);
    }

    @PostMapping("/ai-mix")
    @Operation(summary = "AI 超级混剪")
    public CommonResult<Long> aiMix(@RequestBody @Valid AiVideoGenerateReqVO reqVO) {
        Long id = videoService.aiMix(reqVO.getUserId(), reqVO.getPrompt(), reqVO.getModelId());
        return success(id);
    }

    @PostMapping("/extract-script")
    @Operation(summary = "视频拆解 - 提取脚本")
    public CommonResult<Long> extractScript(@RequestBody @Valid AiVideoAnalyzeReqVO reqVO) {
        Long id = videoService.extractScript(reqVO.getUserId(), reqVO.getVideoUrl(), reqVO.getModelId());
        return success(id);
    }

    @PostMapping("/analyze-elements")
    @Operation(summary = "视频拆解 - 分析元素")
    public CommonResult<Long> analyzeElements(@RequestBody @Valid AiVideoAnalyzeReqVO reqVO) {
        Long id = videoService.analyzeElements(reqVO.getUserId(), reqVO.getVideoUrl(), reqVO.getModelId());
        return success(id);
    }

    @PostMapping("/generate-prompt")
    @Operation(summary = "视频拆解 - 生成提示词")
    public CommonResult<Long> generatePrompt(@RequestBody @Valid AiVideoAnalyzeReqVO reqVO) {
        Long id = videoService.generatePrompt(reqVO.getUserId(), reqVO.getVideoUrl(), reqVO.getModelId());
        return success(id);
    }

    @GetMapping("/page")
    @Operation(summary = "获取视频生成记录列表")
    public CommonResult<PageResult<AiImageRespVO>> getVideoPage(
            @Parameter(description = "用户 ID") @RequestParam Long userId,
            @Validated PageParam pageParam) {
        PageResult<AiImageDO> pageResult = videoService.getVideoPage(userId, new cn.shuang.module.ai.controller.admin.image.vo.AiImagePageReqVO());
        List<AiImageRespVO> list = pageResult.getList().stream().map(image -> {
            AiImageRespVO vo = new AiImageRespVO();
            vo.setId(image.getId());
            vo.setPrompt(image.getPrompt());
            vo.setStatus(image.getStatus());
            vo.setPicUrl(image.getPicUrl());
            vo.setOutputUrl(image.getOutputUrl());
            vo.setGenerationType(image.getGenerationType());
            return vo;
        }).toList();
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

    @GetMapping("/detail")
    @Operation(summary = "获取视频详情")
    public CommonResult<AiImageRespVO> getVideoDetail(
            @Parameter(description = "记录 ID") @RequestParam Long id) {
        AiImageDO image = videoService.getVideoDetail(id);
        if (image == null) {
            return CommonResult.error(1, "记录不存在");
        }
        AiImageRespVO vo = new AiImageRespVO();
        vo.setId(image.getId());
        vo.setPrompt(image.getPrompt());
        vo.setStatus(image.getStatus());
        vo.setPicUrl(image.getPicUrl());
        vo.setOutputUrl(image.getOutputUrl());
        vo.setGenerationType(image.getGenerationType());
        vo.setErrorMessage(image.getErrorMessage());
        return success(vo);
    }

    @PostMapping("/sync-status")
    @Operation(summary = "同步视频状态")
    public CommonResult<Boolean> syncVideoStatus(
            @Parameter(description = "记录 ID") @RequestParam Long id) {
        boolean result = videoService.syncVideoStatus(id);
        return success(result);
    }

}
