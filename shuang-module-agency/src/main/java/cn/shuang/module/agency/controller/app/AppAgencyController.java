package cn.shuang.module.agency.controller.app;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.framework.security.core.util.SecurityFrameworkUtils;
import cn.shuang.module.agency.controller.app.vo.AppAgencyUserVO;
import cn.shuang.module.agency.controller.app.vo.AppBindAgencyReqVO;
import cn.shuang.module.agency.controller.app.vo.AppChildAgencyVO;
import cn.shuang.module.agency.controller.app.vo.AppPointTransferReqVO;
import cn.shuang.module.agency.dal.dataobject.AgencyUserDO;
import cn.shuang.module.agency.service.AgencyUserService;
import cn.shuang.module.system.api.user.AdminUserApi;
import cn.shuang.module.system.api.user.dto.AdminUserRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.shuang.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 代理管理 - 用户端
 *
 * @author shuang-pro
 */
@Tag(name = "用户端 - 代理管理")
@RestController
@RequestMapping("/app/agency")
@RequiredArgsConstructor
@Validated
public class AppAgencyController {

    private final AgencyUserService agencyUserService;
    private final AdminUserApi adminUserApi;

    @GetMapping("/user/my")
    @Operation(summary = "获取我的代理信息")
    public CommonResult<AppAgencyUserVO> getMyAgency() {
        Long userId = getLoginUserId();

        AgencyUserDO agencyUser = agencyUserService.getAgencyByUserId(userId);

        AppAgencyUserVO vo = new AppAgencyUserVO();
        if (agencyUser != null) {
            vo.setAgencyEnabled(agencyUser.getAgencyEnabled());
            vo.setLevel(agencyUser.getLevel());
            vo.setTotalPoints(agencyUser.getTotalPoints());
            vo.setDistributedPoints(agencyUser.getDistributedPoints());
            vo.setAvailablePoints(agencyUser.getTotalPoints() - agencyUser.getDistributedPoints());
            vo.setDirectInviteCount(agencyUser.getDirectInviteCount());
            vo.setTeamTotalCount(agencyUser.getTeamTotalCount());
        } else {
            vo.setAgencyEnabled(false);
            vo.setLevel(0);
            vo.setTotalPoints(0);
            vo.setDistributedPoints(0);
            vo.setAvailablePoints(0);
            vo.setDirectInviteCount(0);
            vo.setTeamTotalCount(0);
        }

        return CommonResult.success(vo);
    }

    @PostMapping("/user/bind")
    @Operation(summary = "绑定上级代理")
    public CommonResult<Boolean> bindAgency(@RequestBody @Validated AppBindAgencyReqVO reqVO) {
        Long userId = getLoginUserId();

        // 1. 根据邀请码查找上级代理
        AdminUserRespDTO parentUser = adminUserApi.getUserByInviteCode(reqVO.getInviteCode());
        if (parentUser == null) {
            return CommonResult.error(1, "邀请码无效");
        }

        // 不能绑定自己
        if (parentUser.getId().equals(userId)) {
            return CommonResult.error(1, "不能绑定自己作为上级代理");
        }

        // 2. 执行绑定逻辑
        boolean result = agencyUserService.bindParentAgency(userId, parentUser.getId());
        return CommonResult.success(result);
    }

    @PostMapping("/point/transfer")
    @Operation(summary = "分配积分给用户")
    public CommonResult<Boolean> transferPoint(@RequestBody @Validated AppPointTransferReqVO reqVO) {
        Long userId = getLoginUserId();

        boolean result = agencyUserService.transferPoints(userId, reqVO.getToUserId(),
                reqVO.getPointAmount(), reqVO.getDescription());
        return CommonResult.success(result);
    }

    @GetMapping("/user/children")
    @Operation(summary = "获取我的下级列表")
    public CommonResult<java.util.List<AppChildAgencyVO>> getChildren() {
        Long userId = getLoginUserId();

        java.util.List<cn.shuang.module.agency.dal.dataobject.AgencyUserDO> children =
            agencyUserService.getChildren(userId);

        java.util.List<AppChildAgencyVO> result = new java.util.ArrayList<>();
        for (cn.shuang.module.agency.dal.dataobject.AgencyUserDO child : children) {
            AppChildAgencyVO vo = new AppChildAgencyVO();
            vo.setUserId(child.getUserId());
            vo.setLevel(child.getLevel());
            vo.setAgencyEnabled(child.getAgencyEnabled());
            vo.setTotalPoints(child.getTotalPoints());
            vo.setDistributedPoints(child.getDistributedPoints());
            vo.setNickname(child.getNickname());
            vo.setCreateTime(child.getCreateTime() != null ?
                child.getCreateTime().toString() : null);
            result.add(vo);
        }

        return CommonResult.success(result);
    }

    @GetMapping("/point/wallet")
    @Operation(summary = "获取我的积分钱包")
    public CommonResult<java.util.Map<String, Integer>> getWallet() {
        Long userId = getLoginUserId();

        int[] wallet = agencyUserService.getWallet(userId);
        java.util.Map<String, Integer> result = new java.util.HashMap<>();
        result.put("availablePoints", wallet[0]);
        result.put("frozenPoints", wallet[1]);
        result.put("totalDistributed", wallet[2]);
        result.put("totalReceived", wallet[3]);

        return CommonResult.success(result);
    }

}
