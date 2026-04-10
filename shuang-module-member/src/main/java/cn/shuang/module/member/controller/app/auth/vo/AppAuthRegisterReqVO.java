package cn.shuang.module.member.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "用户 APP - 手机号 + 密码注册 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppAuthRegisterReqVO {

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "buzhidao")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为 6-20 位")
    private String password;

    @Schema(description = "确认密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "buzhidao")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @Schema(description = "邀请码（选填）", example = "ABC123")
    private String inviteCode;

}
