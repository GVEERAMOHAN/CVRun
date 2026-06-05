package com.example.airesume.model;

import java.time.LocalDateTime;

public class GeneratedResume {
    private String plainText;
    private LocalDateTime generatedAt;

    public GeneratedResume() {}

    public GeneratedResume(String plainText) {
        this.plainText = plainText;
        this.generatedAt = LocalDateTime.now();
    }

    public String getPlainText() { return plainText; }
    public void setPlainText(String plainText) { this.plainText = plainText; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
