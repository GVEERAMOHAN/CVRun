package com.example.airesume.dto;

import com.example.airesume.model.ResumeData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MergeProfileRequest {
    @NotBlank(message = "Workflow ID is required")
    private String workflowId;

    @NotNull(message = "Profile manual data cannot be null")
    private ResumeData manualData;

    public MergeProfileRequest() {}

    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }

    public ResumeData getManualData() { return manualData; }
    public void setManualData(ResumeData manualData) { this.manualData = manualData; }
}
