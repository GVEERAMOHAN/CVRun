package com.example.airesume.service;

import com.example.airesume.ai.AiJobDescriptionAnalysisClient;
import com.example.airesume.ai.StubAiClient;
import com.example.airesume.ai.GeminiAiClient;
import com.example.airesume.model.JobDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JobDescriptionAnalysisService {
    private static final Logger log = LoggerFactory.getLogger(JobDescriptionAnalysisService.class);

    private final StubAiClient stubClient;
    private final GeminiAiClient geminiClient;
    
    @Value("${app.ai.provider:stub}")
    private String aiProvider;

    public JobDescriptionAnalysisService(StubAiClient stubClient, GeminiAiClient geminiClient) {
        this.stubClient = stubClient;
        this.geminiClient = geminiClient;
    }

    public JobDescription analyze(String jdText) {
        if (jdText == null || jdText.isBlank()) {
            throw new IllegalArgumentException("Job description text cannot be empty");
        }

        log.info("Analyzing job description using provider: {}", aiProvider);
        
        AiJobDescriptionAnalysisClient activeClient = getActiveClient();
        return activeClient.analyze(jdText);
    }

    private AiJobDescriptionAnalysisClient getActiveClient() {
        if ("gemini".equalsIgnoreCase(aiProvider)) {
            return geminiClient;
        }
        return stubClient;
    }
}
