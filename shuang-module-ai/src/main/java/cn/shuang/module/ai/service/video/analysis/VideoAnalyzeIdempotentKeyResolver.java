package cn.shuang.module.ai.service.video.analysis;

import cn.hutool.core.lang.hash.MurmurHash;
import cn.hutool.core.util.StrUtil;

/**
 * 视频分析幂等 key 构建器。
 * <p>
 * key 格式：video_analyze:{videoHash}:{providerCode}:{analyzeType}:{clientId}
 * - videoHash: MurmurHash3(64bit) 保证 JVM 间一致性，替代不稳定的 String.hashCode()
 * - providerCode: AI Provider 代号（aliyun/doubao/wumo）
 * - analyzeType: SCRIPT / ELEMENTS / PROMPT
 * - clientId: 前端生成，用于同设备防重（可为空）
 * <p>
 * 设计要点：
 * - 同视频不同 analyzeType 互不干扰（SCRIPT 和 ELEMENTS 是完全不同的操作）
 * - 同视频同 analyzeType 相同 clientId 视为重复请求
 * - 相同 videoUrl 在不同 JVM 重启后 hash 值稳定（MurmurHash3 vs Object.hashCode）
 */
public class VideoAnalyzeIdempotentKeyResolver {

    private static final String PREFIX = "video_analyze";

    /**
     * 供 SpEL 通过 T() 调用，生成幂等 key。
     *
     * @param videoUrl     视频 URL
     * @param providerCode Provider 代号
     * @param analyzeType  分析类型（SCRIPT/ELEMENTS/PROMPT）
     * @param clientId     客户端 ID
     * @return 幂等 key
     */
    public static String buildKey(String videoUrl, String providerCode, String analyzeType, String clientId) {
        String safeUrl = StrUtil.blankToDefault(videoUrl, "");
        String safeProvider = StrUtil.blankToDefault(providerCode, VideoAnalyzeProvider.ALIYUN.getCode());
        String safeAnalyzeType = StrUtil.blankToDefault(analyzeType, "UNKNOWN");
        String safeClientId = StrUtil.blankToDefault(clientId, "");

        // 使用 MurmurHash3 替代 String.hashCode()，保证 JVM 间一致
        String hash = String.valueOf(MurmurHash.hash64(safeUrl));

        return String.format("%s:%s:%s:%s:%s", PREFIX, hash, safeProvider, safeAnalyzeType, safeClientId);
    }
}
