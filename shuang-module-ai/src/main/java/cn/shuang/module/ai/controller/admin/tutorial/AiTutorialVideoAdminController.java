package cn.shuang.module.ai.controller.admin.tutorial;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.framework.common.util.object.BeanUtils;
import cn.shuang.module.ai.controller.admin.tutorial.vo.*;
import cn.shuang.module.ai.dal.dataobject.tutorial.AiTutorialCategoryDO;
import cn.shuang.module.ai.dal.dataobject.tutorial.AiTutorialVideoDO;
import cn.shuang.module.ai.service.tutorial.AiTutorialVideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static cn.shuang.framework.common.pojo.CommonResult.success;

/**
 * 管理后台教程视频 Controller
 * <p>
 * 提供教程视频和分类的管理接口，包括创建、更新、删除、查询等
 *
 * @author shuang-pro
 */
@Tag(name = "管理后台 - 教程视频管理")
@RestController
@RequestMapping("/admin/ai/tutorial")
@Validated
public class AiTutorialVideoAdminController {

    @Resource
    private AiTutorialVideoService tutorialVideoService;

    // ========== 分类管理 ==========

    @PostMapping("/category/create")
    @Operation(summary = "创建教程分类")
    @PreAuthorize("@ss.hasPermission('ai:tutorial-category:create')")
    public CommonResult<Long> createCategory(@Valid @RequestBody AiTutorialCategoryCreateReqVO createReqVO) {
        AiTutorialCategoryDO category = BeanUtils.toBean(createReqVO, AiTutorialCategoryDO.class);
        category.setVideoCount(0);
        tutorialVideoService.createCategory(category);
        return success(category.getId());
    }

    @PutMapping("/category/update")
    @Operation(summary = "更新教程分类")
    @PreAuthorize("@ss.hasPermission('ai:tutorial-category:update')")
    public CommonResult<Boolean> updateCategory(@Valid @RequestBody AiTutorialCategoryUpdateReqVO updateReqVO) {
        AiTutorialCategoryDO category = BeanUtils.toBean(updateReqVO, AiTutorialCategoryDO.class);
        tutorialVideoService.updateCategory(category);
        return success(true);
    }

    @DeleteMapping("/category/delete")
    @Operation(summary = "删除教程分类")
    @Parameter(name = "id", description = "分类 ID", required = true)
    @PreAuthorize("@ss.hasPermission('ai:tutorial-category:delete')")
    public CommonResult<Boolean> deleteCategory(@RequestParam("id") Long id) {
        tutorialVideoService.deleteCategory(id);
        return success(true);
    }

    @GetMapping("/category/list")
    @Operation(summary = "获取教程分类列表")
    public CommonResult<List<AiTutorialCategoryRespVO>> getCategoryList() {
        List<AiTutorialCategoryDO> categories = tutorialVideoService.getCategoryList();
        List<AiTutorialCategoryRespVO> respVOList = categories.stream()
                .map(this::convertCategoryToRespVO)
                .collect(Collectors.toList());
        return success(respVOList);
    }

    @GetMapping("/category/get")
    @Operation(summary = "获取教程分类详情")
    @Parameter(name = "id", description = "分类 ID", required = true)
    public CommonResult<AiTutorialCategoryRespVO> getCategory(@RequestParam("id") Long id) {
        AiTutorialCategoryDO category = tutorialVideoService.getCategory(id);
        return success(convertCategoryToRespVO(category));
    }

    // ========== 视频管理 ==========

    @PostMapping("/video/create")
    @Operation(summary = "创建教程视频")
    @PreAuthorize("@ss.hasPermission('ai:tutorial-video:create')")
    public CommonResult<Long> createVideo(@Valid @RequestBody AiTutorialVideoCreateReqVO createReqVO) {
        AiTutorialVideoDO video = BeanUtils.toBean(createReqVO, AiTutorialVideoDO.class);
        video.setViewCount(0);
        video.setLikeCount(0);
        tutorialVideoService.createVideo(video);
        return success(video.getId());
    }

