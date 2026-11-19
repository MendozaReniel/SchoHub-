package com.schohub.model;

/**
 * PHASE 2 decision node of the flowchart: new applicant or renewal.
 */
public enum ApplicationPath {
    NEW_APPLICATION("New Scholarship Application"),
    RENEWAL("Scholarship Renewal");

    private final String label;

    ApplicationPath(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
