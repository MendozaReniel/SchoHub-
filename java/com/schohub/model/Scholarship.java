package com.schohub.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A pre-populated scholarship program. Every nullable criteria field is
 * optional: only the fields that are set are enforced by the matching engine,
 * which keeps a single table able to describe government, university and
 * private programs.
 */
@Entity
@Table(name = "scholarships")
@Getter
@Setter
@NoArgsConstructor
public class Scholarship {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    private ProviderType providerType;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "benefit")
    private String benefit;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scholarship_levels", joinColumns = @JoinColumn(name = "scholarship_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "education_level")
    private Set<EducationLevel> levels = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scholarship_gwa_thresholds", joinColumns = @JoinColumn(name = "scholarship_id"))
    private List<GwaThreshold> gwaThresholds = new ArrayList<>();

    // ---- residency & household ----
    @Column(name = "required_city")
    private String requiredCity;

    @Column(name = "required_province")
    private String requiredProvince;

    @Column(name = "required_barangay")
    private String requiredBarangay;

    @Column(name = "min_years_residency")
    private Integer minYearsResidency;

    @Column(name = "requires_voter_parents")
    private Boolean requiresRegisteredVoterParents;

    @Column(name = "requires_no_sibling_grantee")
    private Boolean requiresNoSiblingGrantee;

    @Column(name = "max_annual_income")
    private BigDecimal maxAnnualIncome;

    @Column(name = "requires_indigency")
    private Boolean requiresIndigency;

    @Column(name = "requires_listahanan")
    private Boolean requiresListahanan;

    // ---- academics ----
    @Column(name = "requires_no_failing_grades")
    private Boolean requiresNoFailingGrades;

    @Column(name = "min_lowest_subject_grade")
    private Double minLowestSubjectGrade;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scholarship_course_keywords", joinColumns = @JoinColumn(name = "scholarship_id"))
    @Column(name = "keyword")
    private Set<String> courseKeywords = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scholarship_school_keywords", joinColumns = @JoinColumn(name = "scholarship_id"))
    @Column(name = "keyword")
    private Set<String> schoolKeywords = new LinkedHashSet<>();

    @Column(name = "requires_ph_citizenship")
    private Boolean requiresPhilippineCitizenship;

    @Column(name = "female_only")
    private Boolean femaleOnly;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_class_rank")
    private ClassRank requiredClassRank;

    @Column(name = "requires_department_endorsement")
    private Boolean requiresDepartmentEndorsement;

    // ---- documents & links ----
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scholarship_specific_documents", joinColumns = @JoinColumn(name = "scholarship_id"))
    @Column(name = "document_name")
    private List<String> specificDocuments = new ArrayList<>();

    @Column(name = "application_url")
    private String applicationUrl;

    @Column(name = "notes", length = 1000)
    private String notes;

    // ---- renewal ----
    @Column(name = "renewal_enabled")
    private Boolean renewalEnabled = Boolean.TRUE;

    @Column(name = "retention_gwa_percent")
    private Double retentionGwaPercent;

    @Column(name = "min_units_for_renewal")
    private Integer minUnitsForRenewal = 15;

    /**
     * Strictest threshold that applies to the given level, falling back to the
     * level-agnostic threshold. Null means the program has no GWA cut-off.
     */
    public Double gwaFor(EducationLevel studentLevel) {
        Double fallback = null;
        for (GwaThreshold threshold : gwaThresholds) {
            if (threshold.getLevel() != null && threshold.getLevel() == studentLevel) {
                return threshold.getMinPercent();
            }
            if (threshold.getLevel() == null && threshold.getMinPercent() != null) {
                fallback = threshold.getMinPercent();
            }
        }
        return fallback;
    }
}
