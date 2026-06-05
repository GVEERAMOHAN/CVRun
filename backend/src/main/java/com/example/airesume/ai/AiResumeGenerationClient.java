package com.example.airesume.ai;

import com.example.airesume.model.ApprovedContent;
import com.example.airesume.model.JobDescription;

public interface AiResumeGenerationClient {
    String generate(ApprovedContent approvedContent, JobDescription jobDescription);
}
