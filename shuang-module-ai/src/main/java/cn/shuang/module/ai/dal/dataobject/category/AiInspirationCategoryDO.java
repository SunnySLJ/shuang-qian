package cn.shuang.module.ai.dal.dataobject.category;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 灵感案例分类 DO
 * <p>
 * 用于存储灵感案例的行业分类，包括电商、大健康、工厂、探店等
 *
 * @author shuang-pro
 */
@TableName(value = "ai_inspiration_category", autoResultMap = true)
@KeySequence("ai_inspiration_category_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AiInspirationCategoryDO extends BaseDO {

    /**
     * 分类 ID
     * <p>
     * 0=全部, 5=电商, 15=大健康, 16=工厂, 17=励志演讲等
     */
    @TableId
    private Integer id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类图标 URL
     */
    private String icon;

    /**
     * 排序（越小越前）
     */
    private Integer sortOrder;

}