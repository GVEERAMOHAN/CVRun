package com.example.airesume.service;

import com.example.airesume.model.Workflow;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class TextExportService {

    public byte[] exportToPlainText(Workflow workflow) {
        if (workflow.getGeneratedResume() == null || workflow.getGeneratedResume().getPlainText() == null) {
            throw new IllegalStateException("No resume has been generated yet for this workflow.");
        }
        
        String text = workflow.getGeneratedResume().getPlainText();
        return text.getBytes(StandardCharsets.UTF_8);
    }
}
