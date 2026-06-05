package com.example.airesume.service;

import com.example.airesume.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SuggestionRankingService {

    public List<Suggestion> generateSuggestions(ResumeData mergedProfile, JobDescription jd) {
        List<Suggestion> suggestions = new ArrayList<>();
        int idCounter = 1;

        // 1. Process Skills
        if (mergedProfile.getSkills() != null) {
            for (String skill : mergedProfile.getSkills()) {
                Suggestion sug = new Suggestion();
                sug.setId("sug_skill_" + idCounter++);
                sug.setSection("skills");
                sug.setTitle(skill);
                sug.setSource(mergedProfile.getSkills().contains(skill) ? "resume" : "manual");
                
                // Score skill
                boolean matchesJd = containsIgnoreCase(jd.getRequiredSkills(), skill) || jd.getRawText().toLowerCase().contains(skill.toLowerCase());
                int score = matchesJd ? 95 : 60;
                sug.setScore(score);
                sug.setReason(matchesJd ? "High match for backend/frontend development stack in JD" : "General technical skill in profile");
                
                suggestions.add(sug);
            }
        }

        // 2. Process Projects
        if (mergedProfile.getProjects() != null) {
            for (Project p : mergedProfile.getProjects()) {
                Suggestion sug = new Suggestion();
                sug.setId("sug_proj_" + idCounter++);
                sug.setOriginalItemId(p.getId());
                sug.setSection("projects");
                sug.setTitle(p.getTitle());
                sug.setDescription(p.getDescription());
                sug.setBullets(new ArrayList<>(p.getBullets()));
                sug.setTechStack(new ArrayList<>(p.getTechStack()));
                sug.setSource(p.getSource());

                int score = calculateProjectScore(p, jd);
                sug.setScore(score);
                sug.setReason(generateReason("Project", p.getTitle(), p.getTechStack(), jd, score));
                
                suggestions.add(sug);
            }
        }

        // 3. Process Experience
        if (mergedProfile.getExperience() != null) {
            for (Experience exp : mergedProfile.getExperience()) {
                Suggestion sug = new Suggestion();
                sug.setId("sug_exp_" + idCounter++);
                sug.setOriginalItemId(exp.getId());
                sug.setSection("experience");
                sug.setTitle(exp.getRole());
                sug.setSubtitle(exp.getCompany());
                sug.setLocation(exp.getLocation());
                sug.setStartDate(exp.getStartDate());
                sug.setEndDate(exp.getEndDate());
                sug.setBullets(new ArrayList<>(exp.getBullets()));
                sug.setTechStack(new ArrayList<>(exp.getTechStack()));
                sug.setSource(exp.getSource());

                int score = calculateExperienceScore(exp, jd);
                sug.setScore(score);
                sug.setReason(generateReason("Experience", exp.getRole(), exp.getTechStack(), jd, score));

                suggestions.add(sug);
            }
        }

        // 4. Process Achievements
        if (mergedProfile.getAchievements() != null) {
            for (Achievement ach : mergedProfile.getAchievements()) {
                Suggestion sug = new Suggestion();
                sug.setId("sug_ach_" + idCounter++);
                sug.setOriginalItemId(ach.getId());
                sug.setSection("achievements");
                sug.setTitle(ach.getTitle());
                sug.setDescription(ach.getDescription());
                sug.setStartDate(ach.getDate());
                sug.setSource(ach.getSource());

                int score = calculateAchievementScore(ach, jd);
                sug.setScore(score);
                sug.setReason("Aligns with analytical and delivery goals of the role");

                suggestions.add(sug);
            }
        }

        // 5. Process Certifications
        if (mergedProfile.getCertifications() != null) {
            for (Certification cert : mergedProfile.getCertifications()) {
                Suggestion sug = new Suggestion();
                sug.setId("sug_cert_" + idCounter++);
                sug.setOriginalItemId(cert.getId());
                sug.setSection("certifications");
                sug.setTitle(cert.getName());
                sug.setSubtitle(cert.getIssuingOrganization());
                sug.setStartDate(cert.getIssueDate());
                sug.setCredentialUrl(cert.getCredentialUrl());
                sug.setSource(cert.getSource());

                int score = calculateCertificationScore(cert, jd);
                sug.setScore(score);
                sug.setReason("Verifies technical knowledge required by the recruiter");

                suggestions.add(sug);
            }
        }

        // Sort suggestions primarily by relevance score descending
        suggestions.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return suggestions;
    }

    private int calculateProjectScore(Project p, JobDescription jd) {
        double keywordMatch = calculateListMatch(p.getBullets(), jd.getKeywords());
        double techMatch = calculateListMatch(p.getTechStack(), jd.getRequiredSkills());
        
        double roleAlignment = 0.5;
        if (p.getTitle().toLowerCase().contains("ai") || p.getTitle().toLowerCase().contains("web") || p.getTitle().toLowerCase().contains("cloud")) {
            roleAlignment = 1.0;
        }

        double responsibilityMatch = calculateStringListMatch(p.getBullets(), jd.getResponsibilities());
        double userPriority = "manual".equalsIgnoreCase(p.getSource()) || "both".equalsIgnoreCase(p.getSource()) ? 1.0 : 0.7;

        double finalScore = (keywordMatch * 0.35) + (techMatch * 0.25) + (roleAlignment * 0.20) + (responsibilityMatch * 0.15) + (userPriority * 0.05);
        return normalizeScore(finalScore);
    }

    private int calculateExperienceScore(Experience exp, JobDescription jd) {
        double keywordMatch = calculateListMatch(exp.getBullets(), jd.getKeywords());
        double techMatch = calculateListMatch(exp.getTechStack(), jd.getRequiredSkills());
        
        double roleAlignment = 0.5;
        if (exp.getRole().toLowerCase().contains("developer") || exp.getRole().toLowerCase().contains("engineer")) {
            roleAlignment = 1.0;
        }

        double responsibilityMatch = calculateStringListMatch(exp.getBullets(), jd.getResponsibilities());
        double userPriority = "manual".equalsIgnoreCase(exp.getSource()) || "both".equalsIgnoreCase(exp.getSource()) ? 1.0 : 0.7;

        double finalScore = (keywordMatch * 0.35) + (techMatch * 0.25) + (roleAlignment * 0.20) + (responsibilityMatch * 0.15) + (userPriority * 0.05);
        return normalizeScore(finalScore);
    }

    private int calculateAchievementScore(Achievement ach, JobDescription jd) {
        // Simple logic matching text
        double textMatch = jd.getRawText().toLowerCase().contains(ach.getTitle().toLowerCase()) ? 1.0 : 0.3;
        double finalScore = (textMatch * 0.8) + 0.2;
        return normalizeScore(finalScore);
    }

    private int calculateCertificationScore(Certification cert, JobDescription jd) {
        double textMatch = jd.getRawText().toLowerCase().contains(cert.getName().toLowerCase()) ? 1.0 : 0.4;
        double finalScore = (textMatch * 0.8) + 0.2;
        return normalizeScore(finalScore);
    }

    private double calculateListMatch(List<String> sources, List<String> targets) {
        if (sources == null || sources.isEmpty() || targets == null || targets.isEmpty()) {
            return 0.3; // Baseline score
        }
        long matches = targets.stream()
                .filter(t -> sources.stream().anyMatch(s -> s.toLowerCase().contains(t.toLowerCase())))
                .count();
        return Math.min(1.0, (double) matches / Math.min(5, targets.size()));
    }

    private double calculateStringListMatch(List<String> sources, List<String> targets) {
        if (sources == null || sources.isEmpty() || targets == null || targets.isEmpty()) {
            return 0.3;
        }
        long matches = 0;
        for (String target : targets) {
            for (String source : sources) {
                if (source.toLowerCase().contains(target.toLowerCase()) || target.toLowerCase().contains(source.toLowerCase())) {
                    matches++;
                    break;
                }
            }
        }
        return Math.min(1.0, (double) matches / Math.min(3, targets.size()));
    }

    private int normalizeScore(double rawScore) {
        // Map rawScore (0.0 to 1.0) linearly to (50 to 98) range
        int score = 50 + (int) (rawScore * 48);
        return Math.max(50, Math.min(98, score));
    }

    private String generateReason(String type, String title, List<String> techStack, JobDescription jd, int score) {
        if (score >= 90) {
            String bestTech = findMatchingTech(techStack, jd.getRequiredSkills());
            if (bestTech != null) {
                return "High match for " + bestTech + " development and Spring ecosystem in JD";
            }
            return type + " emphasizes scalable design and microservices architecture";
        } else if (score >= 80) {
            return "Good alignment with core technologies and responsibility list";
        } else {
            return "General background alignment with software engineering skills";
        }
    }

    private String findMatchingTech(List<String> techStack, List<String> skills) {
        if (techStack == null || skills == null) return null;
        for (String t : techStack) {
            for (String s : skills) {
                if (t.equalsIgnoreCase(s)) {
                    return t;
                }
            }
        }
        return null;
    }

    private boolean containsIgnoreCase(List<String> list, String val) {
        if (list == null || val == null) return false;
        return list.stream().anyMatch(v -> v.equalsIgnoreCase(val));
    }
}
