package org.example.agent;

public class AgentAction {

    private String action;   // "tool" | "final"
    private String toolName;
    private String input;

    public String getAction() {
        return action;
    }

    public String getToolName() {
        return toolName;
    }

    public String getInput() {
        return input;
    }
}
