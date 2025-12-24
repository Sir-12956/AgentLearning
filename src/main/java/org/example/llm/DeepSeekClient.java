package org.example.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.util.*;

public class DeepSeekClient {

    private static final String API_URL =
            "https://api.deepseek.com/v1/chat/completions";

    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    public DeepSeekClient(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("DEEPSEEK_API_KEY is empty");
        }
        this.apiKey = apiKey;
    }

    public String chat(List<ChatMessage> messages) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", "deepseek-chat");
            body.put("temperature", 0);
            body.put("messages", messages);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(
                            mapper.writeValueAsString(body),
                            MediaType.parse("application/json")
                    ))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException(response.body().string());
                }

                Map<String, Object> json =
                        mapper.readValue(response.body().string(), Map.class);

                List<Map<String, Object>> choices =
                        (List<Map<String, Object>>) json.get("choices");

                Map<String, Object> message =
                        (Map<String, Object>) choices.get(0).get("message");

                return (String) message.get("content");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
