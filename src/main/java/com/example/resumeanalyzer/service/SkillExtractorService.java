package com.example.resumeanalyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SkillExtractorService {

    // ${openai.api.key}
    @Value("Replace later")
    private String openaiApiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public List<String> extractSkills(String resumeText) {
        String prompt = "Extract a list of technical and soft skills from this resume:\n\n" + resumeText + "\n\nReturn them as a plain JSON array of strings.";

        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(message),
                "temperature", 0.2
        );

        try {
            Request request = new Request.Builder()
                    .url(OPENAI_API_URL)
                    .header("Authorization", "Bearer " + openaiApiKey)
                    .post(RequestBody.create(
                            mapper.writeValueAsString(requestBody),
                            MediaType.get("application/json")
                    ))
                    .build();

            Response response = client.newCall(request).execute();
            String body = response.body().string();

            JsonNode root = mapper.readTree(body);
            String content = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            // Parse the result as a JSON array
            return mapper.readValue(content, List.class);

        } catch (IOException e) {
            e.printStackTrace();
            return List.of(); // Fallback
        }
    }
}
