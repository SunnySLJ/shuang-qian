package cn.shuang.module.ai.controller.app.asset;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.framework.security.core.util.SecurityFrameworkUtils;
import cn.shuang.module.ai.controller.app.asset.vo.*;
import cn.shuang.module.ai.dal.dataobject.asset.MaterialGroupDO;
import cn.shuang.module.ai.dal.dataobject.asset.UserAssetDO;
import cn.shuang.module.ai.service.asset.UserAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static cn.shuang.framework.common.pojo.CommonResult.success;

/**
 * 用户端资产 Controller
 *
 * @author shuang-pro
 */
@Tag(name = "用户 APP - 用户资产")
@RestController
@RequestMapping("/app/ai/asset")
@Validated
public class AppUserAssetController {

    @Resource
    private UserAssetService userAssetService;

    // ========== 资产管理 ==========

    @GetMapping("/list")
    @Operation(summary = "获取我的资产列表")
    @Parameter(name = "assetType", description = "资产类型：1-图片，2-视频，3-音频", example = "1")
    @Parameter(name = "groupId", description = "分组ID", example = "1")
    @Parameter(name = "limit", description = "数量限制", example = "20")
    public CommonResult<List<AppUserAssetRespVO>> getMyAssetList(
            @RequestParam(value = "assetType", required = false) Integer assetType,
            @RequestParam(value = "groupId", required = false) Long groupId,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<UserAssetDO> assetList = userAssetService.getUserAssetList(userId, assetType, groupId, limit);
        List<AppUserAssetRespVO> respVOList = assetList.stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());
        return success(respVOList);
    }

    @GetMapping("/get")
    @Operation(summary = "获取资产详情")
    @Parameter(name = "id", description = "资产ID", required = true, example = "1024")
    public CommonResult<AppUserAssetRespVO> getAssetDetail(@RequestParam("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        UserAssetDO asset = userAssetService.getAssetDetail(id, userId);
        return success(convertToRespVO(asset));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除资产")
    @Parameter(name = "id", description = "资产ID", required = true, example = "1024")
    public CommonResult<Boolean> deleteAsset(@RequestParam("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        userAssetService.deleteAsset(id, userId);
        return success(true);
    }

    @PutMapping("/move")
    @Operation(summary = "移动资产到分组")
    @Parameter(name = "id", description = "资产ID", required = true, example = "1024")
    @Parameter(name = "groupId", description = "分组ID", required = true, example = "1")
    public CommonResult<Boolean> moveAssetToGroup(
            @RequestParam("id") Long id,
            @RequestParam("groupId") Long groupId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        userAssetService.moveAssetToGroup(id, groupId, userId);
        return success(true);
    }

    // ========== 素材分组管理 ==========

    @GetMapping("/group/list")
    @Operation(summary = "获取我的素材分组列表")
    public CommonResult<List<AppMaterialGroupRespVO>> getMyGroupList() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<MaterialGroupDO> groupList = userAssetService.getMaterialGroupList(userId);
        List<AppMaterialGroupRespVO> respVOList = groupList.stream()
                .map(this::convertGroupToRespVO)
                .collect(Collectors.toList());
        return success(respVOList);
    }

    @PostMapping("/group/create")
    @Operation(summary = "创建素材分组")
    public CommonResult<Long> createGroup(@Valid @RequestBody AppMaterialGroupCreateReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        Long groupId = userAssetService.createMaterialGroup(userId, reqVO);
        return success(groupId);
    }

    @PutMapping("/group/update")
    @Operation(summary = "更新素材分组")
    @Parameter(name = "id", description = "分组ID", required = true, example = "1")
    @Parameter(name = "name", description = "分组名称", required = true, example = "我的收藏")
    public CommonResult<Boolean> updateGroup(
            @RequestParam("id") Long id,
            @RequestParam("name") String name) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        userAssetService.updateMaterialGroup(id, name, userId);
        return success(true);
    }

    @DeleteMapping("/group/delete")
    @Operation(summary = "删除素材分组")
    @Parameter(name = "id", description = "分组ID", required = true, example = "1")
    public CommonResult<Boolean> deleteGroup(@RequestParam("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        userAssetService.deleteMaterialGroup(id, userId);
        return success(true);
    }

    /**
     * 转换资产为响应 VO
     */
    private AppUserAssetRespVO convertToRespVO(UserAssetDO asset) {
        if (asset == null) {
            return null;
        }
        AppUserAssetRespVO respVO = new AppUserAssetRespVO();
        respVO.setId(asset.getId());
        respVO.setAssetType(asset.getAssetType());
        respVO.setResourceUrl(asset.getResourceUrl());
        respVO.setThumbnailUrl(asset.getThumbnailUrl());
        respVO.setTitle(asset.getTitle());
        respVO.setDescription(asset.getDescription());
        respVO.setFileSize(asset.getFileSize());
        respVO.setDuration(asset.getDuration());
        respVO.setWidth(asset.getWidth());
        respVO.setHeight(asset.getHeight());
        respVO.setGroupId(asset.getGroupId());
        respVO.setCreateTime(asset.getCreateTime());
        return respVO;
    }

    /**
     * 转换分组为响应 VO
     */
    private AppMaterialGroupRespVO convertGroupToRespVO(MaterialGroupDO group) {
        if (group == null) {
            return null;
        }
        AppMaterialGroupRespVO respVO = new AppMaterialGroupRespVO();
        respVO.setId(group.getId());
        respVO.setName(group.getName());
        respVO.setSortOrder(group.getSortOrder());
        respVO.setCreateTime(group.getCreateTime());
        return respVO;
    }

}