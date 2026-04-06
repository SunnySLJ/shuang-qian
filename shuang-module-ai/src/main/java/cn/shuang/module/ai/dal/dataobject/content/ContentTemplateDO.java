package cn.shuang.module.ai.dal.dataobject.content;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 内容模板 DO
 * <p>
 * 预定义的内容模板，用户可一键使用
 */
@TableName("content_template")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentTemplateDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 行业分类
     */
    private String category;

    /**
     * 模板标题
     */
    private String title;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 封面图 URL
     */
    private String coverImageUrl;

    /**
     * 演示视频 URL
     */
    private String demoVideoUrl;

    // ==================== 模板内容 ====================

    /**
     * 提示词模板
     */
    private String promptTemplate;

    /**
     * 脚本模板
     */
    private String scriptTemplate;

    /**
     * 视觉模板 (JSON)
     */
    private String visualTemplate;

    // ==================== 使用统计 ====================

    /**
     * 使用次数
     */
    private Integer useCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 是否精选
     */
    private Boolean featured;

    /**
     * 排序
     */
    private Integer sortOrder;
}
