package cn.shuang.module.member.config;

import cn.shuang.framework.common.biz.ShuangProProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 追梦 Dream 业务配置
 *
 * @author shuang-pro
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ShuangProProperties.class)
public class ShuangProConfiguration {

    @Bean
    public PointsCostService pointsCostService(ShuangProProperties properties) {
        return new PointsCostService(properties);
    }
}
