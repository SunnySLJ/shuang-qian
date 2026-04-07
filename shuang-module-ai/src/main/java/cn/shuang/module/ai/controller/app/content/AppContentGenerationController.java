package cn.shuang.module.ai.controller.app.content;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.module.ai.controller.admin.content.vo.*;
import cn.shuang.module.ai.dal.dataobject.content.ContentGenerationRecordDO;
import cn.shuang.module.ai.service.content.ContentGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.shuang.framework.common.pojo.CommonResult.success;
import static cn.shuang.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - AI 内容生成")
@RestController
@RequestMapping("/app/ai/content")
@Slf4j
public class AppContentGenerationController {

    @Resource
    private ContentGenerationService contentGenerationService;

    @Operation(summary = "生成内容", description = "支持文生视频、图生视频、爆款拆解等")
    @PostMapping("/generate")
    public CommonResult<ContentGenerateRespVO> generate(@Valid @RequestBody ContentGenerateReqVO reqVO) {
        return success(contentGenerationService.generate(reqVO, getLoginUserId()));
    }

    @Operation(summary = "查询生成详情")
    @GetMapping("/get")
    @Parameter(name = "taskId", description = "任务 ID", required = true, example = "1024")
    public CommonResult<ContentDetailRespVO> getGenerationDetail(
            @RequestParam("taskId") Long taskId) {
        ContentGenerationRecordDO record = contentGenerationService.getGenerationDetail(taskId, getLoginUserId());
        return success(ContentGenerationConvert.INSTANCE.convertDetail(record));
    }

    @Operation(summary = "查询我的生成记录列表")
    @GetMapping("/list")
    @Parameter(name = "limit", description = "数量限制", example = "10")
    public CommonResult<List<ContentDetailRespVO>> getMyGenerationList(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<ContentGenerationRecordDO> records =
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
