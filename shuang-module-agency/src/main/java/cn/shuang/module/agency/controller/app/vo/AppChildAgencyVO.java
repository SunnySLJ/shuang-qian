package cn.shuang.module.agency.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 下级代理 VO - 用户端
 *
 * @author shuang-pro
 */
@Schema(description = "用户端 - 下级代理信息")
@Data
public class AppChildAgencyVO {

    @Schema(description = "用户 ID", example = "100")
    private Long userId;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "代理等级", example = "2")
    private Integer level;

    @Schema(description = "是否代理", example = "true")
    private Boolean agencyEnabled;

    @Schema(description = "累计获得积分", example = "10000")
    private Integer totalPoints;

    @Schema(description = "已分配积分", example = "5000")
    private Integer distributedPoints;

    @Schema(description = "绑定时间", example = "2026-01-01 12:00:00")
    private String createTime;

}
