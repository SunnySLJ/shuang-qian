package cn.shuang.module.ai.dal.mysql.asset;

import cn.shuang.framework.mybatis.core.mapper.BaseMapperX;
import cn.shuang.module.ai.dal.dataobject.asset.MaterialGroupDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 素材分组 Mapper
 *
 * @author shuang-pro
 */
@Mapper
public interface MaterialGroupMapper extends BaseMapperX<MaterialGroupDO> {

    /**
     * 获取用户分组列表
     */
    default List<MaterialGroupDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapper<MaterialGroupDO>()
                .eq(MaterialGroupDO::getUserId, userId)
                .orderByAsc(MaterialGroupDO::getSortOrder));
    }

}