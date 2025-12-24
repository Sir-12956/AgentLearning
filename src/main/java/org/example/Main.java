package org.example;

import org.example.agent.PlanningAgent;
import org.example.llm.DeepSeekClient;
import org.example.tool.EchoTool;
import org.example.tool.RandomNumberTool;
import org.example.tool.TimeTool;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String apiKey = System.getenv("DEEPSEEK_API_KEY");

        DeepSeekClient client = new DeepSeekClient(apiKey);

        // Phase 2 Agent, one request, one response
//        SimpleAgent agent = new SimpleAgent(client);

//        System.out.println(agent.run("什么是 JVM？"));
//        System.out.println("----");
//        System.out.println(agent.run("它和 JRE、JDK 有什么关系？"));

        // Phase 3 Agent, enables the agent to decide on Tool calls, with execution handled by Java
//        PlanningAgent agent = new PlanningAgent(
//                client,
//                List.of(new TimeTool())
//        );
//
//        System.out.println(
//                agent.run("现在几点了？")
//        );

        // Phase 4 Agent, Agent upgrades from single-step tool invocation to multi-tool planning and multi-step decision-making
        PlanningAgent agent = new PlanningAgent(
                client,
                List.of(new TimeTool(), new EchoTool(), new RandomNumberTool()),
                10
        );

        String userInput = "请重复输出“你好”，然后诉我现在时间，并告生成一个随机数，再随机给出一个包含幸福的歌词";
        System.out.println(agent.run(userInput));
    }
}
