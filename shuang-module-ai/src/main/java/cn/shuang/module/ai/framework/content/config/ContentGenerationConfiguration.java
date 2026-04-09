package cn.shuang.module.ai.framework.content.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 内容生成模块配置
 *
 * 使用 @ConditionalOnProperty 控制内容生成模块的启用
 * 通过 @Qualifier 指定具体的 ChatModel Bean
 */
@Configuration
@ConditionalOnProperty(value = "yudao.ai.content-generation.enable", havingValue = "true", matchIfMissing = false)
public class ContentGenerationConfiguration {

    /**
     * 内容生成专用的 ChatClient
     * 使用 openAiChatModel 作为默认模型（舞墨 API 兼容 OpenAI 协议）
     */
    @Bean
    @Primary
    public ChatClient contentGenerationChatClient(ChatModel openAiChatModel) {
        return ChatClient.create(openAiChatModel);
    }
}
