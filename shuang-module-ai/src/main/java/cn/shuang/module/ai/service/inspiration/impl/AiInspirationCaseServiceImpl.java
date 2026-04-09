package cn.shuang.module.ai.service.inspiration.impl;

import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.module.ai.controller.admin.inspiration.vo.AiInspirationCaseCreateReqVO;
import cn.shuang.module.ai.controller.admin.inspiration.vo.AiInspirationCasePageReqVO;
import cn.shuang.module.ai.controller.admin.inspiration.vo.AiInspirationCaseUpdateReqVO;
import cn.shuang.module.ai.dal.dataobject.inspiration.AiInspirationCaseDO;
import cn.shuang.module.ai.dal.mysql.inspiration.AiInspirationCaseMapper;
import cn.shuang.module.ai.service.inspiration.AiInspirationCaseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.shuang.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.shuang.module.ai.enums.ErrorCodeConstants.INSPIRATION_CASE_NOT_EXISTS;

/**
 * AI 灵感案例 Service 实现类
 *
 * @author shuang-pro
 */
@Service
@Slf4j
public class AiInspirationCaseServiceImpl implements AiInspirationCaseService {

    @Resource
    private AiInspirationCaseMapper inspirationCaseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCase(AiInspirationCaseCreateReqVO createReqVO) {
        AiInspirationCaseDO inspirationCase = new AiInspirationCaseDO()
                .setType(createReqVO.getType())
                .setCategoryId(createReqVO.getCategoryId())
                .setTitle(createReqVO.getTitle())
                .setContent(createReqVO.getContent())
                .setImage(createReqVO.getImage())
                .setImageFirst(createReqVO.getImageFirst())
                .setImageTail(createReqVO.getImageTail())
                .setVideoUrl(createReqVO.getVideoUrl())
                .setDuration(createReqVO.getDuration())
                .setLabel(createReqVO.getLabel())
                .setIcon(createReqVO.getIcon())
                .setFeatured(createReqVO.getFeatured() != null ? createReqVO.getFeatured() : false)
                .setSortOrder(createReqVO.getSortOrder())
                .setLikeCount(0)
                .setViewCount(0)
                .setUseCount(0);
        inspirationCaseMapper.insert(inspirationCase);
        log.info("[AiInspirationCaseService] 创建灵感案例成功 - id: {}, title: {}", inspirationCase.getId(), inspirationCase.getTitle());
        return inspirationCase.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCase(AiInspirationCaseUpdateReqVO updateReqVO) {
        // 校验案例是否存在
        validateCaseExists(updateReqVO.getId());

        AiInspirationCaseDO updateObj = new AiInspirationCaseDO()
                .setId(updateReqVO.getId())
                .setType(updateReqVO.getType())
                .setCategoryId(updateReqVO.getCategoryId())
                .setTitle(updateReqVO.getTitle())
                .setContent(updateReqVO.getContent())
                .setImage(updateReqVO.getImage())
                .setImageFirst(updateReqVO.getImageFirst())
                .setImageTail(updateReqVO.getImageTail())
                .setVideoUrl(updateReqVO.getVideoUrl())
                .setDuration(updateReqVO.getDuration())
                .setLabel(updateReqVO.getLabel())
                .setIcon(updateReqVO.getIcon())
                .setFeatured(updateReqVO.getFeatured())
                .setSortOrder(updateReqVO.getSortOrder());
        inspirationCaseMapper.updateById(updateObj);
        log.info("[AiInspirationCaseService] 更新灵感案例成功 - id: {}", updateReqVO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCase(Long id) {
        // 校验案例是否存在
        validateCaseExists(id);
        inspirationCaseMapper.deleteById(id);
        log.info("[AiInspirationCaseService] 删除灵感案例成功 - id: {}", id);
    }

    @Override
    public AiInspirationCaseDO getCase(Long id) {
        return inspirationCaseMapper.selectById(id);
    }

    @Override
    public PageResult<AiInspirationCaseDO> getPage(AiInspirationCasePageReqVO pageReqVO) {
        return inspirationCaseMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AiInspirationCaseDO> getListByCategoryId(Integer categoryId, Integer limit) {
        return inspirationCaseMapper.selectListByCategoryId(categoryId, limit);
    }

    @Override
    public List<AiInspirationCaseDO> getListByType(String type, Integer limit) {
        return inspirationCaseMapper.selectListByType(type, limit);
    }

    @Override
    public List<AiInspirationCaseDO> getFeaturedList(Integer limit) {
        return inspirationCaseMapper.selectFeaturedList(limit);
    }

    @Override
    public List<AiInspirationCaseDO> getAllList(Integer limit) {
        return inspirationCaseMapper.selectAllList(limit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long id) {
        inspirationCaseMapper.incrementViewCount(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementUseCount(Long id) {
        inspirationCaseMapper.incrementUseCount(id);
    }

    @Override
    public List<AiInspirationCaseDO> getAllCases() {
        return inspirationCaseMapper.selectList();
    }

    /**
     * 校验案例是否存在
     *
     * @param id 案例 ID
     * @return 案例
     * @throws cn.shuang.framework.common.exception.ServiceException 如果不存在
     */
    private AiInspirationCaseDO validateCaseExists(Long id) {
        AiInspirationCaseDO inspirationCase = inspirationCaseMapper.selectById(id);
        if (inspirationCase == null) {
            throw exception(INSPIRATION_CASE_NOT_EXISTS);
        }
        return inspirationCase;
    }

}