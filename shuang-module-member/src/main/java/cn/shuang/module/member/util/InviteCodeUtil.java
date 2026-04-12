package cn.shuang.module.member.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 邀请码编解码工具
 *
 * 邀请码格式: Base64("AGENT_" + userId)
 * 例如: userId=123 -> AGENT_123 -> Base64 -> "QUdFTlRfMTIz"
 *
 * @author shuang-pro
 */
@Slf4j
public final class InviteCodeUtil {

    private static final String PREFIX = "AGENT_";

    /**
     * 将 userId 编码为邀请码
     */
    public static String encode(Long userId) {
        if (userId == null) {
            return null;
        }
        String raw = PREFIX + userId;
        return Base64.encode(raw);
    }

    /**
     * 将邀请码解码为 userId
     *
     * @param code 邀请码
     * @return userId，解码失败返回 null
     */
    public static Long decode(String code) {
        if (StrUtil.isBlank(code)) {
            return null;
        }
        try {
            String decoded = Base64.decodeStr(code);
            if (!decoded.startsWith(PREFIX)) {
                log.warn("[decode] 邀请码 {} 前缀不匹配，期望 {}, 实际 {}", code, PREFIX, decoded);
                return null;
            }
            String idStr = decoded.substring(PREFIX.length());
            return Long.parseLong(idStr);
        } catch (Exception e) {
            log.warn("[decode] 邀请码 {} 解码失败: {}", code, e.getMessage());
            return null;
        }
    }

    /**
     * 校验邀请码格式是否合法
     */
    public static boolean isValid(String code) {
        return decode(code) != null;
    }
}