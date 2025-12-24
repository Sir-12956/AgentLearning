package org.example.agent;

import org.example.llm.ChatMessage;
import org.example.llm.DeepSeekClient;

import java.util.ArrayList;
import java.util.List;

public class SimpleAgent {

    private final DeepSeekClient llm;
    private final List<ChatMessage> memory = new ArrayList<>();

    public SimpleAgent(DeepSeekClient llm) {
        this.llm = llm;
        memory.add(new ChatMessage(
                "system",
                "You are a Java backend assistant that reasons step by step."
        ));
    }

    public String run(String userInput) {
        memory.add(new ChatMessage("user", userInput));

        String reply = llm.chat(memory);

        memory.add(new ChatMessage("assistant", reply));
        return reply;
    }
}
