package com.example.airesume.cache;

import com.example.airesume.model.Workflow;

public interface WorkflowCache {
    Workflow get(String workflowId);
    void put(String workflowId, Workflow workflow);
    void remove(String workflowId);
    boolean exists(String workflowId);
    void clear();
}
