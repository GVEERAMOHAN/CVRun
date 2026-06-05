package com.example.airesume.service;

import com.example.airesume.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SuggestionRankingServiceTest {

    private SuggestionRankingService rankingService;

    @BeforeEach
    public void setup() {
        rankingService = new SuggestionRankingService();
    }

    @Test
    public void testGenerateSuggestionsRanking() {
        ResumeData merged = new ResumeData();
        merged.setSkills(Arrays.asList("Java", "Spring Boot", "C++"));
        
        Project proj1 = new Project("pr1", "Spring Microservices", "Built Spring Boot backend", 
                Arrays.asList("Implemented REST APIs"), Arrays.asList("Java", "Spring Boot"), Collections.emptyList(), "resume");
        Project proj2 = new Project("pr2", "Legacy System", "Maintained ancient code", 
                Arrays.asList("Did some bug fixing"), Arrays.asList("C++"), Collections.emptyList(), "resume");
        merged.setProjects(Arrays.asList(proj1, proj2));

        JobDescription jd = new JobDescription(
                "Backend developer role requiring Java and Spring Boot experience. Build microservices.",
                Arrays.asList("Java", "Spring Boot", "microservices"),
                Arrays.asList("Java", "Spring Boot"),
                Arrays.asList("Build microservices"),
                "Backend Engineering focus"
        );

        List<Suggestion> suggestions = rankingService.generateSuggestions(merged, jd);
        
        // Assertions
        assertFalse(suggestions.isEmpty());
        
        // Check that Spring project scores higher than Legacy project
        Suggestion springProjSug = suggestions.stream()
                .filter(s -> "projects".equals(s.getSection()) && "pr1".equals(s.getOriginalItemId()))
                .findFirst().orElseThrow();

        Suggestion legacyProjSug = suggestions.stream()
                .filter(s -> "projects".equals(s.getSection()) && "pr2".equals(s.getOriginalItemId()))
                .findFirst().orElseThrow();

        assertTrue(springProjSug.getScore() > legacyProjSug.getScore(), 
                "Spring Boot project score (" + springProjSug.getScore() + ") should be higher than C++ legacy project (" + legacyProjSug.getScore() + ")");
    }
}
