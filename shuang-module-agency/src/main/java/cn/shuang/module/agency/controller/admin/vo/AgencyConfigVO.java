package cn.shuang.module.agency.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 代理配置 VO
 * <p>
 * 用于管理代理系统的各项配置，包括：
 * - 代理级别配置（一级代理、二级代理的要求和权益）
 * - 分成比例配置（一级代理 20%、二级代理 8%）
 * - 升级条件配置（直推人数要求、代理费金额）
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

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    /**
     * 检查是否启用
     *
     * @return 是否启用
     */
    public boolean isEnabled() {
        return enabled != null && enabled;
    }

}
