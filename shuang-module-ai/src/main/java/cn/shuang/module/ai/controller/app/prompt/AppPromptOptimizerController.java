package cn.shuang.module.ai.controller.app.prompt;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.module.ai.controller.app.prompt.vo.AppPromptOptimizeReqVO;
import cn.shuang.module.ai.service.prompt.PromptOptimizeService;
import cn.shuang.module.ai.service.prompt.dto.PromptOptimizeResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.shuang.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/ai/prompt")
@Validated
@Tag(name = "App 端 - Prompt 优化")
public class AppPromptOptimizerController {

    @Resource
    private PromptOptimizeService promptOptimizeService;

    @PostMapping("/optimize/hot-video")
    @Operation(summary = "爆款拆解提示词优化")
    public CommonResult<PromptOptimizeResult> optimizeHotVideo(@RequestBody @Valid AppPromptOptimizeReqVO reqVO) {
        return success(promptOptimizeService.optimizeHotVideoPrompt(
                reqVO.getRawPrompt(), reqVO.getIndustry(), reqVO.getPlatform(), reqVO.getTargetModel()));
    }
}
