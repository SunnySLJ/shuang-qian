package cn.shuang.module.ai.service.video.analysis;

import cn.hutool.core.util.StrUtil;

/**
 * 视频拆解供应商类型。
 */
public enum VideoAnalyzeProvider {

    ALIYUN("aliyun"),
    DOUBAO("doubao"),
    WUMO("wumo");

    private final String code;

    VideoAnalyzeProvider(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static VideoAnalyzeProvider fromCode(String code, VideoAnalyzeProvider defaultValue) {
        if (StrUtil.isBlank(code)) {
            return defaultValue;
        }
        for (VideoAnalyzeProvider provider : values()) {
            if (provider.code.equalsIgnoreCase(code)) {
                return provider;
            }
        }
        if (defaultValue == null) {
            throw new IllegalArgumentException("不支持的视频拆解供应商: " + code);
        }
        return defaultValue;
    }

    public static VideoAnalyzeProvider fromCodeOrNull(String code) {
        return fromCode(code, null);
    }

}
