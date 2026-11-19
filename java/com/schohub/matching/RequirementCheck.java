package com.schohub.matching;

/**
 * Outcome of one eligibility criterion, e.g. "Minimum GWA: 85%" plus whether
 * the student satisfied it and a short explanation for the dashboard.
 * Advisory checks are information-only and never block a match.
 */
public record RequirementCheck(String requirement, boolean met, String detail, boolean advisory) {

    public static RequirementCheck pass(String requirement, String detail) {
        return new RequirementCheck(requirement, true, detail, false);
    }

    public static RequirementCheck fail(String requirement, String detail) {
        return new RequirementCheck(requirement, false, detail, false);
    }

    public static RequirementCheck advisory(String requirement, String detail) {
        return new RequirementCheck(requirement, true, detail, true);
    }

    public boolean blocking() {
        return !met && !advisory;
    }
}
