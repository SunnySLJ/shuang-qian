package cn.shuang.module.pay.service.impl;

import cn.shuang.module.pay.dal.dataobject.WalletDO;
import cn.shuang.module.pay.dal.dataobject.WalletTransactionDO;
import cn.shuang.module.pay.dal.mysql.WalletMapper;
import cn.shuang.module.pay.dal.mysql.WalletTransactionMapper;
import cn.shuang.module.pay.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 积分钱包 Service 实现类
 *
 * @author shuang-pro
 */
@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    @Resource
    private WalletMapper walletMapper;

    @Resource
    private WalletTransactionMapper walletTransactionMapper;

    @Override
    public WalletDO getOrCreateWallet(Long userId) {
        WalletDO wallet = walletMapper.selectByUserId(userId);
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
                    .build();
            walletMapper.insert(wallet);
            log.info("[WalletService] 创建新钱包 - userId: {}", userId);
        }
        return wallet;
    }

    @Override
    public WalletDO getWallet(Long userId) {
        return walletMapper.selectByUserId(userId);
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

        // 2. 更新余额
        int updated = walletMapper.addBalance(userId, amount);
        if (updated == 0) {
            throw new RuntimeException("更新钱包余额失败");
        }

        // 3. 重新查询获取新余额
        wallet = walletMapper.selectByUserId(userId);

        // 4. 记录流水
        WalletTransactionDO transaction = WalletTransactionDO.builder()
                .walletId(wallet.getId())
                .userId(userId)
                .bizType(bizType)
                .bizOrderNo(bizOrderNo)
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

        // 2. 检查余额是否足够
        if (wallet.getBalance() < amount) {
            throw new RuntimeException("积分不足，当前余额：" + wallet.getBalance() + "，需要：" + amount);
        }

        // 3. 扣减余额
        int updated = walletMapper.deductBalance(userId, amount);
        if (updated == 0) {
            throw new RuntimeException("扣减积分失败，余额不足");
        }

        // 4. 重新查询获取新余额
        wallet = walletMapper.selectByUserId(userId);

        // 5. 记录流水（支出为负数）
        WalletTransactionDO transaction = WalletTransactionDO.builder()
                .walletId(wallet.getId())
                .userId(userId)
                .bizType(bizType)
                .bizOrderNo(bizOrderNo)
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
        return walletMapper.selectTransactions(userId, offset, size);
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
        int updated = walletMapper.deductBalance(userId, amount);
        if (updated == 0) {
            throw new RuntimeException("冻结积分失败");
        }

        wallet = walletMapper.selectByUserId(userId);
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
        int updated = walletMapper.addBalance(userId, amount);
        if (updated == 0) {
            throw new RuntimeException("解冻积分失败");
        }

        wallet = walletMapper.selectByUserId(userId);
        log.info("[WalletService] 解冻积分 - userId: {}, amount: {}, balance: {}, frozen: {}",
                userId, amount, wallet.getBalance(), wallet.getFrozenBalance());

        return wallet.getBalance();
    }

    @Override
    public WalletDO getWalletDetail(Long userId) {
        return getOrCreateWallet(userId);
    }

}
