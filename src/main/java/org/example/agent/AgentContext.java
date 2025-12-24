package org.example.agent;

import org.example.llm.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class AgentContext {

    public AgentState state = AgentState.INIT;

    public final List<ChatMessage> dialogue = new ArrayList<>();
    public final List<Observation> observations = new ArrayList<>();

    public AgentAction currentAction;
    public Observation lastObservation;

    public int step = 0;
    public int parseFailures = 0;
    public int toolFailures = 0;

    public String finalResult;
}
