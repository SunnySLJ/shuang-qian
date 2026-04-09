package cn.shuang.module.ai.dal.mysql.inspiration;

import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.framework.mybatis.core.mapper.BaseMapperX;
import cn.shuang.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.shuang.module.ai.controller.admin.inspiration.vo.AiInspirationCasePageReqVO;
import cn.shuang.module.ai.dal.dataobject.inspiration.AiInspirationCaseDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * AI 灵感案例 Mapper
 *
 * @author shuang-pro
 */
@Mapper
public interface AiInspirationCaseMapper extends BaseMapperX<AiInspirationCaseDO> {

    /**
     * 获取灵感案例分页
     */
    default PageResult<AiInspirationCaseDO> selectPage(AiInspirationCasePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiInspirationCaseDO>()
                .likeIfPresent(AiInspirationCaseDO::getTitle, reqVO.getTitle())
                .eqIfPresent(AiInspirationCaseDO::getType, reqVO.getType())
                .eqIfPresent(AiInspirationCaseDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(AiInspirationCaseDO::getFeatured, reqVO.getFeatured())
                .betweenIfPresent(AiInspirationCaseDO::getCreateTime, reqVO.getCreateTimeStartTime(), reqVO.getCreateTimeEndTime())
                .orderByAsc(AiInspirationCaseDO::getSortOrder));
    }

    /**
     * 根据分类 ID 获取灵感案例列表
     */
    default List<AiInspirationCaseDO> selectListByCategoryId(Integer categoryId, Integer limit) {
        return selectList(new LambdaQueryWrapperX<AiInspirationCaseDO>()
                .eq(AiInspirationCaseDO::getCategoryId, categoryId)
                .orderByAsc(AiInspirationCaseDO::getSortOrder)
                .last("LIMIT " + limit));
    }

    /**
     * 根据类型获取灵感案例列表
     */
    default List<AiInspirationCaseDO> selectListByType(String type, Integer limit) {
        return selectList(new LambdaQueryWrapperX<AiInspirationCaseDO>()
                .eq(AiInspirationCaseDO::getType, type)
                .orderByAsc(AiInspirationCaseDO::getSortOrder)
                .last("LIMIT " + limit));
    }

    /**
     * 获取精选灵感案例列表
     */
    default List<AiInspirationCaseDO> selectFeaturedList(Integer limit) {
        return selectList(new LambdaQueryWrapperX<AiInspirationCaseDO>()
                .eq(AiInspirationCaseDO::getFeatured, true)
                .orderByAsc(AiInspirationCaseDO::getSortOrder)
                .last("LIMIT " + limit));
    }

    /**
     * 获取所有灵感案例列表（按排序）
     */
    default List<AiInspirationCaseDO> selectAllList(Integer limit) {
        return selectList(new LambdaQueryWrapperX<AiInspirationCaseDO>()
                .orderByAsc(AiInspirationCaseDO::getSortOrder)
                .last("LIMIT " + limit));
    }

    /**
     * 增加浏览次数
     */
    @Update("UPDATE ai_inspiration_case SET view_count = view_count + 1 WHERE id = #{id} AND deleted = 0")
    int incrementViewCount(@Param("id") Long id);

    /**
     * 增加使用次数
     */
    @Update("UPDATE ai_inspiration_case SET use_count = use_count + 1 WHERE id = #{id} AND deleted = 0")
    int incrementUseCount(@Param("id") Long id);

}