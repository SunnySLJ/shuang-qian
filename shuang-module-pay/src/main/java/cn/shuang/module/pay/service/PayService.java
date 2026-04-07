package cn.shuang.module.pay.service;

/**
 * 支付 Service 接口
 * <p>
 * 统一处理微信支付和支付宝支付
 *
 * @author shuang-pro
 */
public interface PayService {

    /**
     * 创建支付订单
     * <p>
     * 调用微信/支付宝 API 生成支付参数
     *
     * @param orderNo      订单号
     * @param amount       支付金额（单位：分）
     * @param paymentMethod 支付方式：1-微信，2-支付宝
     * @param openid       用户微信 openid（微信支付需要）
     * @return 支付参数（JSON 字符串）
     */
    String createPayment(String orderNo, Integer amount, Integer paymentMethod, String openid);

    /**
     * 处理支付回调
     * <p>
     * 验证签名，更新订单状态
     *
     * @param paymentMethod 支付方式：1-微信，2-支付宝
     * @param requestBody   回调请求体
     * @return 回调响应
     */
    String handlePaymentCallback(Integer paymentMethod, String requestBody);

    /**
     * 查询订单状态
     *
     * @param orderNo       订单号
     * @param paymentMethod 支付方式
     * @return 订单状态：0-待支付，1-已支付，2-已关闭
     */
    Integer queryOrderStatus(String orderNo, Integer paymentMethod);

    /**
     * 退款
     *
     * @param orderNo       订单号
     * @param refundAmount  退款金额（单位：分）
     * @param paymentMethod 支付方式
     * @param reason        退款原因
     * @return 退款成功标志
     */
    boolean refund(String orderNo, Integer refundAmount, Integer paymentMethod, String reason);

}
