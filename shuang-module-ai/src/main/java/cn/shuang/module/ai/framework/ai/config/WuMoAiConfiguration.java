package cn.shuang.module.ai.framework.ai.config;

import cn.shuang.module.ai.framework.ai.core.model.wumo.api.WuMoApi;
import cn.shuang.module.ai.framework.ai.core.model.wumo.api.WuMoApiImpl;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 舞墨 AI 配置类
 *
 * @author shuang-pro
 */
@Configuration
@ConfigurationProperties(prefix = "yudao.ai.wumo")
@Data
public class WuMoAiConfiguration {

    /**
     * API Key
     */
    private String apiKey;

    /**
     * API 基础 URL
     */
    private String baseUrl = "https://api.wuyinkeji.com";

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 默认轮询间隔（毫秒）
     */
    private Long pollInterval = 3000L;

    /**
     * 默认最大等待时间（毫秒）
     */
    private Long maxWaitTime = 300000L;

    @Bean
    public WuMoApi wuMoApi() {
        return new WuMoApiImpl(apiKey, baseUrl);
    }

}
