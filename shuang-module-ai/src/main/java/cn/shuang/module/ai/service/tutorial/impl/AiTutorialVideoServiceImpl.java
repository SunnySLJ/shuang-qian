package cn.shuang.module.ai.service.tutorial.impl;

import cn.shuang.module.ai.dal.dataobject.tutorial.AiTutorialCategoryDO;
import cn.shuang.module.ai.dal.dataobject.tutorial.AiTutorialVideoDO;
import cn.shuang.module.ai.dal.mysql.tutorial.AiTutorialCategoryMapper;
import cn.shuang.module.ai.dal.mysql.tutorial.AiTutorialVideoMapper;
import cn.shuang.module.ai.service.tutorial.AiTutorialVideoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 教程视频 Service 实现类
 *
 * @author shuang-pro
 */
@Service
public class AiTutorialVideoServiceImpl implements AiTutorialVideoService {

    @Resource
    private AiTutorialCategoryMapper categoryMapper;

    @Resource
    private AiTutorialVideoMapper videoMapper;

    @Override
    public List<AiTutorialCategoryDO> getCategoryList() {
        List<AiTutorialCategoryDO> categories = categoryMapper.selectAllBySort();
        // 更新每个分类的视频数量
        categories.forEach(category -> {
            Long count = videoMapper.selectCountByCategoryId(category.getId());
            category.setVideoCount(count != null ? count.intValue() : 0);
        });
        return categories;
    }

    @Override
    public AiTutorialCategoryDO getCategory(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public List<AiTutorialVideoDO> getVideoListByCategory(Long categoryId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        return videoMapper.selectListByCategoryId(categoryId, limit);
    }

    @Override
    public List<AiTutorialVideoDO> getAllVideoList(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        return videoMapper.selectAllBySort(limit);
    }

    @Override
    public AiTutorialVideoDO getVideo(Long id) {
        return videoMapper.selectById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        AiTutorialVideoDO video = videoMapper.selectById(id);
        if (video != null) {
            video.setViewCount(video.getViewCount() != null ? video.getViewCount() + 1 : 1);
            videoMapper.updateById(video);
        }
    }

    @Override
    public void incrementLikeCount(Long id) {
        AiTutorialVideoDO video = videoMapper.selectById(id);
        if (video != null) {
            video.setLikeCount(video.getLikeCount() != null ? video.getLikeCount() + 1 : 1);
            videoMapper.updateById(video);
        }
    }

    // ========== 分类管理 ==========

    @Override
    public void createCategory(AiTutorialCategoryDO category) {
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(AiTutorialCategoryDO category) {
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    // ========== 视频管理 ==========

    @Override
    public void createVideo(AiTutorialVideoDO video) {
        videoMapper.insert(video);
    }

    @Override
    public void updateVideo(AiTutorialVideoDO video) {
        videoMapper.updateById(video);
    }

    @Override
    public void deleteVideo(Long id) {
        videoMapper.deleteById(id);
    }

}