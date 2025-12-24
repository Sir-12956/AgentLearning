package org.example.agent;

public enum AgentState {
    INIT,       // 初始化
    PLAN,       // 调用 LLM 生成 Action
    EXECUTE,    // 执行 Tool
    OBSERVE,    // 生成 Observation
    DECIDE,     // 判断是否继续
    FINAL,      // 正常结束
    FAIL        // 异常终止
}