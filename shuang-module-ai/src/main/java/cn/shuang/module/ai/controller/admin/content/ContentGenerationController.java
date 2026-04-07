package cn.shuang.module.ai.controller.admin.content;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.module.ai.controller.admin.content.vo.*;
import cn.shuang.module.ai.dal.dataobject.content.ContentGenerationRecordDO;
import cn.shuang.module.ai.service.content.ContentGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static cn.shuang.framework.common.pojo.CommonResult.success;
import static cn.shuang.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - AI 内容生成")
@RestController
@RequestMapping("/ai/content")
@Slf4j
@ConditionalOnProperty(value = "yudao.ai.content-generation.enable", havingValue = "true")
public class ContentGenerationController {

    @Resource
    private ContentGenerationService contentGenerationService;

    @Operation(summary = "生成内容", description = "支持文生视频、图生视频、爆款拆解等")
    @PostMapping("/generate")
    @PreAuthorize("@ss.hasPermission('ai:content:generate')")
    public CommonResult<ContentGenerateRespVO> generate(@Valid @RequestBody ContentGenerateReqVO reqVO) {
        return success(contentGenerationService.generate(reqVO, getLoginUserId()));
    }

    @Operation(summary = "查询生成详情")
    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('ai:content:query')")
    @Parameter(name = "taskId", description = "任务 ID", required = true, example = "1024")
    public CommonResult<ContentDetailRespVO> getGenerationDetail(
            @RequestParam("taskId") Long taskId) {
        ContentGenerationRecordDO record = contentGenerationService.getGenerationDetail(taskId, getLoginUserId());
        return success(ContentGenerationConvert.INSTANCE.convertDetail(record));
    }

    @Operation(summary = "分页查询生成记录")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('ai:content:query')")
    public CommonResult<PageResult<ContentDetailRespVO>> getGenerationPage(
            @Valid ContentGenerationPageReqVO pageReqVO) {
        // TODO: 实现分页查询
        return success(PageResult.empty());
    }

    @Operation(summary = "查询用户的生成记录列表")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('ai:content:query')")
    @Parameter(name = "limit", description = "数量限制", example = "10")
    public CommonResult<java.util.List<ContentDetailRespVO>> getUserGenerationList(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        java.util.List<ContentGenerationRecordDO> records =
                contentGenerationService.getUserGenerationList(getLoginUserId(), limit);
        return success(ContentGenerationConvert.INSTANCE.convertDetailList(records));
    }

    @Operation(summary = "轮询查询生成状态", description = "用于前端轮询任务生成状态")
    @GetMapping("/status")
    @Parameter(name = "taskId", description = "任务 ID", required = true, example = "1024")
    public CommonResult<ContentGenerateRespVO> getGenerationStatus(
            @RequestParam("taskId") Long taskId) {
        ContentGenerationRecordDO record = contentGenerationService.getGenerationDetail(taskId, getLoginUserId());
        ContentGenerateRespVO respVO = new ContentGenerateRespVO();
        respVO.setTaskId(record.getId());
        respVO.setStatus(record.getStatus());
        respVO.setErrorMessage(record.getErrorMessage());
        respVO.setElapsedMs(record.getDurationMs() != null ? record.getDurationMs().longValue() : null);
        return success(respVO);
    }
}
