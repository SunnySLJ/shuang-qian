package cn.shuang.module.ai.dal.dataobject.tutorial;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 教程视频 DO
 * <p>
 * 用于存储教程视频信息，包括视频名称、封面、URL、时长等
 *
 * @author shuang-pro
 */
@TableName(value = "ai_tutorial_video", autoResultMap = true)
@KeySequence("ai_tutorial_video_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AiTutorialVideoDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId
    private Long id;

    /**
     * 分类 ID
     * <p>
     * 对应 ai_tutorial_category 表的 id
     */
    private Long categoryId;

    /**
     * 教程名称
     */
    private String name;

    /**
     * 封面图 URL
     */
    private String cover;

    /**
     * 视频 URL
     */
    private String videoUrl;

    /**
     * 视频时长（秒）
     */
    private Integer duration;

    /**
     * 观看数
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 是否免费观看
     */
    private Boolean isFree;

    /**
     * 排序（越小越前）
     */
    private Integer sortOrder;

}