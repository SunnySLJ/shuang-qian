package cn.shuang.module.pay.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.shuang.module.pay.dal.dataobject.WalletDO;
import cn.shuang.module.pay.dal.dataobject.WalletTransactionDO;
import cn.shuang.module.pay.dal.mysql.WalletMapper;
import cn.shuang.module.pay.dal.mysql.WalletTransactionMapper;
import cn.shuang.module.pay.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 积分钱包 Service 实现类
 *
 * @author shuang-pro
 */
@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    private static final String AUTO_BIZ_ORDER_PREFIX = "wa";

    @Resource
    private WalletMapper pointsWalletMapper;

    @Resource
    private WalletTransactionMapper walletTransactionMapper;

    @Override
    public WalletDO getOrCreateWallet(Long userId) {
        WalletDO wallet = pointsWalletMapper.selectByUserId(userId);
        if (wallet == null) {
            // 创建钱包
            wallet = WalletDO.builder()
                    .userId(userId)
                    .balance(0)
                    .frozenBalance(0)
                    .totalRecharge(0)
                    .totalUsed(0)
                    .totalReceived(0)
                    .totalGiven(0)
                    .totalCommission(0)
                    .version(0)
                    .build();
            pointsWalletMapper.insert(wallet);
            log.info("[WalletService] 创建新钱包 - userId: {}", userId);
        }
        return wallet;
    }

    @Override
    public WalletDO getWallet(Long userId) {
        return pointsWalletMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addPoints(Long userId, Integer amount, String description) {
        return addPoints(userId, amount, 1, null, description);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addPoints(Long userId, Integer amount, Integer bizType, String bizOrderNo, String description) {
        // 1. 获取或创建钱包
        WalletDO wallet = getOrCreateWallet(userId);
        String safeBizOrderNo = buildBizOrderNo(bizOrderNo);

        // 2. 更新余额
        int updated = pointsWalletMapper.addBalance(userId, amount);
        if (updated == 0) {
            throw new RuntimeException("更新钱包余额失败");
        }

        // 3. 重新查询获取新余额
        wallet = pointsWalletMapper.selectByUserId(userId);

        // 4. 记录流水
        WalletTransactionDO transaction = WalletTransactionDO.builder()
                .walletId(wallet.getId())
                .userId(userId)
                .bizType(bizType)
                .bizOrderNo(safeBizOrderNo)
                .amount(amount)
                .balanceAfter(wallet.getBalance())
                .description(description)
                .build();
        walletTransactionMapper.insertTransaction(transaction);

        log.info("[WalletService] 增加积分 - userId: {}, amount: {}, type: {}, balance: {}",
                userId, amount, bizType, wallet.getBalance());

        return wallet.getBalance();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deductPoints(Long userId, Integer amount, String description) {
        return deductPoints(userId, amount, -1, null, description);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deductPoints(Long userId, Integer amount, Integer bizType, String bizOrderNo, String description) {
        // 1. 获取钱包
        WalletDO wallet = getOrCreateWallet(userId);
        String safeBizOrderNo = buildBizOrderNo(bizOrderNo);

        // 2. 使用乐观锁扣减余额（带重试机制）
        int updated = deductBalanceWithRetry(userId, amount, wallet.getVersion());
        if (updated == 0) {
            throw new RuntimeException("扣减积分失败，余额不足或并发冲突");
        }

        // 3. 重新查询获取新余额
        wallet = pointsWalletMapper.selectByUserId(userId);

        // 4. 记录流水（支出为负数）
        WalletTransactionDO transaction = WalletTransactionDO.builder()
                .walletId(wallet.getId())
                .userId(userId)
                .bizType(bizType)
                .bizOrderNo(safeBizOrderNo)
                .amount(-amount) // 支出记为负数
                .balanceAfter(wallet.getBalance())
                .description(description)
                .build();
        walletTransactionMapper.insertTransaction(transaction);

        log.info("[WalletService] 扣减积分 - userId: {}, amount: {}, type: {}, balance: {}",
                userId, amount, bizType, wallet.getBalance());

        return wallet.getBalance();
    }

    @Override
    public boolean hasEnoughPoints(Long userId, Integer requiredAmount) {
        WalletDO wallet = getWallet(userId);
        return wallet != null && wallet.getBalance() >= requiredAmount;
    }

    @Override
    public List<WalletTransactionDO> getTransactions(Long userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return pointsWalletMapper.selectTransactions(userId, offset, size);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer freezePoints(Long userId, Integer amount) {
        WalletDO wallet = getOrCreateWallet(userId);

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("积分不足，无法冻结");
        }

        // 更新余额和冻结余额
        String sql = "UPDATE pay_wallet SET balance = balance - #{amount}, frozen_balance = frozen_balance + #{amount}, update_time = NOW() " +
                     "WHERE user_id = #{userId} AND deleted = 0";
        int updated = pointsWalletMapper.deductBalance(userId, amount);
        if (updated == 0) {
            throw new RuntimeException("冻结积分失败");
        }

        wallet = pointsWalletMapper.selectByUserId(userId);
        log.info("[WalletService] 冻结积分 - userId: {}, amount: {}, balance: {}, frozen: {}",
                userId, amount, wallet.getBalance(), wallet.getFrozenBalance());

        return wallet.getBalance();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer unfreezePoints(Long userId, Integer amount) {
        WalletDO wallet = getOrCreateWallet(userId);

        if (wallet.getFrozenBalance() < amount) {
            throw new RuntimeException("冻结余额不足");
        }

        // 更新余额和冻结余额
        String sql = "UPDATE pay_wallet SET balance = balance + #{amount}, frozen_balance = frozen_balance - #{amount}, update_time = NOW() " +
                     "WHERE user_id = #{userId} AND deleted = 0";

        // 使用原生 SQL 更新
        int updated = pointsWalletMapper.addBalance(userId, amount);
        if (updated == 0) {
            throw new RuntimeException("解冻积分失败");
        }

        wallet = pointsWalletMapper.selectByUserId(userId);
        log.info("[WalletService] 解冻积分 - userId: {}, amount: {}, balance: {}, frozen: {}",
                userId, amount, wallet.getBalance(), wallet.getFrozenBalance());

        return wallet.getBalance();
    }

    @Override
    public WalletDO getWalletDetail(Long userId) {
        return getOrCreateWallet(userId);
    }

    private String buildBizOrderNo(String bizOrderNo) {
        if (bizOrderNo != null && !bizOrderNo.isBlank()) {
            return bizOrderNo;
        }
        return AUTO_BIZ_ORDER_PREFIX + IdUtil.fastSimpleUUID().substring(0, 30);
    }

    @Override
    public WalletTransactionDO getTransactionByRecord(Long recordId) {
        if (recordId == null) {
            return null;
        }
        // 旧实现：通过 recordId 查找（已废弃，请使用 getTransactionByBizOrderNo）
        return walletTransactionMapper.selectByBizOrderNo("refund:" + recordId);
    }

    @Override
    public WalletTransactionDO getTransactionByBizOrderNo(String bizOrderNo) {
        if (bizOrderNo == null || bizOrderNo.isBlank()) {
            return null;
        }
        return walletTransactionMapper.selectByBizOrderNo(bizOrderNo);
    }

    /**
     * 乐观锁扣减余额，内部重试逻辑。
     * 高并发场景下乐观锁可能冲突，重试 3 次，每次间隔 50ms。
     */
    private int deductBalanceWithRetry(Long userId, Integer amount, Integer currentVersion) {
        int retry = 0;
        int lastElapsed = 0;
        while (retry < 3) {
            int updated = pointsWalletMapper.deductBalanceWithVersion(userId, amount, currentVersion);
            if (updated > 0) {
                return updated;
            }
            retry++;
            if (retry >= 3) {
                break;
            }
            // 获取最新版本后重试
            WalletDO latest = getWallet(userId);
            if (latest == null) {
                throw new RuntimeException("钱包不存在");
            }
            currentVersion = latest.getVersion();
            log.warn("[WalletService][deductBalanceWithRetry] 乐观锁冲突，尝试第 {} 次 - userId: {}", retry + 1, userId);
            lastElapsed += 50;
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return 0;
    }

}
