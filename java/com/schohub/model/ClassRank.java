package com.schohub.model;

public enum ClassRank {
    VALEDICTORIAN("Valedictorian (Rank 1)"),
    SALUTATORIAN("Salutatorian (Rank 2)"),
    FIRST_HONORABLE_MENTION("First Honorable Mention");

    private final String label;

    ClassRank(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
