package com.schohub.matching;

import java.util.Collection;
import java.util.Locale;

public final class RuleSupport {

    private RuleSupport() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    public static boolean sameText(String left, String right) {
        return normalize(left).equals(normalize(right));
    }

    public static boolean sameProvince(String left, String right) {
        return normalize(left).replace(" province", "").equals(normalize(right).replace(" province", ""));
    }

    public static boolean containsAny(String haystack, Collection<String> needles) {
        String normalized = normalize(haystack);
        if (normalized.isEmpty() || needles == null) {
            return false;
        }
        for (String needle : needles) {
            String candidate = normalize(needle);
            if (!candidate.isEmpty() && normalized.contains(candidate)) {
                return true;
            }
        }
        return false;
    }
}
