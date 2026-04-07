package cn.shuang.module.ai.service.category;

import cn.shuang.module.ai.dal.dataobject.category.AiInspirationCategoryDO;

import java.util.List;

/**
 * 灵感案例分类 Service 接口
 *
 * @author shuang-pro
 */
public interface AiInspirationCategoryService {

    /**
     * 获取所有分类列表（按排序）
     *
     * @return 分类列表
     */
    List<AiInspirationCategoryDO> getCategoryList();

    /**
     * 获取分类详情
     *
     * @param id 分类 ID
     * @return 分类详情
     */
    AiInspirationCategoryDO getCategory(Integer id);

    /**
     * 创建分类
     *
     * @param category 分类信息
     * @return 分类 ID
     */
    Integer createCategory(AiInspirationCategoryDO category);

    /**
     * 更新分类
     *
     * @param category 分类信息
     */
    void updateCategory(AiInspirationCategoryDO category);

    /**
     * 删除分类
     *
     * @param id 分类 ID
     */
    void deleteCategory(Integer id);

}