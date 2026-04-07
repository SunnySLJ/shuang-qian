package cn.shuang.module.ai.dal.dataobject.tutorial;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 教程视频分类 DO
 * <p>
 * 用于存储教程视频的分类信息，如初级入门、进阶操作、高级技巧等
 *
 * @author shuang-pro
 */
@TableName(value = "ai_tutorial_category", autoResultMap = true)
@KeySequence("ai_tutorial_category_seq")
@Data
public class AiTutorialCategoryDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 分类图标 URL
     */
    private String iconUrl;

    /**
     * 排序（越小越前）
     */
    private Integer sortOrder;

    /**
     * 视频数量
     */
    private Integer videoCount;

}