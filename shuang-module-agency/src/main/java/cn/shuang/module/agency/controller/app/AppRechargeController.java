package cn.shuang.module.agency.controller.app;

import cn.hutool.core.util.StrUtil;
import cn.shuang.framework.common.pojo.CommonResult;
import cn.shuang.framework.common.util.number.MoneyUtils;
import cn.shuang.framework.security.core.util.SecurityFrameworkUtils;
import cn.shuang.module.agency.controller.app.vo.AppRechargeRespVO;
import cn.shuang.module.agency.controller.app.vo.AppRechargeSubmitReqVO;
import cn.shuang.module.agency.service.AgencyUserService;
import cn.shuang.module.pay.api.order.dto.PayOrderCreateReqDTO;
import cn.shuang.module.pay.controller.admin.order.vo.PayOrderSubmitRespVO;
import cn.shuang.module.pay.dal.dataobject.order.PayOrderExtensionDO;
import cn.shuang.module.pay.enums.order.PayOrderStatusEnum;
import cn.shuang.module.pay.service.order.PayOrderService;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static cn.shuang.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - 积分充值
 *
 * @author shuang-pro
 */
@Tag(name = "用户 APP - 积分充值")
@RestController
@RequestMapping("/app/recharge")
@Validated
@Slf4j
public class AppRechargeController {

    private static final String RECHARGE_BIZ_TYPE = "recharge";
    private static final String DEFAULT_APP_KEY = "default";
    private static final int ORDER_EXPIRE_MINUTES = 30;

    @Resource
    private PayOrderService payOrderService;
    @Resource
    private AgencyUserService agencyUserService;

    @GetMapping("/config")
    @Operation(summary = "获取充值配置（比例、支付方式）")
    public CommonResult<AppRechargeRespVO> getRechargeConfig() {
        AppRechargeRespVO vo = new AppRechargeRespVO();
        vo.setPricePerYuan(3);
        vo.setMinAmount(1.0);
        vo.setMaxAmount(10000.0);
        vo.setSupportedChannels(Arrays.asList("wechat", "alipay"));
        return success(vo);
    }

    @PostMapping("/submit")
    @Operation(summary = "提交充值订单")
    public CommonResult<AppRechargeSubmitRespVO> submitRecharge(@RequestBody @Valid AppRechargeSubmitReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        String userIp = cn.shuang.framework.common.util.servlet.ServletUtils.getClientIP();

        // 1. 计算充值金额和积分
        Double amount = calculateAmount(reqVO);
        if (amount <= 0) {
            return CommonResult.error(1, "充值金额必须大于 0");
        }
        int points = calculatePoints(amount);
        String subject = StrUtil.format("积分充值-{}元={}积分", amount, points);

        // 2. 创建支付订单
        PayOrderCreateReqDTO createDTO = new PayOrderCreateReqDTO();
        createDTO.setAppKey(DEFAULT_APP_KEY);
        createDTO.setUserIp(userIp);
        createDTO.setUserId(userId);
        createDTO.setMerchantOrderId(generateMerchantOrderId(userId));
        createDTO.setSubject(StrUtil.sub(subject, 0, 32));
        createDTO.setBody(subject);
        createDTO.setPrice(MoneyUtils.yuanToFen(amount));
        createDTO.setExpireTime(LocalDateTime.now().plusMinutes(ORDER_EXPIRE_MINUTES));

        Long orderId = payOrderService.createOrder(createDTO);

        // 3. 提交支付
        cn.shuang.module.pay.controller.admin.order.vo.PayOrderSubmitReqVO submitReqVO =
            new cn.shuang.module.pay.controller.admin.order.vo.PayOrderSubmitReqVO();
        submitReqVO.setId(orderId);
        submitReqVO.setChannelCode(mapChannelCode(reqVO.getChannelCode()));
        submitReqVO.setReturnUrl("shuang://recharge/callback");

        // 附加充值业务信息到 channelExtras（用于回调时识别并发放积分）
        Map<String, String> extras = Maps.newHashMapWithExpectedSize(4);
        extras.put("bizType", RECHARGE_BIZ_TYPE);
        extras.put("userId", String.valueOf(userId));
        extras.put("points", String.valueOf(points));
        extras.put("amount", String.valueOf(amount));
        submitReqVO.setChannelExtras(extras);

        PayOrderSubmitRespVO orderResp = payOrderService.submitOrder(submitReqVO, userIp);

        // 4. 构建返回
        AppRechargeSubmitRespVO respVO = new AppRechargeSubmitRespVO();
        respVO.setOrderId(orderId);
        respVO.setOrderNo(orderResp.getOrderNo());
        respVO.setAmount(amount);
        respVO.setPoints(points);
        respVO.setChannelCode(reqVO.getChannelCode());
        respVO.setChannelExtras(orderResp.getChannelExtras());
        return success(respVO);
    }

