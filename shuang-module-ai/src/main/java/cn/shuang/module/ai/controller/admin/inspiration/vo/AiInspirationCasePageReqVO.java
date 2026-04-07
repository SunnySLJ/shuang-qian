package cn.shuang.module.ai.controller.admin.inspiration.vo;

import cn.shuang.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.shuang.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * AI 灵感案例分页 Request VO
 *
 * @author shuang-pro
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AiInspirationCasePageReqVO extends PageParam {

    @Schema(description = "类型：banana/veo/grok/seedance", example = "veo")
    private String type;

    @Schema(description = "分类 ID", example = "5")
    private Integer categoryId;

    @Schema(description = "案例标题", example = "活字印刷")
    private String title;

    @Schema(description = "是否精选", example = "true")
    private Boolean featured;

    @Schema(description = "创建时间 (开始)", example = "2024-01-01 00:00:00")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTimeStartTime;

    @Schema(description = "创建时间 (结束)", example = "2024-12-31 23:59:59")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTimeEndTime;

}