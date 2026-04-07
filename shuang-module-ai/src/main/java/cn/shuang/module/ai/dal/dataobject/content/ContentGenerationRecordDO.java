package cn.shuang.module.ai.dal.dataobject.content;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 内容生成记录 DO
 * <p>
 * 记录用户的内容生成请求和结果
 */
@TableName("content_generation_record")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentGenerationRecordDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 生成类型：1-爆款拆解，2-文生视频，3-图生视频，4-黄金 6 秒，5-AI 混剪
     */
    private Integer generationType;

    /**
     * 子类型
     */
    private Integer subType;

    // ==================== 输入 ====================

    /**
     * 输入文本 (提示词/参考文案)
     */
    private String inputText;

    /**
     * 输入图片 URL
     */
    private String inputImageUrl;

    /**
     * 输入视频 URL(拆解用)
     */
    private String inputVideoUrl;

    /**
     * 使用的模板 ID
     */
    private Long templateId;

    // ==================== 输出 ====================

    /**
     * 输出结果 URL
     */
    private String outputUrl;

    /**
     * 缩略图 URL
     */
    private String outputThumbnail;

    /**
     * 视频时长 (ms)
     */
    private Integer outputDurationMs;

    /**
     * AI 分析结果 (JSON 格式)
     */
    private String analysisResult;

    /**
     * 生成的脚本内容
     */
    private String scriptContent;

    /**
     * SEO 优化标题
     */
    private String seoTitle;

    /**
     * SEO 标签 (逗号分隔)
     */
    private String seoTags;

    // ==================== 状态和统计 ====================

    /**
     * 状态：0-处理中，1-完成，2-失败
     */
    private Integer status;

    /**
     * 消耗积分
     */
    private Integer costPoints;

    /**
     * 耗时 (ms)
     */
    private Integer durationMs;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 错误信息
     */
    private String errorMessage;
}