    @GetMapping("/order/status")
    @Operation(summary = "查询充值订单状态")
    public CommonResult<AppRechargeStatusRespVO> getOrderStatus(@RequestParam String orderNo) {
        PayOrderExtensionDO extension = payOrderService.getOrderExtensionByNo(orderNo);
        AppRechargeStatusRespVO respVO = new AppRechargeStatusRespVO();
        respVO.setOrderNo(orderNo);
        if (extension == null) {
            respVO.setStatus("unknown");
            respVO.setStatusText("订单不存在");
            return success(respVO);
        }
        respVO.setStatus(String.valueOf(extension.getStatus()));
        respVO.setStatusText(getStatusText(extension.getStatus()));
        respVO.setPaid(PayOrderStatusEnum.isSuccess(extension.getStatus()));
        return success(respVO);
    }

    @GetMapping("/packages")
    @Operation(summary = "获取充值套餐列表")
    public CommonResult<List<AppRechargeRespVO.RechargePackageVO>> getPackages() {
        List<AppRechargeRespVO.RechargePackageVO> packages = new ArrayList<>();
        packages.add(buildPackage(1L, 9.9, 30, 0, "基础套餐"));
        packages.add(buildPackage(2L, 29.9, 90, 10, "进阶套餐"));
        packages.add(buildPackage(3L, 99.9, 300, 50, "高级套餐"));
        packages.add(buildPackage(4L, 299.9, 900, 200, "尊享套餐"));
        packages.add(buildPackage(5L, 999.9, 3000, 1000, "年度套餐"));
        return success(packages);
    }

    // ========== 私有方法 ==========

    private Double calculateAmount(AppRechargeSubmitReqVO reqVO) {
        if (reqVO.getPackageId() != null) {
            if (reqVO.getPackageId() == 1L) return 9.9;
            if (reqVO.getPackageId() == 2L) return 29.9;
            if (reqVO.getPackageId() == 3L) return 99.9;
            if (reqVO.getPackageId() == 4L) return 299.9;
            if (reqVO.getPackageId() == 5L) return 999.9;
        }
        return reqVO.getCustomAmount() != null ? reqVO.getCustomAmount() : 0.0;
    }

    private int calculatePoints(Double amount) {
        return (int) (amount * 3);
    }

    private String mapChannelCode(String clientCode) {
        // 前端传 wechat -> 支付模块的微信渠道编码
        if ("wechat".equals(clientCode)) return "wx_app";
        if ("alipay".equals(clientCode)) return "alipay_app";
        return clientCode;
    }

    private String generateMerchantOrderId(Long userId) {
        return String.format("R%d%d", userId, System.currentTimeMillis());
    }

    private AppRechargeRespVO.RechargePackageVO buildPackage(Long id, Double price, Integer points, Integer bonus, String name) {
        AppRechargeRespVO.RechargePackageVO pkg = new AppRechargeRespVO.RechargePackageVO();
        pkg.setId(id);
        pkg.setPrice(price);
        pkg.setPoints(points);
        pkg.setBonus(bonus);
        pkg.setName(name);
        return pkg;
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待支付";
            case 10 -> "支付成功";
            case 20 -> "已退款";
            case 30 -> "已关闭";
            default -> "未知";
        };
    }

    // ========== 响应 VO ==========

    @lombok.Data
    public static class AppRechargeSubmitRespVO {
        private Long orderId;
        private String orderNo;
        private Double amount;
        private Integer points;
        private String channelCode;
        private Map<String, String> channelExtras;
    }

    @lombok.Data
    public static class AppRechargeStatusRespVO {
        private String orderNo;
        private String status;
        private String statusText;
        private Boolean paid;
    }
}
