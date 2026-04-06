package cn.shuang.module.ai.framework.content.agent;

import cn.shuang.module.ai.framework.content.context.ContentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.List;

/**
 * 所有内容生成 Agent 的基类
 * <p>
 * 参考 Claw-AI-Lab 的 BaseAgent 设计，提供统一的 LLM 调用和 JSON 解析能力
 */
@Slf4j
public abstract class BaseContentAgent {

    /**
     * Agent 名称
     */
    protected final String name;

    /**
     * Spring AI ChatClient
     */
    protected final ChatClient chatClient;

    public BaseContentAgent(String name, ChatClient.Builder chatClientBuilder) {
        this.name = name;
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 执行 Agent 的核心任务
     * <p>
     * 子类必须实现此方法来定义具体的 Agent 行为
     *
     * @param context 上下文（包含所有前置 Agent 的输出）
     * @return 执行结果
     */
    public abstract AgentStepResult execute(ContentContext context);

    /**
     * 调用 LLM 进行对话
     *
     * @param system 系统提示词
     * @param user   用户输入
     * @return LLM 响应内容
     */
    protected String chat(String system, String user) {
        log.debug("[{}] Chat - System: {}, User: {}", name, system, user);
        return chatClient.prompt()
                .system(system)
                .user(user)
                .call()
                .content();
    }

    /**
     * 调用 LLM 并期望返回 JSON
     *
     * @param system     系统提示词
     * @param user       用户输入
     * @param outputType 期望的返回类型
     * @return LLM 响应的 JSON 对象
     */
    protected <T> T chatJson(String system, String user, Class<T> outputType) {
        log.debug("[{}] ChatJson - System: {}, User: {}, OutputType: {}", name, system, user, outputType);
        return chatClient.prompt()
                .system(system)
                .user(user)
                .call()
                .entity(outputType);
    }

    /**
     * 创建成功的执行结果
     */
    protected AgentStepResult result(Object data) {
        return AgentStepResult.success(data);
    }

    /**
     * 创建失败的执行结果
     */
    protected AgentStepResult fail(String error) {
        return AgentStepResult.fail(error);
    }

    /**
     * 创建带统计信息的执行结果
     */
    protected AgentStepResult result(Object data, int llmCalls, int tokenUsage) {
        return AgentStepResult.success(data, llmCalls, tokenUsage);
    }
}
