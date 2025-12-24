package org.example.agent;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.llm.ChatMessage;
import org.example.llm.DeepSeekClient;
import org.example.tool.Tool;

import java.util.*;

public class PlanningAgent {

    private final DeepSeekClient llm;
    private final Map<String, Tool> tools = new HashMap<>();
//    private final List<ChatMessage> memory = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();

    // 最大循环步数，防止无限循环
    private final int maxSteps;
    private static final int MAX_PARSE_FAILURES = 2;
    private static final int MAX_TOOL_FAILURES = 2;

    public PlanningAgent(DeepSeekClient llm, List<Tool> toolList, int maxSteps) {
        this.llm = llm;
        this.maxSteps = maxSteps;
        for (Tool tool : toolList) {
            tools.put(tool.name(), tool);
        }

//        memory.add(new ChatMessage(
//                "system",
//                """
//                You are an Agent capable of planning multiple steps.
//                You MUST NOT solve tasks yourself.
//                For user requests with multiple tasks, decompose into multiple actions.
//                Each action must be JSON in the format:
//                {
//                  "action": "tool" or "final",
//                  "toolName": "<tool name if action is tool>",
//                  "input": "<input for tool or final answer>"
//                }
//                Available tools:
//                - current_time: get current system time
//                - echo: repeat input
//                - random_number: generate a random number
//                Always return each action one by one. Do not combine multiple tools in one action.
//                When you receive an observation, it will be a JSON object with:
//                - type = "observation"
//                - tool
//                - success
//                - output or error
//
//                You MUST use the observation output for subsequent planning.
//                Do NOT fabricate tool results.
//                """
//        ));
    }

//    public String run(String userInput) {
//        memory.add(new ChatMessage("user", userInput));
//
//        int step = 0;
//
//        while (step < maxSteps) {
//            step++;
//            String raw;
//            try {
//                raw = llm.chat(memory);
//            } catch (Exception e) {
//                memory.add(new ChatMessage("assistant",
//                        "Error: LLM request failed: " + e.getMessage()));
//                continue; // 重试
//            }
//
//            AgentAction action;
//
//            try {
//                action = mapper.readValue(raw, AgentAction.class);
//            } catch (JsonMappingException e) {
//                memory.add(new ChatMessage("assistant",
//                        "Error parsing JSON action, raw output:\n" + raw));
//                continue; // 让 LLM 重新规划
//            } catch (Exception e) {
//                memory.add(new ChatMessage("assistant",
//                        "Unexpected error parsing JSON: " + e.getMessage()));
//                continue;
//            }
//
//
//            if ("final".equals(action.getAction())) {
//                memory.add(new ChatMessage("assistant", raw));
//                return action.getInput();
//            }
//
//            // 校验工具
//            Tool tool = tools.get(action.getToolName());
//            if (tool == null) {
//                memory.add(new ChatMessage("assistant",
//                        "Error: Unknown tool `" + action.getToolName() + "`"));
//                continue; // 让 LLM 重新规划
//            }
//
//            // 参数校验（简单示例：非 null）
//            if (action.getInput() == null) {
//                memory.add(new ChatMessage("assistant",
//                        "Error: Tool `" + action.getToolName() + "` received null input"));
//                continue; // 让 LLM 重新规划
//            }
//
//            // 执行工具并注入 Observation
//            Observation observation;
//            try {
//                String toolResult = tool.execute(action.getInput());
//                observation = Observation.success(action.getToolName(), toolResult);
//            } catch (Exception e) {
//                observation = Observation.failure(action.getToolName(), e.getMessage());
//            }
//
//            try {
//                String observationJson = mapper.writeValueAsString(observation);
//                memory.add(new ChatMessage("assistant", observationJson));
//            } catch (Exception e) {
//                memory.add(new ChatMessage(
//                        "assistant",
//                        "{\"type\":\"observation\",\"tool\":\"" + action.getToolName()
//                                + "\",\"success\":false,\"error\":\"serialization_failed\"}"
//                ));
//            }
//        }
//
//        // 超过最大循环步数
//        return "Error: Exceeded maximum allowed steps (" + maxSteps + ").";
//    }


