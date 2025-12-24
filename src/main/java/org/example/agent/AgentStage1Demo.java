package org.example.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class AgentStage1Demo {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        System.out.println("start");
        System.out.println("http.proxyHost=" + System.getProperty("http.proxyHost"));
        System.out.println("https.proxyHost=" + System.getProperty("https.proxyHost"));


        // 1. 初始化模型
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-mini")
                .temperature(0.0) // 强烈建议：初期一定设为 0
                .build();

        System.out.println("after calling model");

        // 2. 模拟用户输入
        String userInput = "帮我查一下订单 12345 的状态";

        // 3. 构造强约束 Prompt（核心）
        String prompt = buildPrompt(userInput);

        // 4. 调用模型
        String llmResult = model.generate(prompt);

        System.out.println("LLM 原始输出：");
        System.out.println(llmResult);

        // 5. 解析 JSON
        JsonNode root = MAPPER.readTree(llmResult);
        String action = root.get("action").asText();

        // 6. Java 路由执行（Executor）
        switch (action) {
            case "queryOrder" -> {
                String orderId = root.get("orderId").asText();
                queryOrder(orderId);
            }
            case "cancelOrder" -> {
                String orderId = root.get("orderId").asText();
                cancelOrder(orderId);
            }
            case "chat" -> {
                String message = root.get("message").asText();
                chat(message);
            }
            default -> throw new IllegalStateException("未知 action: " + action);
        }
    }

    private static String buildPrompt(String userInput) {
        return """
                你是一个后端路由助手，负责将用户输入转换为系统可执行的指令。

                规则（必须严格遵守）：
                1. 只能输出 JSON，不允许输出任何解释性文字
                2. action 只能是以下三种之一：
                   - queryOrder
                   - cancelOrder
                   - chat
                3. 如果 action 是 queryOrder 或 cancelOrder，必须提供 orderId
                4. 如果无法判断具体操作，使用 chat

                JSON 格式示例：
                {
                  "action": "queryOrder",
                  "orderId": "12345"
                }

                用户输入：
                """ + userInput;
    }

    // ====== 以下是模拟的业务方法 ======

    private static void queryOrder(String orderId) {
        System.out.println("【执行查询订单】orderId = " + orderId);
        // TODO 实际业务中这里查 DB / 调用 RPC
    }

    private static void cancelOrder(String orderId) {
        System.out.println("【执行取消订单】orderId = " + orderId);
        // TODO 实际业务逻辑
    }

    private static void chat(String message) {
        System.out.println("【普通聊天】" + message);
    }
}
