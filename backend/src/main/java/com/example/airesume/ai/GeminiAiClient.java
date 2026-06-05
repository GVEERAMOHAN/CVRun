package com.example.airesume.ai;

import com.example.airesume.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class GeminiAiClient implements AiJobDescriptionAnalysisClient, AiResumeGenerationClient {
    private static final Logger log = LoggerFactory.getLogger(GeminiAiClient.class);

    @Value("${app.ai.gemini.api-key:}")
    private String apiKey;

    @Value("${app.ai.gemini.model:gemini-2.5-flash}")
    private String modelName;

    private final StubAiClient stubFallback;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiAiClient(StubAiClient stubFallback) {
        this.stubFallback = stubFallback;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public JobDescription analyze(String jdText) {
        if (apiKey == null || apiKey.isBlank()) {
            log.info("Gemini API Key not set. Falling back to local Stub client for Job Description analysis.");
            return stubFallback.analyze(jdText);
        }

        try {
            String prompt = "You are a professional ATS resume analyzer. Analyze this Job Description:\n\n" + jdText + "\n\n" +
                    "Respond with a single JSON object (no markdown wrapping) containing: \n" +
                    "1. 'keywords': list of top 8 keywords/technologies\n" +
                    "2. 'requiredSkills': list of top 8 technical skills\n" +
                    "3. 'responsibilities': list of 4 key duties\n" +
                    "4. 'roleEmphasis': short summary phrase of the position emphasis.\n" +
                    "Example output: {\"keywords\":[\"Java\"],\"requiredSkills\":[\"Java\"],\"responsibilities\":[\"Develop APIs\"],\"roleEmphasis\":\"Backend Dev\"}";

            String rawJson = callGemini(prompt);
            JsonNode root = objectMapper.readTree(rawJson);
            
            List<String> keywords = new ArrayList<>();
            if (root.has("keywords")) {
                root.get("keywords").forEach(k -> keywords.add(k.asText()));
            }
            List<String> requiredSkills = new ArrayList<>();
            if (root.has("requiredSkills")) {
                root.get("requiredSkills").forEach(s -> requiredSkills.add(s.asText()));
            }
            List<String> responsibilities = new ArrayList<>();
            if (root.has("responsibilities")) {
                root.get("responsibilities").forEach(r -> responsibilities.add(r.asText()));
            }
            String roleEmphasis = root.has("roleEmphasis") ? root.get("roleEmphasis").asText() : "Full Stack Software Engineer";

            return new JobDescription(jdText, keywords, requiredSkills, responsibilities, roleEmphasis);
        } catch (Exception e) {
            log.error("Failed to call Gemini API for JD analysis. Falling back to Stub client.", e);
            return stubFallback.analyze(jdText);
        }
    }

    @Override
    public String generate(ApprovedContent approved, JobDescription jd) {
        if (apiKey == null || apiKey.isBlank()) {
            log.info("Gemini API Key not set. Falling back to local Stub client for Resume Generation.");
            return stubFallback.generate(approved, jd);
        }

        try {
            // Convert approved content to JSON format for the prompt
            String approvedJson = objectMapper.writeValueAsString(approved);
            String prompt = "You are an expert ATS resume writer. Rewrite and structure a tailored plain text resume.\n" +
                    "Use ONLY these approved user items. Never invent projects, skills, experience, certifications, or achievements. Stay 100% factually grounded in user-provided info:\n" +
                    approvedJson + "\n\n" +
                    "Optimize the phrasing of experience bullets to align with these Job Description keywords: " + jd.getKeywords() + "\n\n" +
                    "Format the output as a clean single-column plain text resume with clear CAPITALIZED headers (e.g. SUMMARY, SKILLS, EXPERIENCE, PROJECTS, EDUCATION) and use standard asterisks (*) for bullets. Do not wrap in HTML/markdown codes.";

            return callGemini(prompt);
        } catch (Exception e) {
            log.error("Failed to call Gemini API for Resume generation. Falling back to Stub client.", e);
            return stubFallback.generate(approved, jd);
        }
    }

    private String callGemini(String prompt) throws Exception {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build structure: {"contents": [{"parts": [{"text": prompt}]}]}
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);
        
        Map<String, Object> contentsPart = new HashMap<>();
        contentsPart.put("parts", Collections.singletonList(textPart));
        requestBody.put("contents", Collections.singletonList(contentsPart));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        String response = restTemplate.postForObject(url, entity, String.class);

        // Parse response structure to get text: candidates[0].content.parts[0].text
        JsonNode root = objectMapper.readTree(response);
        JsonNode candidate = root.path("candidates").get(0);
        JsonNode parts = candidate.path("content").path("parts").get(0);
        String text = parts.path("text").asText();
        
        // Handle code block wrappers if any are returned
        if (text.startsWith("```json")) {
            text = text.substring(7);
            if (text.endsWith("```")) {
                text = text.substring(0, text.length() - 3);
            }
        } else if (text.startsWith("```")) {
            text = text.substring(3);
            if (text.endsWith("```")) {
                text = text.substring(0, text.length() - 3);
            }
        }
        return text.trim();
    }
}
