package com.example.resumeanalyzer.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiSummaryService {

    private final OpenAiService openAiService;

    public AiSummaryService(@Value("${openai.api.key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
    }

    public String generateSummary(String resumeText) {
        CompletionRequest request = CompletionRequest.builder()
                .prompt("Summarize this resume in 3-5 sentences:\n" + resumeText)
                .model("gpt-3.5-turbo-instruct")
                .temperature(0.7)
                .maxTokens(150)
                .build();

        return openAiService.createCompletion(request)
                .getChoices()
                .get(0)
                .getText()
                .trim();
    }
}
