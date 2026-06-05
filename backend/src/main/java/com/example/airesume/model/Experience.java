package com.example.airesume.model;

import java.util.ArrayList;
import java.util.List;

public class Experience {
    private String id;
    private String company;
    private String role;
    private String location;
    private String startDate;
    private String endDate;
    private List<String> bullets = new ArrayList<>();
    private List<String> techStack = new ArrayList<>();
    private String source; // "resume", "manual", "both"

    public Experience() {}

    public Experience(String id, String company, String role, String location, String startDate, String endDate, List<String> bullets, List<String> techStack, String source) {
        this.id = id;
        this.company = company;
        this.role = role;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bullets = bullets != null ? bullets : new ArrayList<>();
        this.techStack = techStack != null ? techStack : new ArrayList<>();
        this.source = source;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public List<String> getBullets() { return bullets; }
    public void setBullets(List<String> bullets) { this.bullets = bullets; }

    public List<String> getTechStack() { return techStack; }
    public void setTechStack(List<String> techStack) { this.techStack = techStack; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
