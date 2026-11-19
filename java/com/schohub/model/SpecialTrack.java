package com.schohub.model;

public enum SpecialTrack {
    NONE("None"),
    STEM_IT("STEM / IT priority"),
    VARSITY_ATHLETIC("Varsity / Athletic"),
    CULTURAL_NROTC("Cultural / NROTC"),
    WORK_STUDY("Work-Study");

    private final String label;

    SpecialTrack(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
