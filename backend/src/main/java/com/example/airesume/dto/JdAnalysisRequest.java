package com.example.airesume.dto;

import jakarta.validation.constraints.NotBlank;

public class JdAnalysisRequest {
    @NotBlank(message = "Workflow ID is required")
    private String workflowId;

    @NotBlank(message = "Job description cannot be empty")
    private String jdText;

    public JdAnalysisRequest() {}

    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }

    public String getJdText() { return jdText; }
    public void setJdText(String jdText) { this.jdText = jdText; }
}
