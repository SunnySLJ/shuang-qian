package cn.shuang.module.ai.controller.app.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * AI 视频生成请求 VO
 *
 * @author shuang-pro
 */
@Data
public class AiVideoGenerateReqVO {

    /**
     * 用户 ID
     */
    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    /**
     * 提示词
     */
    @NotBlank(message = "提示词不能为空")
    private String prompt;

    /**
     * 模型 ID
     */
    @NotNull(message = "模型 ID 不能为空")
    private Long modelId;

    /**
     * 视频时长（秒）
     */
    private Integer duration = 5;

    /**
     * 参考图片 URL（图生视频用）
     */
    private String imageUrl;

}