    @PutMapping("/video/update")
    @Operation(summary = "更新教程视频")
    @PreAuthorize("@ss.hasPermission('ai:tutorial-video:update')")
    public CommonResult<Boolean> updateVideo(@Valid @RequestBody AiTutorialVideoUpdateReqVO updateReqVO) {
        AiTutorialVideoDO video = BeanUtils.toBean(updateReqVO, AiTutorialVideoDO.class);
        tutorialVideoService.updateVideo(video);
        return success(true);
    }

    @DeleteMapping("/video/delete")
    @Operation(summary = "删除教程视频")
    @Parameter(name = "id", description = "视频 ID", required = true)
    @PreAuthorize("@ss.hasPermission('ai:tutorial-video:delete')")
    public CommonResult<Boolean> deleteVideo(@RequestParam("id") Long id) {
        tutorialVideoService.deleteVideo(id);
        return success(true);
    }

    @GetMapping("/video/list")
    @Operation(summary = "获取教程视频列表")
    @Parameter(name = "categoryId", description = "分类 ID（可选）")
    @Parameter(name = "limit", description = "数量限制", example = "20")
    public CommonResult<List<AiTutorialVideoRespVO>> getVideoList(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        List<AiTutorialVideoDO> videoList;

        if (categoryId != null) {
            videoList = tutorialVideoService.getVideoListByCategory(categoryId, limit);
        } else {
            videoList = tutorialVideoService.getAllVideoList(limit);
        }

        List<AiTutorialVideoRespVO> respVOList = videoList.stream()
                .map(this::convertVideoToRespVO)
                .collect(Collectors.toList());
        return success(respVOList);
    }

    @GetMapping("/video/get")
    @Operation(summary = "获取教程视频详情")
    @Parameter(name = "id", description = "视频 ID", required = true)
    public CommonResult<AiTutorialVideoRespVO> getVideo(@RequestParam("id") Long id) {
        AiTutorialVideoDO video = tutorialVideoService.getVideo(id);
        return success(convertVideoToRespVO(video));
    }

    /**
     * 转换分类为 Response VO
     */
    private AiTutorialCategoryRespVO convertCategoryToRespVO(AiTutorialCategoryDO category) {
        if (category == null) {
            return null;
        }
        AiTutorialCategoryRespVO respVO = new AiTutorialCategoryRespVO();
        respVO.setId(category.getId());
        respVO.setName(category.getName());
        respVO.setDescription(category.getDescription());
        respVO.setIconUrl(category.getIconUrl());
        respVO.setSortOrder(category.getSortOrder());
        respVO.setVideoCount(category.getVideoCount());
        respVO.setCreateTime(category.getCreateTime());
        return respVO;
    }

    /**
     * 转换视频为 Response VO
     */
    private AiTutorialVideoRespVO convertVideoToRespVO(AiTutorialVideoDO video) {
        if (video == null) {
            return null;
        }
        AiTutorialVideoRespVO respVO = new AiTutorialVideoRespVO();
        respVO.setId(video.getId());
        respVO.setCategoryId(video.getCategoryId());

        // 获取分类名称
        AiTutorialCategoryDO category = tutorialVideoService.getCategory(video.getCategoryId());
        if (category != null) {
            respVO.setCategoryName(category.getName());
        }

        respVO.setName(video.getName());
        respVO.setCover(video.getCover());
        respVO.setVideoUrl(video.getVideoUrl());
        respVO.setDuration(video.getDuration());
        respVO.setViewCount(video.getViewCount());
        respVO.setLikeCount(video.getLikeCount());
        respVO.setIsFree(video.getIsFree());
        respVO.setSortOrder(video.getSortOrder());
        respVO.setCreateTime(video.getCreateTime());
        return respVO;
    }

}