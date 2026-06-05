package com.example.airesume.service;

import com.example.airesume.ai.AiResumeGenerationClient;
import com.example.airesume.ai.StubAiClient;
import com.example.airesume.ai.GeminiAiClient;
import com.example.airesume.model.ApprovedContent;
import com.example.airesume.model.GeneratedResume;
import com.example.airesume.model.JobDescription;
import com.example.airesume.model.ResumeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ResumeGenerationService {
    private static final Logger log = LoggerFactory.getLogger(ResumeGenerationService.class);

    private final StubAiClient stubClient;
    private final GeminiAiClient geminiClient;

    @Value("${app.ai.provider:stub}")
    private String aiProvider;

    public ResumeGenerationService(StubAiClient stubClient, GeminiAiClient geminiClient) {
        this.stubClient = stubClient;
        this.geminiClient = geminiClient;
    }

    public GeneratedResume generate(ResumeData mergedProfile, ApprovedContent approved, JobDescription jd) {
        if (approved == null || (approved.getSkills().isEmpty() 
                && approved.getProjects().isEmpty() 
                && approved.getExperience().isEmpty())) {
            throw new IllegalArgumentException("Cannot generate resume without approved content");
        }

        log.info("Generating resume using AI provider: {}", aiProvider);
        AiResumeGenerationClient activeClient = getActiveClient();
        String rawText = activeClient.generate(approved, jd);

        // Post-process to inject actual user personal info (guarding against stub placeholders)
        String tailoredText = injectPersonalDetails(rawText, mergedProfile);

        return new GeneratedResume(tailoredText);
    }

    private String injectPersonalDetails(String text, ResumeData profile) {
        // Find the placeholder header and replace it with user details
        StringBuilder contact = new StringBuilder();
        if (profile.getName() != null && !profile.getName().isBlank()) {
            contact.append(profile.getName().toUpperCase()).append("\n");
        } else {
            contact.append("JOHN DOE\n");
        }
        
        boolean hasContactInfo = false;
        if (profile.getPhone() != null && !profile.getPhone().isBlank()) {
            contact.append(profile.getPhone());
            hasContactInfo = true;
        }
        if (profile.getEmail() != null && !profile.getEmail().isBlank()) {
            if (hasContactInfo) contact.append(" | ");
            contact.append(profile.getEmail());
            hasContactInfo = true;
        }
        if (profile.getLocation() != null && !profile.getLocation().isBlank()) {
            if (hasContactInfo) contact.append(" | ");
            contact.append(profile.getLocation());
            hasContactInfo = true;
        }
        
        if (hasContactInfo) {
            contact.append("\n");
        }
        
        // Add links
        if (profile.getLinks() != null && !profile.getLinks().isEmpty()) {
            String linksStr = profile.getLinks().stream()
                    .map(l -> l.getLabel() + ": " + l.getUrl())
                    .collect(Collectors.joining(" | "));
            contact.append(linksStr).append("\n");
        }

        contact.append("--------------------------------------------------------------------------------\n");

        // Simple replacement of the first lines up to the first divider
        int idx = text.indexOf("--------------------------------------------------------------------------------\n");
        if (idx != -1) {
            return contact.toString() + text.substring(idx + "--------------------------------------------------------------------------------\n".length());
        }
        return contact.toString() + "\n" + text;
    }

    private AiResumeGenerationClient getActiveClient() {
        if ("gemini".equalsIgnoreCase(aiProvider)) {
            return geminiClient;
        }
        return stubClient;
    }
}
