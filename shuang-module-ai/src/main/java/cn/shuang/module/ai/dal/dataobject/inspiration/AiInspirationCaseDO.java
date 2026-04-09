package cn.shuang.module.ai.dal.dataobject.inspiration;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * AI 灵感案例 DO
 * <p>
 * 用于存储灵感案例信息，包括类型、分类、标题、提示词内容、视频等
 * 数据来源参考 timarsky.com 的灵感案例库
 *
 * @author shuang-pro
 */
@TableName(value = "ai_inspiration_case", autoResultMap = true)
@KeySequence("ai_inspiration_case_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AiInspirationCaseDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId
    private Long id;

    /**
     * 类型：banana(香蕉生图)/veo(威尔视频)/grok(马克视频)/seedance(索拉视频)
     */
    private String type;

    /**
     * 分类 ID
     * <p>
     * 对应 ai_inspiration_category 表的 id
     * 0=全部, 5=电商, 15=大健康, 16=工厂, 17=励志演讲等
     */
    private Integer categoryId;

    /**
     * 案例标题
     */
    private String title;

    /**
     * 提示词内容
     * <p>
     * 用于 AI 生成的提示词模板，用户可以参考或直接使用
     */
    private String content;

    /**
     * 封面图 URL
     */
    private String image;

    /**
     * 首帧图 URL
     * <p>
     * 用于视频生成的首帧参考图
     */
    private String imageFirst;

    /**
     * 尾帧图 URL
     * <p>
     * 用于视频生成的尾帧参考图
     */
    private String imageTail;

    /**
     * 视频 URL
     */
    private String videoUrl;

    /**
     * 视频时长（秒）
     */
    private Integer duration;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 浏览数
     */
    private Integer viewCount;

    /**
     * 使用数
     * <p>
     * 用户基于此案例生成内容的次数
     */
    private Integer useCount;

    /**
     * 标签
     * <p>
     * 例如：威尔视频、香蕉生图、马克视频等
     */
    private String label;

    /**
     * 图标 URL
     * <p>
     * 对应类型的图标，例如 https://cdn.fenshen123.com/icons/grok.png
     */
    private String icon;

    /**
     * 是否精选
     */
    private Boolean featured;

    /**
     * 排序（越小越前）
     */
    private Integer sortOrder;

}