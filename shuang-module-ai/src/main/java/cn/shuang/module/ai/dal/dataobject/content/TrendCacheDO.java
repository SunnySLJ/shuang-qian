package cn.shuang.module.ai.dal.dataobject.content;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 热门内容缓存 DO
 * <p>
 * 缓存从各平台抓取的热门内容及其分析结果
 */
@TableName("trend_cache")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendCacheDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 平台：douyin/xiaohongshu/bilibili
     */
    private String platform;

    /**
     * 分类
     */
    private String category;

    /**
     * 内容 ID
     */
    private String contentId;

    /**
     * 标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    // ==================== 热门指标 ====================

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 播放数
     */
    private Integer viewCount;

    // ==================== 分析结果 ====================

    /**
     * 热门分数 (0-100)
     */
    private BigDecimal trendScore;

    /**
     * 分析结果 (JSON)
     */
    private String analysisResult;

    /**
     * 标签
     */
    private String tags;

    // ==================== 审计字段 ====================

    /**
     * 抓取时间
     */
    private LocalDateTime crawledAt;
}
