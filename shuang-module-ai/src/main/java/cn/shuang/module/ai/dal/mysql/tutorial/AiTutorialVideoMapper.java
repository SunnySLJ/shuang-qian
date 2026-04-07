package cn.shuang.module.ai.dal.mysql.tutorial;

import cn.shuang.framework.mybatis.core.mapper.BaseMapperX;
import cn.shuang.module.ai.dal.dataobject.tutorial.AiTutorialVideoDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 教程视频 Mapper
 *
 * @author shuang-pro
 */
@Mapper
public interface AiTutorialVideoMapper extends BaseMapperX<AiTutorialVideoDO> {

    /**
     * 根据分类获取视频列表
     *
     * @param categoryId 分类 ID
     * @param limit      数量限制
     * @return 视频列表
     */
    default List<AiTutorialVideoDO> selectListByCategoryId(Long categoryId, Integer limit) {
        return selectList(new LambdaQueryWrapper<AiTutorialVideoDO>()
                .eq(AiTutorialVideoDO::getCategoryId, categoryId)
                .orderByAsc(AiTutorialVideoDO::getSortOrder)
                .last("LIMIT " + limit));
    }

    /**
     * 获取所有视频列表（按排序）
     *
     * @param limit 数量限制
     * @return 视频列表
     */
    default List<AiTutorialVideoDO> selectAllBySort(Integer limit) {
        return selectList(new LambdaQueryWrapper<AiTutorialVideoDO>()
                .orderByAsc(AiTutorialVideoDO::getSortOrder)
                .last("LIMIT " + limit));
    }

    /**
     * 根据分类获取视频数量
     *
     * @param categoryId 分类 ID
     * @return 视频数量
     */
    default Long selectCountByCategoryId(Long categoryId) {
        return selectCount(new LambdaQueryWrapper<AiTutorialVideoDO>()
                .eq(AiTutorialVideoDO::getCategoryId, categoryId));
    }

}