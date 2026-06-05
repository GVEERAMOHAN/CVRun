package com.example.airesume.model;

import java.util.ArrayList;
import java.util.List;

public class ApprovedContent {
    private List<String> skills = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
    private List<Experience> experience = new ArrayList<>();
    private List<Education> education = new ArrayList<>();
    private List<Certification> certifications = new ArrayList<>();
    private List<Achievement> achievements = new ArrayList<>();
    private List<Link> links = new ArrayList<>();

    public ApprovedContent() {}

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }

    public List<Experience> getExperience() { return experience; }
    public void setExperience(List<Experience> experience) { this.experience = experience; }

    public List<Education> getEducation() { return education; }
    public void setEducation(List<Education> education) { this.education = education; }

    public List<Certification> getCertifications() { return certifications; }
    public void setCertifications(List<Certification> certifications) { this.certifications = certifications; }

    public List<Achievement> getAchievements() { return achievements; }
    public void setAchievements(List<Achievement> achievements) { this.achievements = achievements; }

    public List<Link> getLinks() { return links; }
    public void setLinks(List<Link> links) { this.links = links; }
}
