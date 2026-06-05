package com.example.airesume.service;

import com.example.airesume.model.Project;
import com.example.airesume.model.ResumeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileMergeServiceTest {

    private ProfileMergeService mergeService;

    @BeforeEach
    public void setup() {
        mergeService = new ProfileMergeService();
    }

    @Test
    public void testMergePersonalDetails() {
        ResumeData parsed = new ResumeData();
        parsed.setName("John Doe");
        parsed.setEmail("john@example.com");

        ResumeData manual = new ResumeData();
        manual.setName("John Q. Doe"); // override
        manual.setEmail(""); // empty, should use parsed

        ResumeData merged = mergeService.merge(parsed, manual);
        assertEquals("John Q. Doe", merged.getName());
        assertEquals("john@example.com", merged.getEmail());
    }

    @Test
    public void testMergeSkillsDeduplication() {
        ResumeData parsed = new ResumeData();
        parsed.setSkills(Arrays.asList("Java", "Spring", "react"));

        ResumeData manual = new ResumeData();
        manual.setSkills(Arrays.asList("React", "Docker", "JAVA"));

        ResumeData merged = mergeService.merge(parsed, manual);
        
        // React, Docker, JAVA (from manual first) + Spring (from parsed)
        // Deduplicated case-insensitively
        assertEquals(4, merged.getSkills().size());
        assertTrue(merged.getSkills().contains("React"));
        assertTrue(merged.getSkills().contains("Docker"));
        assertTrue(merged.getSkills().contains("JAVA"));
        assertTrue(merged.getSkills().contains("Spring"));
    }

    @Test
    public void testMergeProjects() {
        ResumeData parsed = new ResumeData();
        Project parsedProject = new Project("p1", "E-Commerce", "Description 1", Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), "resume");
        parsed.setProjects(Collections.singletonList(parsedProject));

        ResumeData manual = new ResumeData();
        Project manualProject = new Project(null, "E-Commerce", "Manual overrides description", Arrays.asList("Bullet 1"), Arrays.asList("Java"), Collections.emptyList(), "manual");
        manual.setProjects(Collections.singletonList(manualProject));

        ResumeData merged = mergeService.merge(parsed, manual);

        assertEquals(1, merged.getProjects().size());
        Project mergedProject = merged.getProjects().get(0);
        assertEquals("Manual overrides description", mergedProject.getDescription());
        assertEquals("both", mergedProject.getSource());
        assertEquals(1, mergedProject.getBullets().size());
        assertEquals("Bullet 1", mergedProject.getBullets().get(0));
    }
}
