package com.example.airesume.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Workflow {
    private String workflowId;
    private WorkflowStatus status = WorkflowStatus.CREATED;
    private ResumeData resumeData = new ResumeData();
    private ResumeData manualData = new ResumeData();
    private ResumeData mergedProfile = new ResumeData();
    private JobDescription jobDescription = new JobDescription();
    private List<Suggestion> suggestions = new ArrayList<>();
    private ApprovedContent approvedContent = new ApprovedContent();
    private GeneratedResume generatedResume;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public Workflow() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusMinutes(30); // Default TTL 30 minutes
    }

    public Workflow(String workflowId) {
        this();
        this.workflowId = workflowId;
    }

    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }

    public WorkflowStatus getStatus() { return status; }
    public void setStatus(WorkflowStatus status) { this.status = status; }

    public ResumeData getResumeData() { return resumeData; }
    public void setResumeData(ResumeData resumeData) { this.resumeData = resumeData; }

    public ResumeData getManualData() { return manualData; }
    public void setManualData(ResumeData manualData) { this.manualData = manualData; }

    public ResumeData getMergedProfile() { return mergedProfile; }
    public void setMergedProfile(ResumeData mergedProfile) { this.mergedProfile = mergedProfile; }

    public JobDescription getJobDescription() { return jobDescription; }
    public void setJobDescription(JobDescription jobDescription) { this.jobDescription = jobDescription; }

    public List<Suggestion> getSuggestions() { return suggestions; }
    public void setSuggestions(List<Suggestion> suggestions) { this.suggestions = suggestions; }

    public ApprovedContent getApprovedContent() { return approvedContent; }
    public void setApprovedContent(ApprovedContent approvedContent) { this.approvedContent = approvedContent; }

    public GeneratedResume getGeneratedResume() { return generatedResume; }
    public void setGeneratedResume(GeneratedResume generatedResume) { this.generatedResume = generatedResume; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
