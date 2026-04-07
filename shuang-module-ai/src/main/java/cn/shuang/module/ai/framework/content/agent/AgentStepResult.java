package cn.shuang.module.ai.framework.content.agent;

import lombok.Builder;
import lombok.Data;

/**
 * Agent 执行结果
 * <p>
 * 所有内容生成 Agent 的统一返回结构
 */
@Data
@Builder
public class AgentStepResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 结果数据
     */
    private Object data;

    /**
     * 错误信息
     */
    private String error;

    /**
     * LLM 调用次数
     */
    private int llmCalls;

    /**
     * Token 使用量
     */
    private int tokenUsage;

    public static AgentStepResult success(Object data) {
        return AgentStepResult.builder()
                .success(true)
                .data(data)
                .build();
    }

    public static AgentStepResult success(Object data, int llmCalls, int tokenUsage) {
        return AgentStepResult.builder()
                .success(true)
                .data(data)
                .llmCalls(llmCalls)
                .tokenUsage(tokenUsage)
                .build();
    }

    public static AgentStepResult fail(String error) {
        return AgentStepResult.builder()
                .success(false)
                .error(error)
                .build();
    }
}
