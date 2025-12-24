package org.example.llm;

public class LLMResponse {
    String content;      // raw model output
    long latencyMs;
    int promptTokens;
    int completionTokens;
}

