package com.schohub.model;

public enum EducationLevel {
    JUNIOR_HIGH("Junior High School"),
    SENIOR_HIGH("Senior High School"),
    COLLEGE("College");

    private final String label;

    EducationLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
