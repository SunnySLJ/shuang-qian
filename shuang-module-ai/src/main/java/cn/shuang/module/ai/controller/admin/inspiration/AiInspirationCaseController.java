package cn.shuang.module.ai.controller.admin.inspiration;

import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.module.ai.controller.admin.inspiration.vo.*;
import cn.shuang.module.ai.dal.dataobject.inspiration.AiInspirationCaseDO;
import cn.shuang.module.ai.service.inspiration.AiInspirationCaseService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.shuang.framework.common.pojo.CommonResult.success;
import static cn.shuang.framework.common.util.collection.CollectionUtils.convertList;

/**
 * AI 灵感案例管理 Controller
 * <p>
 * 提供行业案例库的 CRUD 接口，支持按分类查询、精选案例推荐等功能
 *
 * @author shuang-pro
 */
@Tag(name = "管理后台 - AI 灵感案例")
@RestController
@RequestMapping("/ai/inspiration-case")
@Validated
public class AiInspirationCaseController {

    @Resource
    private AiInspirationCaseService inspirationCaseService;

    @PostMapping("/create")
    @Operation(summary = "创建灵感案例")
    public Long createCase(@Validated @RequestBody AiInspirationCaseCreateReqVO createReqVO) {
        return inspirationCaseService.createCase(createReqVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新灵感案例")
    public void updateCase(@Validated @RequestBody AiInspirationCaseUpdateReqVO updateReqVO) {
        inspirationCaseService.updateCase(updateReqVO);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除灵感案例")
    @Parameter(name = "id", description = "案例 ID", required = true)
    public void deleteCase(@RequestParam("id") Long id) {
        inspirationCaseService.deleteCase(id);
    }

    @GetMapping("/get")
    @Operation(summary = "获取灵感案例详情")
    @Parameter(name = "id", description = "案例 ID", required = true)
    public AiInspirationCaseRespVO getCase(@RequestParam("id") Long id) {
        AiInspirationCaseDO inspirationCase = inspirationCaseService.getCase(id);
        // 增加浏览次数
        if (inspirationCase != null) {
            inspirationCaseService.incrementViewCount(id);
        }
        return convertToRespVO(inspirationCase);
    }

    @GetMapping("/page")
    @Operation(summary = "获取灵感案例分页")
    public PageResult<AiInspirationCaseRespVO> getPage(AiInspirationCasePageReqVO pageReqVO) {
        PageResult<AiInspirationCaseDO> pageResult = inspirationCaseService.getPage(pageReqVO);
        List<AiInspirationCaseRespVO> respVOList = convertList(pageResult.getList(), this::convertToRespVO);
        return new PageResult<>(respVOList, pageResult.getTotal());
    }

    @GetMapping("/list-by-category")
    @Operation(summary = "根据分类获取灵感案例列表")
    @Parameter(name = "category", description = "行业分类", required = true)
    @Parameter(name = "limit", description = "数量限制", example = "10")
    public List<AiInspirationCaseRespVO> getListByCategory(
            @RequestParam("category") String category,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<AiInspirationCaseDO> list = inspirationCaseService.getListByCategory(category, limit);
        return convertList(list, this::convertToRespVO);
    }

    @GetMapping("/featured-list")
    @Operation(summary = "获取精选灵感案例列表")
    @Parameter(name = "limit", description = "数量限制", example = "10")
    public List<AiInspirationCaseRespVO> getFeaturedList(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<AiInspirationCaseDO> list = inspirationCaseService.getFeaturedList(limit);
        return convertList(list, this::convertToRespVO);
    }

    /**
     * 转换为 Response VO
     *
     * @param inspirationCase 灵感案例 DO
     * @return Response VO
     */
    private AiInspirationCaseRespVO convertToRespVO(AiInspirationCaseDO inspirationCase) {
        if (inspirationCase == null) {
            return null;
        }
        AiInspirationCaseRespVO respVO = new AiInspirationCaseRespVO();
        respVO.setId(inspirationCase.getId());
        respVO.setCategory(inspirationCase.getCategory());
        respVO.setTitle(inspirationCase.getTitle());
        respVO.setDescription(inspirationCase.getDescription());
        respVO.setCoverImageUrl(inspirationCase.getCoverImageUrl());
        respVO.setVideoUrl(inspirationCase.getVideoUrl());
        respVO.setPromptTemplate(inspirationCase.getPromptTemplate());
        respVO.setViewCount(inspirationCase.getViewCount());
        respVO.setUseCount(inspirationCase.getUseCount());
        respVO.setFeatured(inspirationCase.getFeatured());
        respVO.setSortOrder(inspirationCase.getSortOrder());
        respVO.setExtraData(inspirationCase.getExtraData());
        respVO.setCreateTime(inspirationCase.getCreateTime());
        return respVO;
    }

}
