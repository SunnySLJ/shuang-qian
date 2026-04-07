package cn.shuang.module.ai.framework.content.context;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * SEO 优化结果
 * <p>
 * SEOOptimizerAgent 的输出结果
 */
@Data
@Builder
public class SEOResult {

    /**
     * 优化后的标题
     */
    private String optimizedTitle;

    /**
     * 视频描述
     */
    private String description;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 封面文案建议
     */
    private String coverText;

    /**
     * SEO 关键词列表
     */
    private List<String> keywords;

    /**
     * 推荐发布时间
     */
    private String suggestedPublishTime;
}
