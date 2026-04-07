package cn.shuang.module.ai.dal.dataobject.asset;

import cn.shuang.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 素材分组 DO
 *
 * @author shuang-pro
 */
@TableName(value = "ai_material_group", autoResultMap = true)
@KeySequence("ai_material_group_seq")
@Data
public class MaterialGroupDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sortOrder;

}