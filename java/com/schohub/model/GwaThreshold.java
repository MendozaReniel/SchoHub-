package com.schohub.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * A single GWA requirement. A scholarship may hold several thresholds, for
 * example EBD needs 85% for high school applicants but 2.50 (79%) for college.
 * A null level means "applies to every level".
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GwaThreshold {

    @Enumerated(EnumType.STRING)
    @Column(name = "education_level")
    private EducationLevel level;

    /** Minimum acceptable GWA already normalised to a percentage. */
    @Column(name = "min_percent")
    private Double minPercent;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GwaThreshold that)) {
            return false;
        }
        return level == that.level && Objects.equals(minPercent, that.minPercent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, minPercent);
    }
}
