package com.example.airesume.model;

import java.util.ArrayList;
import java.util.List;

public class Suggestion {
    private String id;
    private String originalItemId;
    private String section; // "skills", "projects", "experience", "achievements", "certifications"
    private String title;   // e.g. skill name, project title, role name, certification name, achievement title
    private String subtitle;// e.g. company name, issuing organization, institution
    private String description;
    private String location;
    private String startDate;
    private String endDate;
    private String grade;
    private List<String> bullets = new ArrayList<>();
    private List<String> techStack = new ArrayList<>();
    private String credentialUrl;
    
    private int score;
    private String reason;
    private String source; // "resume", "manual", "both"
    private boolean approved = true; // Default to approved as shown in standard wizard flows

    public Suggestion() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOriginalItemId() { return originalItemId; }
    public void setOriginalItemId(String originalItemId) { this.originalItemId = originalItemId; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public List<String> getBullets() { return bullets; }
    public void setBullets(List<String> bullets) { this.bullets = bullets; }

    public List<String> getTechStack() { return techStack; }
    public void setTechStack(List<String> techStack) { this.techStack = techStack; }

    public String getCredentialUrl() { return credentialUrl; }
    public void setCredentialUrl(String credentialUrl) { this.credentialUrl = credentialUrl; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
}
