package cn.shuang.module.member.controller.app.point;

import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.module.pay.dal.dataobject.WalletDO;
import cn.shuang.module.pay.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import static cn.shuang.framework.common.pojo.CommonResult.success;
import static cn.shuang.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 用户 APP - 积分钱包
 *
 * @author shuang-pro
 */
@Tag(name = "用户 APP - 积分钱包")
@RestController
@RequestMapping("/member/point")
@Validated
@Slf4j
public class AppMemberPointWalletController {

    @Resource
    private WalletService walletService;

    @GetMapping("/wallet")
    @Operation(summary = "获取我的积分钱包")
    public CommonResult<WalletInfoRespVO> getMyWallet() {
        Long userId = getLoginUserId();
        WalletDO wallet = walletService.getOrCreateWallet(userId);

        WalletInfoRespVO vo = new WalletInfoRespVO();
        vo.setBalance(wallet.getBalance());
        vo.setFrozenBalance(wallet.getFrozenBalance());
        vo.setTotalRecharge(wallet.getTotalRecharge());
        vo.setTotalUsed(wallet.getTotalUsed());

        return success(vo);
    }

    @Data
    public static class WalletInfoRespVO {
        /** 积分余额（单位：分） */
        private Integer balance;
        /** 冻结积分（单位：分） */
        private Integer frozenBalance;
        /** 累计充值（单位：分） */
        private Integer totalRecharge;
        /** 累计消耗（单位：分） */
        private Integer totalUsed;
    }
}
