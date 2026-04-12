package cn.shuang.module.ai.controller.app.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * AI 视频分析请求 VO
 *
 * @author shuang-pro
 */
@Data
public class AiVideoAnalyzeReqVO {

    /**
     * 视频 URL
     */
    @Schema(description = "视频 URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "视频 URL 不能为空")
    private String videoUrl;

    /**
     * 模型 ID
     */
    @Schema(description = "模型 ID，兼容旧链路可选")
    private Long modelId;

    /**
     * 视频拆解供应商
     */
    @Schema(description = "视频拆解供应商，可选值：aliyun、doubao、wumo")
    private String provider;

    /**
     * 客户端请求 ID（用于幂等）
     */
    @Schema(description = "客户端请求 ID，用于幂等控制")
    private String clientId;

}
