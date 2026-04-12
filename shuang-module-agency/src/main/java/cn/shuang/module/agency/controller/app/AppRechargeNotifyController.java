package cn.shuang.module.agency.controller.app;

import cn.hutool.core.util.StrUtil;
import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.module.agency.service.AgencyUserService;
import cn.shuang.module.agency.service.CommissionService;
import cn.shuang.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
import cn.shuang.module.pay.dal.dataobject.order.PayOrderExtensionDO;
import cn.shuang.module.pay.enums.order.PayOrderStatusEnum;
import cn.shuang.module.pay.service.WalletService;
import cn.shuang.module.pay.service.order.PayOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.shuang.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - 积分充值回调
 * <p>
 * 支付成功后，支付模块会回调此接口，触发积分发放
 *
 * @author shuang-pro
 */
@Tag(name = "用户 APP - 积分充值回调")
@RestController
@RequestMapping("/notify/recharge")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AppRechargeNotifyController {

    private static final String RECHARGE_BIZ_TYPE = "recharge";

    private final PayOrderService payOrderService;
    private final AgencyUserService agencyUserService;
    private final CommissionService commissionService;
    private final WalletService walletService;

    @PostMapping("/order")
    @Operation(summary = "充值支付成功回调")
    public CommonResult<?> notifyRechargeOrder(@RequestBody PayOrderNotifyReqDTO reqDTO) {
        log.info("[notifyRechargeOrder] 收到充值回调，merchantOrderId={}", reqDTO.getMerchantOrderId());

        // 1. 根据商户订单号查找支付扩展单
        PayOrderExtensionDO extension = payOrderService.getOrderExtensionByNo(reqDTO.getMerchantOrderId());
        if (extension == null) {
            log.warn("[notifyRechargeOrder] 订单 {} 不存在", reqDTO.getMerchantOrderId());
            return CommonResult.error(1, "订单不存在");
        }

        // 2. 必须是充值类型的订单
        if (extension.getChannelExtras() == null
                || !RECHARGE_BIZ_TYPE.equals(extension.getChannelExtras().get("bizType"))) {
            log.info("[notifyRechargeOrder] 订单 {} 不是充值订单，跳过", reqDTO.getMerchantOrderId());
            return success(null);
        }

        // 3. 检查是否已处理（幂等）
        if (PayOrderStatusEnum.isSuccess(extension.getStatus())) {
            log.info("[notifyRechargeOrder] 订单 {} 已处理过，跳过", reqDTO.getMerchantOrderId());
            return success(null);
        }

        // 4. 解析充值信息并发放积分
        Map<String, String> extras = extension.getChannelExtras();
        Long userId = Long.valueOf(extras.get("userId"));
        Integer points = Integer.valueOf(extras.get("points"));
        Double amount = Double.valueOf(extras.get("amount"));

        // 4.1 发放积分到用户钱包（bizType=2 表示充值收入）
        walletService.addPoints(userId, points, 2,
                reqDTO.getMerchantOrderId(),
                StrUtil.format("积分充值 {} 元，获得 {} 积分", amount, points));

        // 4.2 给上级代理分佣（一级+二级，使用统一的 CommissionService）
        // bizOrderNo 复用支付订单号，幂等由 CommissionService 内部保证
        try {
            commissionService.calculateRechargeCommission(userId, points, reqDTO.getMerchantOrderId());
        } catch (Exception e) {
            // 分佣失败不影响积分发放，只记录日志
            log.error("[notifyRechargeOrder] 分佣失败，不阻塞积分发放 - userId: {}, orderNo: {}",
                    userId, reqDTO.getMerchantOrderId(), e);
        }

        log.info("[notifyRechargeOrder] 用户 {} 充值 {} 元发放 {} 积分成功", userId, amount, points);
        return success(null);
    }
}
