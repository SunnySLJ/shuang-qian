package cn.shuang.module.pay.service.impl;

import cn.shuang.module.pay.dal.dataobject.RechargeOrderDO;
import cn.shuang.module.pay.dal.mysql.RechargeOrderMapper;
import cn.shuang.module.pay.service.RechargeService;
import cn.shuang.module.pay.service.WalletService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 充值 Service 实现类
 *
 * @author shuang-pro
 */
@Service
@Slf4j
public class RechargeServiceImpl implements RechargeService {

    @Resource
    private RechargeOrderMapper rechargeOrderMapper;

    @Resource
    private WalletService walletService;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 积分兑换比例：1 元 = 100 积分
     */
    private static final int POINTS_PER_YUAN = 100;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRechargeOrder(Long userId, Integer amount, Integer paymentMethod, Integer expiredMinutes) {
        log.info("[RechargeService] 开始创建充值订单 - userId: {}, amount: {}, paymentMethod: {}",
                userId, amount, paymentMethod);

        // 1. 参数校验
        if (amount <= 0) {
            throw new IllegalArgumentException("充值金额必须大于 0");
        }
        if (paymentMethod == null || (paymentMethod != 1 && paymentMethod != 2)) {
            throw new IllegalArgumentException("支付方式不支持");
        }

        // 2. 计算积分
        int points = amount * POINTS_PER_YUAN / 100; // 1 元=100 积分
        int bonusPoints = 0; // 赠送积分（暂不赠送）
        int totalPoints = points + bonusPoints;

        // 3. 生成订单号
        String orderNo = generateOrderNo();

        // 4. 创建订单
        LocalDateTime expiredTime = (expiredMinutes != null && expiredMinutes > 0)
                ? LocalDateTime.now().plusMinutes(expiredMinutes)
                : LocalDateTime.now().plusMinutes(30); // 默认 30 分钟过期

        RechargeOrderDO order = RechargeOrderDO.builder()
                .orderNo(orderNo)
                .userId(userId)
                .amount(amount)
                .points(points)
                .bonusPoints(bonusPoints)
                .totalPoints(totalPoints)
                .paymentMethod(paymentMethod)
                .status(0) // 待支付
                .expiredTime(expiredTime)
                .remark("充值订单")
                .build();

        try {
            // 保存扩展数据
            order.setExtraData(objectMapper.writeValueAsString(java.util.Map.of(
                    "points", points,
                    "bonusPoints", bonusPoints,
                    "totalPoints", totalPoints
            )));
        } catch (JsonProcessingException e) {
            log.warn("[RechargeService] 序列化扩展数据失败", e);
        }

        rechargeOrderMapper.insert(order);

        log.info("[RechargeService] 充值订单创建成功 - orderId: {}, orderNo: {}, userId: {}, amount: {}, totalPoints: {}",
                order.getId(), orderNo, userId, amount, totalPoints);

        return order.getId();
    }

    @Override
    public RechargeOrderDO getOrderDetail(Long orderId) {
        return rechargeOrderMapper.selectById(orderId);
    }

