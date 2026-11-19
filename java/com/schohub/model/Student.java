package com.schohub.model;

import com.schohub.service.GwaConverter;
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
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A student account plus the base identity form and the details of the
 * selected branch (new application vs. renewal). Keeping everything on one
 * record means the dashboard can be rebuilt from a single login.
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
public class Student {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // ---------------------------------------------------------------
    // Base Student Identity (required for all)
    // ---------------------------------------------------------------
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "education_level")
    private EducationLevel educationLevel;

    @Column(name = "school")
    private String school;

    @Column(name = "school_year")
    private String schoolYear;

    @Column(name = "program")
    private String program;

    @Column(name = "gwa_value")
    private Double gwaValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "gwa_scale")
    private GwaScale gwaScale;

    @Column(name = "city")
    private String city;

    @Column(name = "province")
    private String province;

    @Column(name = "citizenship")
    private String citizenship;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex")
    private Sex sex;

    // ---------------------------------------------------------------
    // Branch selection
    // ---------------------------------------------------------------
    @Enumerated(EnumType.STRING)
    @Column(name = "application_path")
    private ApplicationPath applicationPath;

    // ---------------------------------------------------------------
    // Path A - new scholarship application
    // ---------------------------------------------------------------
    @Column(name = "barangay")
    private String barangay;

    @Column(name = "years_of_residency")
    private Integer yearsOfResidency;

    @Column(name = "parents_registered_voter")
    private Boolean parentsRegisteredVoter;

    @Column(name = "sibling_ebd_grantee")
    private Boolean siblingEbdGrantee;

    @Column(name = "annual_family_income")
    private BigDecimal annualFamilyIncome;

    @Column(name = "indigency_status")
    private Boolean indigencyStatus;

    @Column(name = "listahanan_beneficiary")
    private Boolean listahananBeneficiary;

    @Column(name = "has_failing_grades")
    private Boolean hasFailingDroppedIncompleteGrades;

    @Column(name = "lowest_subject_grade")
    private Double lowestSubjectGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "special_track")
    private SpecialTrack specialTrack;

    @Enumerated(EnumType.STRING)
    @Column(name = "class_rank")
    private ClassRank classRank;

    // ---------------------------------------------------------------
    // Path B - scholarship renewal
    // ---------------------------------------------------------------
    @Column(name = "active_scholarship_code")
    private String activeScholarshipCode;

    @Column(name = "enrolled_units")
    private Integer enrolledUnits;

    @Column(name = "previous_term_gwa_value")
    private Double previousTermGwaValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_term_gwa_scale")
    private GwaScale previousTermGwaScale;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_renewal_documents", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "document_name")
    private Set<String> renewalDocumentsOnHand = new LinkedHashSet<>();

    /** GWA converted to a percentage so every comparison uses one unit. */
    @Transient
    public Double getGwaPercent() {
        return GwaConverter.toPercent(gwaValue, gwaScale);
    }

    @Transient
    public Double getPreviousTermGwaPercent() {
        return GwaConverter.toPercent(previousTermGwaValue, previousTermGwaScale);
    }

    @Transient
    public Integer getAge() {
        return birthday == null ? null : Period.between(birthday, LocalDate.now()).getYears();
    }
}
