package org.example.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.util.*;

public class DeepSeekDemo {

    private static final String API_KEY = System.getenv("DEEPSEEK_API_KEY");
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    public static void main(String[] args) throws Exception {
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IllegalArgumentException("DEEPSEEK_API_KEY is not set");
        }

        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(message("system", "You are a helpful assistant."));
        messages.add(message("user", "请用一句话解释什么是 Java 的 JVM"));

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(
                        mapper.writeValueAsString(requestBody),
                        MediaType.parse("application/json")
                ))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("HTTP error: " + response.code() + " " + response.body().string());
            }

            String responseBody = response.body().string();
            System.out.println("Raw response:");
            System.out.println(responseBody);
        }
    }

    private static Map<String, String> message(String role, String content) {
        Map<String, String> msg = new HashMap<>();
        msg.put("role", role);
        msg.put("content", content);
        return msg;
    }
}
