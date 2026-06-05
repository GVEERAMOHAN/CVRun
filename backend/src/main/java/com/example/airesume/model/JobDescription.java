package com.example.airesume.model;

import java.util.ArrayList;
import java.util.List;

public class JobDescription {
    private String rawText;
    private List<String> keywords = new ArrayList<>();
    private List<String> requiredSkills = new ArrayList<>();
    private List<String> responsibilities = new ArrayList<>();
    private String roleEmphasis;

    public JobDescription() {}

    public JobDescription(String rawText, List<String> keywords, List<String> requiredSkills, List<String> responsibilities, String roleEmphasis) {
        this.rawText = rawText;
        this.keywords = keywords != null ? keywords : new ArrayList<>();
        this.requiredSkills = requiredSkills != null ? requiredSkills : new ArrayList<>();
        this.responsibilities = responsibilities != null ? responsibilities : new ArrayList<>();
        this.roleEmphasis = roleEmphasis;
    }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public List<String> getResponsibilities() { return responsibilities; }
    public void setResponsibilities(List<String> responsibilities) { this.responsibilities = responsibilities; }

    public String getRoleEmphasis() { return roleEmphasis; }
    public void setRoleEmphasis(String roleEmphasis) { this.roleEmphasis = roleEmphasis; }
}
