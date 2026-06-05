package com.example.airesume.dto;

import com.example.airesume.model.ApprovedContent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ApprovalRequest {
    @NotBlank(message = "Workflow ID is required")
    private String workflowId;

    @NotNull(message = "Approved content cannot be null")
    private ApprovedContent approvedContent;

    public ApprovalRequest() {}

    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }

    public ApprovedContent getApprovedContent() { return approvedContent; }
    public void setApprovedContent(ApprovedContent approvedContent) { this.approvedContent = approvedContent; }
}
