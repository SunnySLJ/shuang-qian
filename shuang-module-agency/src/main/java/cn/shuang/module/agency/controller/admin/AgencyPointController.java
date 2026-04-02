package cn.shuang.module.agency.controller.admin;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.framework.common.pojo.PageParam;
import cn.shuang.framework.common.pojo.PageResult;
import cn.shuang.module.agency.controller.admin.vo.AgencyUserVO;
import cn.shuang.module.agency.dal.dataobject.AgencyPointTransferDO;
import cn.shuang.module.agency.dal.dataobject.AgencyUserDO;
import cn.shuang.module.agency.service.AgencyPointService;
import cn.shuang.module.agency.service.AgencyUserService;
import cn.shuang.module.system.api.user.AdminUserApi;
import cn.shuang.module.system.api.user.dto.AdminUserRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static cn.shuang.framework.common.pojo.CommonResult.success;

/**
 * 积分管理 - 管理后台
 *
 * @author shuang-pro
 */
@Tag(name = "管理后台 - 积分管理")
@RestController
@RequestMapping("/agency/point")
@RequiredArgsConstructor
@Validated
public class AgencyPointController {

    private final AgencyPointService agencyPointService;
    private final AgencyUserService agencyUserService;
    private final AdminUserApi adminUserApi;

    @PostMapping("/transfer")
    @Operation(summary = "分配积分给用户")
    @PreAuthorize("@ss.hasPermission('agency:point:transfer')")
    public CommonResult<Boolean> transferPoint(
            @Parameter(description = "接收方用户 ID") @RequestParam Long userId,
            @Parameter(description = "积分数量") @RequestParam Integer pointAmount,
            @Parameter(description = "描述") @RequestParam(required = false) String description) {
        // 从 Session 获取当前登录用户 ID
        Long fromUserId = 1L; // TODO: 实际应该从 SecurityUtils.getLoginUserId() 获取
        boolean result = agencyPointService.transferPoints(fromUserId, userId, pointAmount, description);
        return success(result);
    }

    @GetMapping("/records")
    @Operation(summary = "获取积分分配记录列表")
    @PreAuthorize("@ss.hasPermission('agency:point:list')")
    public CommonResult<PageResult<AgencyPointTransferVO>> transferRecords(PageParam pageParam,
            @Parameter(description = "用户 ID") @RequestParam(required = false) Long userId) {
        PageResult<AgencyPointTransferDO> pageResult = agencyPointService.getPage(userId, pageParam.getPageNo(), pageParam.getPageSize());
        return success(pageResult.convert(this::convert));
    }

    @GetMapping("/wallet")
    @Operation(summary = "获取积分钱包详情")
    @PreAuthorize("@ss.hasPermission('agency:point:list')")
    public CommonResult<AgencyWalletVO> getWallet(
            @Parameter(description = "用户 ID") @RequestParam Long userId) {
        int[] wallet = agencyPointService.getWallet(userId);
        AgencyWalletVO vo = new AgencyWalletVO();
        vo.setBalance(wallet[0]);
        vo.setFrozenBalance(wallet[1]);
        vo.setTotalDistributed(wallet[2]);
        vo.setTotalReceived(wallet[3]);
        return success(vo);
    }

    @GetMapping("/transactions")
    @Operation(summary = "获取积分流水列表")
    @PreAuthorize("@ss.hasPermission('agency:point:list')")
    public CommonResult<PageResult<AgencyPointTransferVO>> getTransactions(PageParam pageParam,
            @Parameter(description = "用户 ID") @RequestParam Long userId) {
        PageResult<AgencyPointTransferDO> pageResult = agencyPointService.getTransactions(userId, pageParam.getPageNo(), pageParam.getPageSize());
        return success(pageResult.convert(this::convert));
    }

    private AgencyPointTransferVO convert(AgencyPointTransferDO entity) {
        if (entity == null) {
            return null;
        }
        AgencyPointTransferVO vo = new AgencyPointTransferVO();
        vo.setId(entity.getId());
        vo.setFromUserId(entity.getFromUserId());
        vo.setToUserId(entity.getToUserId());
        vo.setPointAmount(entity.getPointAmount());
        vo.setDescription(entity.getDescription());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

}
