package com.schohub.model;

/**
 * A student may enter a GWA either as a percentage (85, 88.5 ...) or on the
 * 1.00 - 5.00 college scale (1.50, 2.00 ...). The matching engine normalises
 * both to a percentage before comparing them with scholarship requirements.
 */
public enum GwaScale {
    PERCENTAGE("Percentage"),
    COLLEGE_1_TO_5("1.00 - 5.00 college scale");

    private final String label;

    GwaScale(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
