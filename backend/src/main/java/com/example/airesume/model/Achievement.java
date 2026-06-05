package com.example.airesume.model;

public class Achievement {
    private String id;
    private String title;
    private String description;
    private String date;
    private String source; // "resume", "manual", "both"

    public Achievement() {}

    public Achievement(String id, String title, String description, String date, String source) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.source = source;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
