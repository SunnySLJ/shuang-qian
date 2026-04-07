package cn.shuang.module.ai.framework.content.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 内容生成模块配置
 */
@Configuration
public class ContentGenerationConfiguration {

    /**
     * 提供 ChatClient.Builder 用于 Agent 的 LLM 调用
     * 使用 @ConditionalOnMissingBean 确保只有在没有自定义 Bean 时才创建
     */
    @Bean
    @ConditionalOnMissingBean
    public ChatClient.Builder chatClientBuilder(ChatModel chatModel) {
        return ChatClient.builder(chatModel);
    }
}
