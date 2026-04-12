package cn.shuang.module.ai.controller.app.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * AI 视频生成请求 VO
 *
 * @author shuang-pro
 */
@Data
public class AiVideoGenerateReqVO {

    /**
     * 提示词
     */
    @NotBlank(message = "提示词不能为空")
    private String prompt;

    /**
     * 模型 ID
     */
    private Long modelId;

    /**
     * 视频时长（秒）
     */
    private Integer duration = 5;

    /**
     * 画面比例
     */
    private String ratio;

    /**
     * 生成风格类型
     */
    private String styleType;

    /**
     * 视觉风格
     */
    private String visualStyle;

    /**
     * 运镜方式
     */
    private String cameraMovement;

    /**
     * 画面重点
     */
    private String shotFocus;

    /**
     * 质量等级
     */
    private String qualityLevel;

    /**
     * 参考图片 URL（图生视频用）
     */
    private String imageUrl;

    /**
     * 客户端请求 ID（用于幂等）
     */
    @Schema(description = "客户端请求 ID，用于幂等控制")
    private String clientId;

}