    private String systemPrompt() {
        return """
                You are an Agent capable of planning multiple steps.
                You MUST NOT solve tasks yourself.
                For user requests with multiple tasks, decompose into multiple actions.
                Each action must be JSON in the format:
                {
                  "action": "tool" or "final",
                  "toolName": "<tool name if action is tool>",
                  "input": "<input for tool or final answer>"
                }
                Available tools:
                - current_time: get current system time
                - echo: repeat input
                - random_number: generate a random number
                Always return each action one by one. Do not combine multiple tools in one action.
                When you receive an observation, it will be a JSON object with:
                - type = "observation"
                - tool
                - success
                - output or error
                
                You MUST use the observation output for subsequent planning.
                Do NOT fabricate tool results.
                """;
    }

    public String run(String userInput) {
        AgentContext ctx = new AgentContext();
        ctx.dialogue.add(new ChatMessage("system", systemPrompt()));
        ctx.dialogue.add(new ChatMessage("user", userInput));

        while (true) {
            switch (ctx.state) {
                case INIT -> ctx.state = AgentState.PLAN;

                case PLAN -> handlePlan(ctx);

                case EXECUTE -> handleExecute(ctx);

                case OBSERVE -> handleObserve(ctx);

                case DECIDE -> handleDecide(ctx);

                case FINAL -> {
                    return ctx.finalResult;
                }

                case FAIL -> {
                    return "Agent failed: " + ctx.finalResult;
                }
            }
        }
    }

    private void handlePlan(AgentContext ctx) {
        if (++ctx.step > maxSteps) {
            ctx.finalResult = "Exceeded max steps";
            ctx.state = AgentState.FAIL;
            return;
        }

        try {
            String raw = llm.chat(ctx.dialogue);
            ctx.currentAction = mapper.readValue(raw, AgentAction.class);
            ctx.dialogue.add(new ChatMessage("assistant", raw));
            ctx.state = AgentState.EXECUTE;
        } catch (Exception e) {
            ctx.parseFailures++;
            ctx.dialogue.add(new ChatMessage("assistant",
                    "Error parsing action: " + e.getMessage()));
            ctx.state = ctx.parseFailures >= MAX_PARSE_FAILURES ? AgentState.FAIL : AgentState.PLAN;
        }
    }

    private void handleExecute(AgentContext ctx) {

        if (!"tool".equals(ctx.currentAction.getAction())) {
            ctx.finalResult = ctx.currentAction.getInput();
            ctx.state = AgentState.FINAL;
            return;
        }

        Tool tool = tools.get(ctx.currentAction.getToolName());
        if (tool == null) {
            ctx.finalResult = "Unknown tool: " + ctx.currentAction.getToolName();
            ctx.state = AgentState.FAIL;
            return;
        }

        try {
            String result = tool.execute(ctx.currentAction.getInput());
            ctx.lastObservation = Observation.success(tool.name(), result);
            ctx.state = AgentState.OBSERVE;
        } catch (Exception e) {
            ctx.lastObservation = Observation.failure(tool.name(), e.getMessage());
            ctx.toolFailures++;
            ctx.state = ctx.toolFailures >= MAX_TOOL_FAILURES ? AgentState.FAIL : AgentState.OBSERVE;
        }
    }

    private void handleObserve(AgentContext ctx) {
        try {
            String json = mapper.writeValueAsString(ctx.lastObservation);
            ctx.dialogue.add(new ChatMessage("assistant", json));
            ctx.observations.add(ctx.lastObservation);
            ctx.state = AgentState.DECIDE;
        } catch (Exception e) {
            ctx.finalResult = "Observation serialization failed";
            ctx.state = AgentState.FAIL;
        }
    }

    private void handleDecide(AgentContext ctx) {
        if ("final".equals(ctx.currentAction.getAction())) {
            ctx.finalResult = ctx.currentAction.getInput();
            ctx.state = AgentState.FINAL;
        } else {
            ctx.state = AgentState.PLAN;
        }
    }



}
