package cn.shuang.module.agency.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 绑定代理请求 VO - 用户端
 *
 * @author shuang-pro
 */
@Schema(description = "用户端 - 绑定代理请求")
@Data
public class AppBindAgencyReqVO {

    @Schema(description = "上级代理邀请码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "邀请码不能为空")
    private String inviteCode;

}
