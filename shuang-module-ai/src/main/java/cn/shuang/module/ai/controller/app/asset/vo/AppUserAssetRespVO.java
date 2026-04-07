package cn.shuang.module.ai.controller.app.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户资产响应 VO
 *
 * @author shuang-pro
 */
@Schema(description = "用户 APP - 用户资产 Response VO")
@Data
public class AppUserAssetRespVO {

    @Schema(description = "资产 ID", example = "1")
    private Long id;

    @Schema(description = "资产类型：1-图片，2-视频，3-音频", example = "1")
    private Integer assetType;

    @Schema(description = "资源URL", example = "https://example.com/asset.jpg")
    private String resourceUrl;

    @Schema(description = "缩略图URL", example = "https://example.com/thumb.jpg")
    private String thumbnailUrl;

    @Schema(description = "标题", example = "AI生成的图片")
    private String title;

    @Schema(description = "描述", example = "一只可爱的橘猫")
    private String description;

    @Schema(description = "文件大小(字节)", example = "1024000")
    private Long fileSize;

    @Schema(description = "时长(秒，视频/音频)", example = "10")
    private Integer duration;

    @Schema(description = "宽度", example = "1024")
    private Integer width;

    @Schema(description = "高度", example = "1024")
    private Integer height;

    @Schema(description = "分组ID", example = "1")
    private Long groupId;

    @Schema(description = "分组名称", example = "默认分组")
    private String groupName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}