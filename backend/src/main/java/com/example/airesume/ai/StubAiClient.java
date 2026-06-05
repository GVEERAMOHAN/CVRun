package com.example.airesume.ai;

import com.example.airesume.model.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StubAiClient implements AiJobDescriptionAnalysisClient, AiResumeGenerationClient {

    private static final String[] DICTIONARY = {
        "Java", "Spring Boot", "React", "TypeScript", "JavaScript", "Docker", "Kubernetes", "AWS", 
        "PostgreSQL", "MySQL", "Git", "REST APIs", "Microservices", "Kafka", "Redis", "JUnit",
        "CI/CD", "Agile", "Scrum", "SQL", "NoSQL", "Python", "Cloud", "Security", "Backend", "Frontend"
    };

    @Override
    public JobDescription analyze(String jdText) {
        if (jdText == null || jdText.isBlank()) {
            throw new IllegalArgumentException("Job description text cannot be empty");
        }

        List<String> keywords = new ArrayList<>();
        List<String> requiredSkills = new ArrayList<>();
        List<String> responsibilities = new ArrayList<>();
        
        String lowerJd = jdText.toLowerCase();
        for (String word : DICTIONARY) {
            if (lowerJd.contains(word.toLowerCase())) {
                keywords.add(word);
                requiredSkills.add(word);
            }
        }

        // Parse responsibilities - look for lines starting with bullet symbols or verbs
        String[] lines = jdText.split("\\r?\\n");
        for (String line : lines) {
            String clean = line.trim();
            if (clean.startsWith("-") || clean.startsWith("*") || clean.startsWith("•")) {
                responsibilities.add(clean.replaceFirst("^[-*•]\\s*", ""));
            } else if (clean.length() > 20 && (clean.toLowerCase().startsWith("responsibilit") || clean.toLowerCase().startsWith("require"))) {
                // Skip header line itself
            } else if (clean.length() > 30 && containsActionVerb(clean)) {
                responsibilities.add(clean);
            }
        }

        // Limit lists to avoid huge payloads
        if (responsibilities.isEmpty()) {
            responsibilities.add("Develop and maintain scalable web applications");
            responsibilities.add("Collaborate with cross-functional teams in agile environment");
        }
        if (responsibilities.size() > 5) {
            responsibilities = responsibilities.subList(0, 5);
        }

        String roleEmphasis = "Full-Stack Software Engineering";
        if (lowerJd.contains("frontend") || lowerJd.contains("ui")) {
            roleEmphasis = "Frontend Development focus";
        } else if (lowerJd.contains("backend") || lowerJd.contains("spring")) {
            roleEmphasis = "Backend Engineering focus";
        }

        return new JobDescription(jdText, keywords, requiredSkills, responsibilities, roleEmphasis);
    }

    private boolean containsActionVerb(String line) {
        String lower = line.toLowerCase();
        return lower.contains("build") || lower.contains("develop") || lower.contains("design") 
                || lower.contains("write") || lower.contains("collaborate") || lower.contains("manage")
                || lower.contains("implement") || lower.contains("maintain");
    }

    @Override
    public String generate(ApprovedContent approved, JobDescription jd) {
        StringBuilder sb = new StringBuilder();
        
        // Let's assume we can obtain the name from contact info.
        // We'll format a premium single-column layout
        sb.append("JOHN DOE\n"); // Fallback name, usually overridden by service
        sb.append("--------------------------------------------------------------------------------\n");
        
        // SUMMARY
        sb.append("SUMMARY\n");
        sb.append("-------\n");
        sb.append("Results-driven developer specialized in ").append(jd.getRoleEmphasis()).append(".\n");
        sb.append("Proficient in writing clean, modular code and collaborating in agile teams.\n\n");

        // SKILLS
        sb.append("SKILLS\n");
        sb.append("------\n");
        if (!approved.getSkills().isEmpty()) {
            sb.append(String.join(", ", approved.getSkills())).append("\n\n");
        } else {
            sb.append("None provided\n\n");
        }

        // EXPERIENCE
        sb.append("EXPERIENCE\n");
        sb.append("----------\n");
        for (Experience exp : approved.getExperience()) {
            sb.append(exp.getRole().toUpperCase()).append(" | ").append(exp.getCompany());
            if (exp.getLocation() != null && !exp.getLocation().isEmpty()) {
                sb.append(" | ").append(exp.getLocation());
            }
            sb.append("\n");
            sb.append(exp.getStartDate()).append(" - ").append(exp.getEndDate()).append("\n");
            for (String bullet : exp.getBullets()) {
                sb.append("  * ").append(bullet).append("\n");
            }
            sb.append("\n");
        }

        // PROJECTS
        sb.append("PROJECTS\n");
        sb.append("--------\n");
        for (Project proj : approved.getProjects()) {
            sb.append(proj.getTitle().toUpperCase());
            if (!proj.getTechStack().isEmpty()) {
                sb.append(" (").append(String.join(", ", proj.getTechStack())).append(")");
            }
            sb.append("\n");
            for (String bullet : proj.getBullets()) {
                sb.append("  * ").append(bullet).append("\n");
            }
            sb.append("\n");
        }

        // EDUCATION
        sb.append("EDUCATION\n");
        sb.append("---------\n");
        for (Education edu : approved.getEducation()) {
            sb.append(edu.getDegree()).append(" in ").append(edu.getFieldOfStudy()).append("\n");
            sb.append(edu.getInstitution());
            if (edu.getLocation() != null && !edu.getLocation().isEmpty()) {
                sb.append(" | ").append(edu.getLocation());
            }
            sb.append("\n");
            sb.append(edu.getStartDate()).append(" - ").append(edu.getEndDate());
            if (edu.getGrade() != null && !edu.getGrade().isEmpty()) {
                sb.append(" | Grade: ").append(edu.getGrade());
            }
            sb.append("\n\n");
        }

        // CERTIFICATIONS
        if (!approved.getCertifications().isEmpty()) {
            sb.append("CERTIFICATIONS & ACHIEVEMENTS\n");
            sb.append("-----------------------------\n");
            for (Certification cert : approved.getCertifications()) {
                sb.append("  * ").append(cert.getName()).append(" - ").append(cert.getIssuingOrganization()).append(" (").append(cert.getIssueDate()).append(")\n");
            }
            for (Achievement ach : approved.getAchievements()) {
                sb.append("  * ").append(ach.getTitle()).append(" - ").append(ach.getDescription()).append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
