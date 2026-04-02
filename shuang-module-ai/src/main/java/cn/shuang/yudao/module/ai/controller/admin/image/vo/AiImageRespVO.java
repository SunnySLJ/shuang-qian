package cn.shuang.module.ai.controller.admin.image.vo;

import cn.shuang.module.ai.framework.ai.core.model.midjourney.api.MidjourneyApi;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - AI 绘画 Response VO")
@Data
public class AiImageRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "OpenAI")
    private String platform;  // 参见 AiPlatformEnum 枚举

    @Schema(description = "模型", requiredMode = Schema.RequiredMode.REQUIRED, example = "stable-diffusion-v1-6")
    private String model;

    @Schema(description = "提示词", requiredMode = Schema.RequiredMode.REQUIRED, example = "南极的小企鹅")
    private String prompt;

    @Schema(description = "图片宽度", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Integer width;

    @Schema(description = "图片高度", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Integer height;

    @Schema(description = "绘画状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer status;

    @Schema(description = "是否发布", requiredMode = Schema.RequiredMode.REQUIRED, example = "public")
    private Boolean publicStatus;

    @Schema(description = "图片地址", example = "https://www.iocoder.cn/1.png")
    private String picUrl;

    @Schema(description = "绘画错误信息", example = "图片错误信息")
    private String errorMessage;

    @Schema(description = "绘制参数")
    private Map<String, String> options;

    @Schema(description = "mj buttons 按钮")
    private List<MidjourneyApi.Button> buttons;

    @Schema(description = "完成时间")
    private LocalDateTime finishTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "生成类型：1-生图，2-文生视频，3-图生视频，4-黄金 6 秒，5-AI 混剪，6-视频拆解 - 提取脚本，7-视频拆解 - 分析元素，8-视频拆解 - 生成提示词")
    private Integer generationType;

    @Schema(description = "输入图片 URL（图生视频用）")
    private String inputImageUrl;

    @Schema(description = "输入视频 URL（视频拆解用）")
    private String inputVideoUrl;

    @Schema(description = "输出 URL（视频或图片）")
    private String outputUrl;

    @Schema(description = "视频封面 URL")
    private String coverUrl;

    @Schema(description = "视频时长（秒）")
    private Integer duration;

    @Schema(description = "任务编号")
    private String taskId;

}
