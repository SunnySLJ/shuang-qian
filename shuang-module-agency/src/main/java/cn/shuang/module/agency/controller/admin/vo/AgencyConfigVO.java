package cn.shuang.module.agency.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 代理配置 VO
 *
 * @author shuang-pro
 */
@Schema(description = "管理后台 - 代理配置")
@Data
public class AgencyConfigVO {

    @Schema(description = "配置键", requiredMode = Schema.RequiredMode.REQUIRED, example = "level1_commission_rate")
    private String configKey;

    @Schema(description = "配置值", requiredMode = Schema.RequiredMode.REQUIRED, example = "2000")
    private String configValue;

    @Schema(description = "描述", example = "一级代理分成比例")
    private String description;

}
