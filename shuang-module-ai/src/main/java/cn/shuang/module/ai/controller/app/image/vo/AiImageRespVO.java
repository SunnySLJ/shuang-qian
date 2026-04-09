package cn.shuang.module.ai.controller.app.image.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 图片响应 VO
 *
 * @author shuang-pro
 */
@Schema(description = "用户 APP - AI 图片 Response VO")
@Data
public class AiImageRespVO {

    @Schema(description = "图片 ID", example = "1")
    private Long id;

    @Schema(description = "提示词", example = "一只可爱的橘猫")
    private String prompt;

    @Schema(description = "参考图片URL", example = "https://example.com/ref.jpg")
    private String referenceImageUrl;

    @Schema(description = "生成的图片URL", example = "https://example.com/output.jpg")
    private String outputUrl;

    @Schema(description = "缩略图URL", example = "https://example.com/thumb.jpg")
    private String thumbnailUrl;

    @Schema(description = "状态：0-处理中，1-完成，2-失败", example = "1")
    private Integer status;

    @Schema(description = "错误信息", example = "生成失败")
    private String errorMessage;

    @Schema(description = "消耗积分", example = "4")
    private Integer costPoints;

    @Schema(description = "生成类型：1-文生图，2-图生图", example = "1")
    private Integer generationType;

    @Schema(description = "宽度", example = "1024")
    private Integer width;

    @Schema(description = "高度", example = "1024")
    private Integer height;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}