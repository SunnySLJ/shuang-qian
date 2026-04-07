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
                .eqIfPresent(AiInspirationCaseDO::getCategory, reqVO.getCategory())
                .eqIfPresent(AiInspirationCaseDO::getFeatured, reqVO.getFeatured())
                .betweenIfPresent(AiInspirationCaseDO::getCreateTime, reqVO.getCreateTimeStartTime(), reqVO.getCreateTimeEndTime())
                .orderByAsc(AiInspirationCaseDO::getSortOrder));
    }

    /**
     * 获取灵感案例列表（带排序）
     */
    default List<AiInspirationCaseDO> selectListWithSort(Integer sortOrder, Boolean featured) {
        return selectList(new LambdaQueryWrapperX<AiInspirationCaseDO>()
                .eqIfPresent(AiInspirationCaseDO::getFeatured, featured)
                .ltIfPresent(AiInspirationCaseDO::getSortOrder, sortOrder)
                .orderByAsc(AiInspirationCaseDO::getSortOrder));
    }

    /**
     * 根据分类获取灵感案例列表
     */
    default List<AiInspirationCaseDO> selectListByCategory(String category, Integer limit) {
        return selectList(new LambdaQueryWrapperX<AiInspirationCaseDO>()
                .eq(AiInspirationCaseDO::getCategory, category)
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
