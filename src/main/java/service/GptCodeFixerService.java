package service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

import org.json.JSONObject;

import java.io.IOException;

public class GptCodeFixerService {
    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY"); // Set this in your environment
    private static final String OPENAI_ENDPOINT = "https://api.openai.com/v1/chat/completions";

    private final OkHttpClient client;

    public GptCodeFixerService() {
        client = new OkHttpClient();
    }

    public String getFixedCode(String inputCode) {
        if (OPENAI_API_KEY == null || OPENAI_API_KEY.isEmpty()) {
            return "❌ ERROR: API key not found. Set OPENAI_API_KEY as an environment variable.";
        }

        String prompt = "Fix any bugs and optimize this Java code. Return only the corrected Java code:\n\n"
                + inputCode;

        MediaType mediaType = MediaType.parse("application/json");
        String json = "{\n" +
                "  \"model\": \"gpt-3.5-turbo\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"You are a Java code fixer. Fix bugs and optimize code.\"},\n"
                +
                "    {\"role\": \"user\", \"content\": " + JSONObject.quote(prompt) + "}\n" +
                "  ],\n" +
                "  \"temperature\": 0.2\n" +
                "}";

        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(OPENAI_ENDPOINT)
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try (Response response = client.newCall(request).execute()) {
                if (response.code() == 429) {
                    System.out.println("⚠️ Rate limit hit (HTTP 429). Attempt " + attempt + " of " + maxRetries);
                    if (attempt < maxRetries) {
                        Thread.sleep(5000); // Wait before retrying
                        continue;
                    } else {
                        return "❌ Error: Too many requests (HTTP 429). Please try again later.";
                    }
                }

                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    return "❌ API Error: " + response.code() + " - " + response.message() + "\n" + errorBody;
                }

                String responseBody = response.body().string();
                return extractMessageFromResponse(responseBody);

            } catch (IOException | InterruptedException e) {
                return "❌ Request failed: " + e.getMessage();
            }
        }

        return "❌ Unexpected error.";
    }

    private String extractMessageFromResponse(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return obj.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();
        } catch (Exception e) {
            return "⚠️ Could not parse GPT response: " + e.getMessage();
        }
    }

}
