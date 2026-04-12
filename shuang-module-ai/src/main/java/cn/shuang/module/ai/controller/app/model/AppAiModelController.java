package cn.shuang.module.ai.controller.app.model;

import cn.shuang.framework.common.enums.CommonStatusEnum;
import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.module.ai.controller.admin.model.vo.model.AiModelRespVO;
import cn.shuang.module.ai.dal.dataobject.model.AiModelDO;
import cn.shuang.module.ai.service.model.AiModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.shuang.framework.common.pojo.CommonResult.success;
import static cn.shuang.framework.common.util.collection.CollectionUtils.convertList;

@RestController
@RequestMapping("/ai/model")
@Validated
@Tag(name = "用户 APP - AI 模型")
public class AppAiModelController {

    @Resource
    private AiModelService modelService;

    @GetMapping("/simple-list")
    @Operation(summary = "获得启用中的模型简列表")
    @Parameter(name = "type", description = "模型类型：2 图片，4 视频", required = true, example = "2")
    @Parameter(name = "platform", description = "平台，可选", example = "WuMo")
    public CommonResult<List<AiModelRespVO>> getSimpleList(
            @RequestParam("type") Integer type,
            @RequestParam(value = "platform", required = false) String platform) {
        List<AiModelDO> list = modelService.getModelListByStatusAndType(
                CommonStatusEnum.ENABLE.getStatus(), type, platform);
        return success(convertList(list, model -> {
            AiModelRespVO vo = new AiModelRespVO();
            vo.setId(model.getId());
            vo.setName(model.getName());
            vo.setModel(model.getModel());
            vo.setPlatform(model.getPlatform());
            vo.setType(model.getType());
            vo.setSort(model.getSort());
            vo.setStatus(model.getStatus());
            return vo;
        }));
    }
}
