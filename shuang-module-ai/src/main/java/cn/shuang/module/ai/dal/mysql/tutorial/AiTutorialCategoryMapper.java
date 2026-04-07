package cn.shuang.module.ai.dal.mysql.tutorial;

import cn.shuang.framework.mybatis.core.mapper.BaseMapperX;
import cn.shuang.module.ai.dal.dataobject.tutorial.AiTutorialCategoryDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 教程视频分类 Mapper
 *
 * @author shuang-pro
 */
@Mapper
public interface AiTutorialCategoryMapper extends BaseMapperX<AiTutorialCategoryDO> {

    /**
     * 获取所有分类（按排序）
     *
     * @return 分类列表
     */
    default List<AiTutorialCategoryDO> selectAllBySort() {
        return selectList(new LambdaQueryWrapper<AiTutorialCategoryDO>()
                .orderByAsc(AiTutorialCategoryDO::getSortOrder));
    }

}