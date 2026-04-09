package cn.shuang.module.ai.controller.admin.inspiration;

import cn.shuang.framework.common.pojo.CommonResult;
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
@RequestMapping("/admin/ai/inspiration-case")
@Validated
public class AiInspirationCaseController {

    @Resource
    private AiInspirationCaseService inspirationCaseService;

    @PostMapping("/create")
    @Operation(summary = "创建灵感案例")
    public CommonResult<Long> createCase(@Validated @RequestBody AiInspirationCaseCreateReqVO createReqVO) {
        return success(inspirationCaseService.createCase(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新灵感案例")
    public CommonResult<Boolean> updateCase(@Validated @RequestBody AiInspirationCaseUpdateReqVO updateReqVO) {
        inspirationCaseService.updateCase(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除灵感案例")
    @Parameter(name = "id", description = "案例 ID", required = true)
    public CommonResult<Boolean> deleteCase(@RequestParam("id") Long id) {
        inspirationCaseService.deleteCase(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取灵感案例详情")
    @Parameter(name = "id", description = "案例 ID", required = true)
    public CommonResult<AiInspirationCaseRespVO> getCase(@RequestParam("id") Long id) {
        AiInspirationCaseDO inspirationCase = inspirationCaseService.getCase(id);
        return success(convertToRespVO(inspirationCase));
    }

    @GetMapping("/page")
    @Operation(summary = "获取灵感案例分页")
    public CommonResult<PageResult<AiInspirationCaseRespVO>> getPage(AiInspirationCasePageReqVO pageReqVO) {
        PageResult<AiInspirationCaseDO> pageResult = inspirationCaseService.getPage(pageReqVO);
        List<AiInspirationCaseRespVO> respVOList = convertList(pageResult.getList(), this::convertToRespVO);
        return success(new PageResult<>(respVOList, pageResult.getTotal()));
    }

    @GetMapping("/list-by-category")
    @Operation(summary = "根据分类获取灵感案例列表")
    @Parameter(name = "categoryId", description = "分类 ID", required = true)
    @Parameter(name = "limit", description = "数量限制", example = "10")
    public CommonResult<List<AiInspirationCaseRespVO>> getListByCategory(
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<AiInspirationCaseDO> list = inspirationCaseService.getListByCategoryId(categoryId, limit);
        return success(convertList(list, this::convertToRespVO));
    }

    @GetMapping("/featured-list")
    @Operation(summary = "获取精选灵感案例列表")
    @Parameter(name = "limit", description = "数量限制", example = "10")
    public CommonResult<List<AiInspirationCaseRespVO>> getFeaturedList(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<AiInspirationCaseDO> list = inspirationCaseService.getFeaturedList(limit);
        return success(convertList(list, this::convertToRespVO));
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
        respVO.setType(inspirationCase.getType());
        respVO.setCategoryId(inspirationCase.getCategoryId());
        respVO.setTitle(inspirationCase.getTitle());
        respVO.setContent(inspirationCase.getContent());
        respVO.setImage(inspirationCase.getImage());
        respVO.setImageFirst(inspirationCase.getImageFirst());
        respVO.setImageTail(inspirationCase.getImageTail());
        respVO.setVideoUrl(inspirationCase.getVideoUrl());
        respVO.setDuration(inspirationCase.getDuration());
        respVO.setLikeCount(inspirationCase.getLikeCount());
        respVO.setViewCount(inspirationCase.getViewCount());
        respVO.setUseCount(inspirationCase.getUseCount());
        respVO.setLabel(inspirationCase.getLabel());
        respVO.setIcon(inspirationCase.getIcon());
        respVO.setFeatured(inspirationCase.getFeatured());
        respVO.setSortOrder(inspirationCase.getSortOrder());
        respVO.setCreateTime(inspirationCase.getCreateTime());
        return respVO;
    }

}