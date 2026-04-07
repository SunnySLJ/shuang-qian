package cn.shuang.module.ai.controller.admin.content.vo;

import cn.shuang.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - AI 内容生成记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ContentGenerationPageReqVO extends PageParam {

    @Schema(description = "用户 ID", example = "1024")
    private Long userId;

    @Schema(description = "生成类型：1-爆款拆解，2-文生视频，3-图生视频，4-黄金 6 秒，5-AI 混剪", example = "1")
    private Integer generationType;

    @Schema(description = "状态：0-处理中，1-完成，2-失败", example = "1")
    private Integer status;
}
