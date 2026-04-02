package cn.shuang.module.ai.controller.app.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * AI 视频分析请求 VO
 *
 * @author shuang-pro
 */
@Data
public class AiVideoAnalyzeReqVO {

    /**
     * 用户 ID
     */
    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    /**
     * 视频 URL
     */
    @NotBlank(message = "视频 URL 不能为空")
    private String videoUrl;

    /**
     * 模型 ID
     */
    @NotNull(message = "模型 ID 不能为空")
    private Long modelId;

}
