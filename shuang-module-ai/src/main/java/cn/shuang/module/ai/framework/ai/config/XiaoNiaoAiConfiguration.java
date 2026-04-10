package cn.shuang.module.ai.framework.ai.config;

import cn.shuang.module.ai.config.XiaoNiaoProperties;
import cn.shuang.module.ai.framework.ai.core.model.xiaoniao.api.XiaoNiaoApi;
import cn.shuang.module.ai.framework.ai.core.model.xiaoniao.api.XiaoNiaoApiImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 小云雀 AI 视频生成配置类
 * <p>
 * 基于火山引擎即梦AI的小云雀营销成片Agent
 * 文档：https://www.volcengine.com/docs/85621/2283654
 *
 * @author shuang-pro
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(XiaoNiaoProperties.class)
@ConditionalOnProperty(prefix = "yudao.ai.xiaoniao", name = "enable", havingValue = "true", matchIfMissing = false)
public class XiaoNiaoAiConfiguration {

    private final XiaoNiaoProperties properties;
    private final ObjectMapper objectMapper;

    @Bean
    public XiaoNiaoApi xiaoNiaoApi() {
        return new XiaoNiaoApiImpl(properties, objectMapper);
    }
}
