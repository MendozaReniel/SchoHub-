package com.schohub.service;

import com.schohub.model.GwaScale;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * SchoHub stores every GWA requirement as a percentage so that programs
 * published as "2.50 or better" can be compared with programs published as
 * "at least 85%". The 1.00 - 5.00 college scale is mapped with the usual
 * Philippine university equivalence table and interpolated in between.
 */
public final class GwaConverter {

    /** {college scale, equivalent percentage} anchor points, ascending scale. */
    private static final double[][] ANCHORS = {
            {1.00, 97}, {1.25, 94}, {1.50, 91}, {1.75, 88}, {2.00, 85},
            {2.25, 82}, {2.50, 79}, {2.75, 76}, {3.00, 72}, {4.00, 70}, {5.00, 65}
    };

    private static final double LOWEST_MAPPED_PERCENT = 65;
    private static final double HIGHEST_MAPPED_PERCENT = 97;

    private GwaConverter() {
    }

    /** Converts a GWA on the given scale into the equivalent percentage. */
    public static Double toPercent(Double value, GwaScale scale) {
        if (value == null || scale == null) {
            return null;
        }
        if (scale == GwaScale.PERCENTAGE) {
            return round(value);
        }
        for (int i = 0; i < ANCHORS.length - 1; i++) {
            double lowScale = ANCHORS[i][0];
            double lowPercent = ANCHORS[i][1];
            double highScale = ANCHORS[i + 1][0];
            double highPercent = ANCHORS[i + 1][1];
            if (value >= lowScale && value <= highScale) {
                double percent = lowPercent + (value - lowScale) * (highPercent - lowPercent) / (highScale - lowScale);
                return round(percent);
            }
        }
        return value < ANCHORS[0][0]
                ? round(ANCHORS[0][1])
                : round(ANCHORS[ANCHORS.length - 1][1]);
    }

    /** Inverse of {@link #toPercent}; null when the percentage has no scale equivalent. */
    public static Double toScale(double percent) {
        if (percent < LOWEST_MAPPED_PERCENT || percent > HIGHEST_MAPPED_PERCENT) {
            return null;
        }
        for (int i = 0; i < ANCHORS.length - 1; i++) {
            double lowPercent = ANCHORS[i][1];
            double highPercent = ANCHORS[i + 1][1];
            if (percent <= lowPercent && percent >= highPercent) {
                double lowScale = ANCHORS[i][0];
                double highScale = ANCHORS[i + 1][0];
                return round(lowScale + (lowPercent - percent) * (highScale - lowScale) / (lowPercent - highPercent));
            }
        }
        return null;
    }

    /** "85", "88.5" - trailing zeros stripped. */
    public static String percentText(double percent) {
        return new DecimalFormat("0.##").format(percent);
    }

    /** "85% (approx. 2.00 on the 1.00-5.00 scale)" */
    public static String describe(double percent) {
        Double scale = toScale(percent);
        String base = percentText(percent) + "%";
        if (scale == null) {
            return base;
        }
        return base + " (approx. " + String.format(Locale.US, "%.2f", scale) + " on the 1.00-5.00 scale)";
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
