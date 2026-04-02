package cn.shuang.module.pay.service.impl;

import cn.shuang.module.pay.dal.dataobject.RechargeOrderDO;
import cn.shuang.module.pay.dal.mysql.RechargeOrderMapper;
import cn.shuang.module.pay.service.PayService;
import cn.shuang.module.pay.service.RechargeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付 Service 实现类
 * <p>
 * 当前为 MOCK 实现，真实环境需要替换为微信/支付宝官方 SDK
 *
 * @author shuang-pro
 */
@Service
@Slf4j
public class PayServiceImpl implements PayService {

    @Resource
    private RechargeOrderMapper rechargeOrderMapper;

    @Resource
    private RechargeService rechargeService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public String createPayment(String orderNo, Integer amount, Integer paymentMethod, String openid) {
        log.info("[PayService] 创建支付订单 - orderNo: {}, amount: {}, paymentMethod: {}, openid: {}",
                orderNo, amount, paymentMethod, openid);

        // 查询订单
        RechargeOrderDO order = rechargeOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        if (order.getStatus() != 0) {
            throw new IllegalStateException("订单状态异常：" + order.getStatus());
        }

        // 生成支付参数（MOCK 实现）
        Map<String, Object> payParams = new HashMap<>();
        payParams.put("orderNo", orderNo);
        payParams.put("amount", amount);
        payParams.put("paymentMethod", paymentMethod);

        if (paymentMethod == 1) {
            // 微信支付参数
            payParams.put("appid", "wx_mock_appid");
            payParams.put("mchid", "mock_mchid");
            payParams.put("nonce_str", "mock_nonce_str");
            payParams.put("sign", "mock_sign");
            payParams.put("prepay_id", "mock_prepay_id");
        } else if (paymentMethod == 2) {
            // 支付宝支付参数
            payParams.put("out_trade_no", orderNo);
            payParams.put("total_amount", amount / 100.0); // 转为元
            payParams.put("subject", "积分充值");
            payParams.put("product_code", "QUICK_MSECURITY_PAY");
        }

        try {
            String result = objectMapper.writeValueAsString(payParams);
            log.info("[PayService] 支付参数生成成功 - orderNo: {}", orderNo);
            return result;
        } catch (Exception e) {
            log.error("[PayService] 序列化支付参数失败", e);
            throw new RuntimeException("生成支付参数失败");
        }
    }

    @Override
    public String handlePaymentCallback(Integer paymentMethod, String requestBody) {
        log.info("[PayService] 处理支付回调 - paymentMethod: {}, requestBody: {}", paymentMethod, requestBody);

        // MOCK 实现：解析回调数据并处理
        // 真实环境需要验证签名、解密数据等

        try {
            Map<String, Object> callbackData = objectMapper.readValue(requestBody, Map.class);

            String orderNo = (String) callbackData.get("out_trade_no");
            String transactionId = (String) callbackData.get("transaction_id");
            Integer totalAmount = ((Number) callbackData.get("total_amount")).intValue();

            // 处理充值回调
            boolean success = rechargeService.handleRechargeCallback(orderNo, transactionId, totalAmount * 100);

            Map<String, Object> response = new HashMap<>();
            if (paymentMethod == 1) {
                // 微信支付响应格式
                response.put("code", success ? "SUCCESS" : "FAIL");
                response.put("message", success ? "成功" : "失败");
            } else {
                // 支付宝响应格式
                response.put("success", success);
            }

            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            log.error("[PayService] 处理支付回调失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "FAIL");
            errorResponse.put("message", "处理失败");
            try {
                return objectMapper.writeValueAsString(errorResponse);
            } catch (Exception ex) {
                return "{\"code\":\"FAIL\",\"message\":\"处理失败\"}";
            }
        }
    }

    @Override
    public Integer queryOrderStatus(String orderNo, Integer paymentMethod) {
        log.info("[PayService] 查询订单状态 - orderNo: {}, paymentMethod: {}", orderNo, paymentMethod);

        RechargeOrderDO order = rechargeOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return null;
        }

        // MOCK 实现：直接返回订单状态
        // 真实环境需要调用微信/支付宝查询接口
        return order.getStatus();
    }

    @Override
    public boolean refund(String orderNo, Integer refundAmount, Integer paymentMethod, String reason) {
        log.info("[PayService] 退款 - orderNo: {}, refundAmount: {}, paymentMethod: {}, reason: {}",
                orderNo, refundAmount, paymentMethod, reason);

        // MOCK 实现：直接返回成功
        // 真实环境需要调用微信/支付宝退款接口

        RechargeOrderDO order = rechargeOrderMapper.selectByOrderNo(orderNo);
        if (order == null || order.getStatus() != 1) {
            log.warn("[PayService] 订单不存在或未支付 - orderNo: {}", orderNo);
            return false;
        }

        // TODO: 调用微信/支付宝退款 API
        // 退款成功后更新订单状态为已退款（3）

        log.info("[PayService] 退款成功（MOCK） - orderNo: {}, refundAmount: {}", orderNo, refundAmount);
        return true;
    }

}
