package cn.shuang.module.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 视频拆解供应商配置。
 */
@Configuration
@ConfigurationProperties(prefix = "yudao.ai.video-analyze")
@Data
public class VideoAnalyzeProperties {

    /**
     * 默认供应商：aliyun / doubao / wumo
     */
    private String defaultProvider = "aliyun";

    /**
     * 公共请求超时（秒）
     */
    private int timeoutSeconds = 180;

    private Provider aliyun = new Provider();

    private Provider doubao = new Provider();

    /**
     * 备用供应商（主供应商失败后尝试）
     */
    private String fallbackProvider = "doubao";

    @Data
    public static class Provider {

        /**
         * 是否启用
         */
        private boolean enabled;

        /**
         * API Key
         */
        private String apiKey;

        /**
         * Base URL，不包含 /chat/completions 时也可自动拼接
         */
        private String baseUrl;

        /**
         * 模型名
         */
        private String model;
    }

}
