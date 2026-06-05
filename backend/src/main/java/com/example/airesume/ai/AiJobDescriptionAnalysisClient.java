package com.example.airesume.ai;

import com.example.airesume.model.JobDescription;

public interface AiJobDescriptionAnalysisClient {
    JobDescription analyze(String jdText);
}
