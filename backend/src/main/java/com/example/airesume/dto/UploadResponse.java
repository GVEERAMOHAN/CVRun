package com.example.airesume.dto;

import com.example.airesume.model.ResumeData;
import com.example.airesume.model.WorkflowStatus;

public class UploadResponse {
    private String workflowId;
    private WorkflowStatus status;
    private ResumeData resumeData;

    public UploadResponse() {}

    public UploadResponse(String workflowId, WorkflowStatus status, ResumeData resumeData) {
        this.workflowId = workflowId;
        this.status = status;
        this.resumeData = resumeData;
    }

    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }

    public WorkflowStatus getStatus() { return status; }
    public void setStatus(WorkflowStatus status) { this.status = status; }

    public ResumeData getResumeData() { return resumeData; }
    public void setResumeData(ResumeData resumeData) { this.resumeData = resumeData; }
}
