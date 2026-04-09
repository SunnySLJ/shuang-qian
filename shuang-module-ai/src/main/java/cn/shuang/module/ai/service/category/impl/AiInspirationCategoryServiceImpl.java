package cn.shuang.module.ai.service.category.impl;

import cn.shuang.module.ai.dal.dataobject.category.AiInspirationCategoryDO;
import cn.shuang.module.ai.dal.mysql.category.AiInspirationCategoryMapper;
import cn.shuang.module.ai.service.category.AiInspirationCategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 灵感案例分类 Service 实现类
 *
 * @author shuang-pro
 */
@Service
@Slf4j
public class AiInspirationCategoryServiceImpl implements AiInspirationCategoryService {

    @Resource
    private AiInspirationCategoryMapper categoryMapper;

    @Override
    public List<AiInspirationCategoryDO> getCategoryList() {
        return categoryMapper.selectAllBySort();
    }

    @Override
    public AiInspirationCategoryDO getCategory(Integer id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public Integer createCategory(AiInspirationCategoryDO category) {
        categoryMapper.insert(category);
        log.info("[AiInspirationCategoryService] 创建分类成功 - id: {}, name: {}", category.getId(), category.getName());
        return category.getId();
    }

    @Override
    public void updateCategory(AiInspirationCategoryDO category) {
        categoryMapper.updateById(category);
        log.info("[AiInspirationCategoryService] 更新分类成功 - id: {}", category.getId());
    }

    @Override
    public void deleteCategory(Integer id) {
        categoryMapper.deleteById(id);
        log.info("[AiInspirationCategoryService] 删除分类成功 - id: {}", id);
    }

}