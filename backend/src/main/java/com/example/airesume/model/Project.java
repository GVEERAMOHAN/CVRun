package com.example.airesume.model;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private String id;
    private String title;
    private String description;
    private List<String> bullets = new ArrayList<>();
    private List<String> techStack = new ArrayList<>();
    private List<String> domainTags = new ArrayList<>();
    private String source; // "resume", "manual", "both"

    public Project() {}

    public Project(String id, String title, String description, List<String> bullets, List<String> techStack, List<String> domainTags, String source) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.bullets = bullets != null ? bullets : new ArrayList<>();
        this.techStack = techStack != null ? techStack : new ArrayList<>();
        this.domainTags = domainTags != null ? domainTags : new ArrayList<>();
        this.source = source;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getBullets() { return bullets; }
    public void setBullets(List<String> bullets) { this.bullets = bullets; }

    public List<String> getTechStack() { return techStack; }
    public void setTechStack(List<String> techStack) { this.techStack = techStack; }

    public List<String> getDomainTags() { return domainTags; }
    public void setDomainTags(List<String> domainTags) { this.domainTags = domainTags; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
