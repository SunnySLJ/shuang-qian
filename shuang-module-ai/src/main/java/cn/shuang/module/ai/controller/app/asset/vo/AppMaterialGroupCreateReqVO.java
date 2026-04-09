package cn.shuang.module.ai.controller.app.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建素材分组请求 VO
 *
 * @author shuang-pro
 */
@Schema(description = "用户 APP - 创建素材分组 Request VO")
@Data
public class AppMaterialGroupCreateReqVO {

    @Schema(description = "分组名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "我的收藏")
    @NotBlank(message = "分组名称不能为空")
    private String name;

}