    @Override
    public RechargeOrderDO getOrderByOrderNo(String orderNo) {
        return rechargeOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public List<RechargeOrderDO> getUserOrders(Long userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return rechargeOrderMapper.selectByUserId(userId, offset, size);
    }

    @Override
    public List<RechargeOrderDO> getPendingOrders(Long userId) {
        return rechargeOrderMapper.selectPendingOrders(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleRechargeCallback(String orderNo, String transactionId, Integer actualAmount) {
        log.info("[RechargeService] 开始处理充值回调 - orderNo: {}, transactionId: {}, actualAmount: {}",
                orderNo, transactionId, actualAmount);

        // 1. 查询订单
        RechargeOrderDO order = rechargeOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            log.error("[RechargeService] 订单不存在 - orderNo: {}", orderNo);
            return false;
        }

        // 2. 检查订单状态
        if (order.getStatus() != 0) {
            log.warn("[RechargeService] 订单状态异常，跳过 - orderId: {}, status: {}", order.getId(), order.getStatus());
            return order.getStatus() == 1; // 已支付则返回成功
        }

        // 3. 检查订单是否过期
        if (order.getExpiredTime() != null && order.getExpiredTime().isBefore(LocalDateTime.now())) {
            log.warn("[RechargeService] 订单已过期 - orderId: {}, expiredTime: {}", order.getId(), order.getExpiredTime());
            rechargeOrderMapper.closeExpiredOrders(LocalDateTime.now());
            return false;
        }

        // 4. 验证支付金额（可选，严格模式下启用）
        // if (!order.getAmount().equals(actualAmount)) {
        //     log.error("[RechargeService] 支付金额不匹配 - orderId: {}, expected: {}, actual: {}",
        //             order.getId(), order.getAmount(), actualAmount);
        //     return false;
        // }

        // 5. 更新订单状态
        rechargeOrderMapper.updateStatus(order.getId(), 1, transactionId, LocalDateTime.now());

        // 6. 增加用户钱包积分
        try {
            walletService.addPoints(order.getUserId(), order.getTotalPoints(), 1, orderNo,
                    String.format("充值获得积分 - 订单：%s", orderNo));
            log.info("[RechargeService] 充值积分成功 - userId: {}, points: {}", order.getUserId(), order.getTotalPoints());
        } catch (Exception e) {
            log.error("[RechargeService] 增加钱包积分失败 - userId: {}, points: {}", order.getUserId(), order.getTotalPoints(), e);
            // 回滚订单状态
            rechargeOrderMapper.updateStatus(order.getId(), 0, null, null);
            throw e;
        }

        // 7. 计算代理分成（如果有上级代理）
        try {
            // 通过 ApplicationContext 动态获取 CommissionService（避免循环依赖）
            // 使用字符串方式获取，避免编译时依赖 agency 模块
            Object commissionService = applicationContext.getBean("commissionServiceImpl");
            if (commissionService != null) {
                // 使用反射调用 calculateRechargeCommission 方法
                java.lang.reflect.Method method = commissionService.getClass()
                    .getMethod("calculateRechargeCommission", Long.class, Integer.class, String.class);
                Long commissionRecordId = (Long) method.invoke(commissionService,
                    order.getUserId(), order.getTotalPoints(), orderNo);
                if (commissionRecordId != null) {
                    log.info("[RechargeService] 代理分成计算成功 - userId: {}, commissionRecordId: {}",
                            order.getUserId(), commissionRecordId);
                }
            }
        } catch (Exception e) {
            // CommissionService 可能不存在（当 agency 模块未加载时），静默处理
            log.debug("[RechargeService] 计算代理分成失败或 CommissionService 不存在 - userId: {}", order.getUserId(), e);
            // 分成失败不影响主流程，仅记录日志
        }

        log.info("[RechargeService] 充值回调处理完成 - orderId: {}, userId: {}, points: {}",
                order.getId(), order.getUserId(), order.getTotalPoints());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeOrder(Long orderId) {
        RechargeOrderDO order = rechargeOrderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 0) {
            log.warn("[RechargeService] 订单不存在或已处理 - orderId: {}, status: {}", orderId,
                    order != null ? order.getStatus() : "N/A");
            return false;
        }

        order.setStatus(2); // 已关闭
        rechargeOrderMapper.updateById(order);

        log.info("[RechargeService] 订单已关闭 - orderId: {}", orderId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int closeExpiredOrders() {
        int closedCount = rechargeOrderMapper.closeExpiredOrders(LocalDateTime.now());
        if (closedCount > 0) {
            log.info("[RechargeService] 批量关闭过期订单 - count: {}", closedCount);
        }
        return closedCount;
    }

    /**
     * 生成订单号
     * 格式：R + yyyyMMddHHmmss + UUID(8 位)
     */
    private String generateOrderNo() {
        String timestamp = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .format(LocalDateTime.now());
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "R" + timestamp + uuid;
    }

}
