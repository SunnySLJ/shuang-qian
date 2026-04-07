package cn.shuang.module.pay.service;

import cn.shuang.module.pay.dal.dataobject.RechargeOrderDO;

import java.util.List;

/**
 * 充值 Service 接口
 *
 * @author shuang-pro
 */
public interface RechargeService {

    /**
     * 创建充值订单
     * <p>
     * 为用户创建充值订单，生成订单号并设置过期时间
     *
     * @param userId         用户 ID
     * @param amount         充值金额（单位：分）
     * @param paymentMethod  支付方式：1-微信，2-支付宝
     * @param expiredMinutes 过期时间（分钟）
     * @return 订单 ID
     */
    Long createRechargeOrder(Long userId, Integer amount, Integer paymentMethod, Integer expiredMinutes);

    /**
     * 获取订单详情
     *
     * @param orderId 订单 ID
     * @return 订单详情
     */
    RechargeOrderDO getOrderDetail(Long orderId);

    /**
     * 根据订单号获取订单详情
     *
     * @param orderNo 订单号
     * @return 订单详情
     */
    RechargeOrderDO getOrderByOrderNo(String orderNo);

    /**
     * 获取用户的订单列表
     *
     * @param userId 用户 ID
     * @param page   页码
     * @param size   每页大小
     * @return 订单列表
     */
    List<RechargeOrderDO> getUserOrders(Long userId, Integer page, Integer size);

    /**
     * 获取待支付的订单
     *
     * @param userId 用户 ID
     * @return 订单列表
     */
    List<RechargeOrderDO> getPendingOrders(Long userId);

    /**
     * 处理充值回调（通用）
     * <p>
     * 验证回调数据，更新订单状态，增加用户积分，计算代理分成
     *
     * @param orderNo       订单号
     * @param transactionId 第三方支付流水号
     * @param actualAmount  实际支付金额（单位：分）
     * @return 是否成功
     */
    boolean handleRechargeCallback(String orderNo, String transactionId, Integer actualAmount);

    /**
     * 关闭订单
     *
     * @param orderId 订单 ID
     * @return 是否成功
     */
    boolean closeOrder(Long orderId);

    /**
     * 清理过期订单
     *
     * @return 关闭的订单数量
     */
    int closeExpiredOrders();

}
