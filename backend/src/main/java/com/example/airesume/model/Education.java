package com.example.airesume.model;

public class Education {
    private String id;
    private String institution;
    private String degree;
    private String fieldOfStudy;
    private String startDate;
    private String endDate;
    private String grade; // GPA or percentage
    private String location;
    private String source; // "resume", "manual", "both"

    public Education() {}

    public Education(String id, String institution, String degree, String fieldOfStudy, String startDate, String endDate, String grade, String location, String source) {
        this.id = id;
        this.institution = institution;
        this.degree = degree;
        this.fieldOfStudy = fieldOfStudy;
        this.startDate = startDate;
        this.endDate = endDate;
        this.grade = grade;
        this.location = location;
        this.source = source;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getFieldOfStudy() { return fieldOfStudy; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
