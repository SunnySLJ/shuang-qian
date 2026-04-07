package cn.shuang.module.ai.dal.mysql.category;

import cn.shuang.framework.mybatis.core.mapper.BaseMapperX;
import cn.shuang.module.ai.dal.dataobject.category.AiInspirationCategoryDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 灵感案例分类 Mapper
 *
 * @author shuang-pro
 */
@Mapper
public interface AiInspirationCategoryMapper extends BaseMapperX<AiInspirationCategoryDO> {

    /**
     * 获取所有分类（按排序）
     *
     * @return 分类列表
     */
    default List<AiInspirationCategoryDO> selectAllBySort() {
        return selectList(new LambdaQueryWrapper<AiInspirationCategoryDO>()
                .orderByAsc(AiInspirationCategoryDO::getSortOrder));
    }

}