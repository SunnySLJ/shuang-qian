package cn.shuang.module.ai.dal.dataobject.inspiration;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.Map;

/**
 * AI 灵感案例 DO
 * <p>
 * 用于存储行业案例库，包括电商、大健康、工厂、探店等行业的优质案例
 * 用户可以参考这些案例生成类似的 AI 内容
 *
 * @author shuang-pro
 */
@TableName(value = "ai_inspiration_case", autoResultMap = true)
@KeySequence("ai_inspiration_case_seq")
@Data
public class AiInspirationCaseDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId
    private Long id;

    /**
     * 行业分类
     * <p>
     * 例如：电商、大健康、工厂、探店、宠物、美食、AI 数字人等
     */
    private String category;

    /**
     * 案例标题
     */
    private String title;

    /**
     * 案例描述
     */
    private String description;

    /**
     * 封面图 URL
     */
    private String coverImageUrl;

    /**
     * 演示视频 URL
     */
    private String videoUrl;

    /**
     * 提示词模板
     * <p>
     * 用户可以直接使用或修改此模板进行 AI 生成
     */
    private String promptTemplate;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 使用次数
     * <p>
     * 用户基于此案例生成内容的次数
     */
    private Integer useCount;

    /**
     * 是否精选
     */
    private Boolean featured;

    /**
     * 排序（越小越前）
     */
    private Integer sortOrder;

    /**
     * 扩展数据
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> extraData;

}
