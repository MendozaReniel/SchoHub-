package com.schohub.web.dto;

import com.schohub.model.ApplicationPath;
import com.schohub.model.ClassRank;
import com.schohub.model.EducationLevel;
import com.schohub.model.GwaScale;
import com.schohub.model.Sex;
import com.schohub.model.SpecialTrack;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.schohub.web.dto.ScholarshipDtos.RenewalStatusResponse;
import static com.schohub.web.dto.ScholarshipDtos.ScholarshipMatchResponse;

public final class StudentDtos {

    private StudentDtos() {
    }

    // ------------------------------------------------------------------
    // Requests
    // ------------------------------------------------------------------

    /** PHASE 2 base identity form, required for every student. */
    public record ProfileUpdateRequest(
            @NotBlank(message = "Full name is required.")
            String fullName,

            @NotNull(message = "Birthday is required.")
            @Past(message = "Birthday must be in the past.")
            LocalDate birthday,

            @NotNull(message = "Education level is required.")
            EducationLevel educationLevel,

            @NotBlank(message = "School is required.")
            String school,

            @NotBlank(message = "School year is required.")
            String schoolYear,

            @NotBlank(message = "Program / course is required.")
            String program,

            @NotNull(message = "GWA is required.")
            Double gwaValue,

            @NotNull(message = "GWA scale is required.")
            GwaScale gwaScale,

            @NotBlank(message = "City / municipality is required.")
            String city,

            @NotBlank(message = "Province is required.")
            String province,

            String citizenship,
            Sex sex) {
    }

    /** PATH A: new scholarship application inputs. */
    public record NewApplicationRequest(
            @NotBlank(message = "Barangay is required.")
            String barangay,

            @NotNull(message = "Years of residency is required.")
            @Min(value = 0, message = "Years of residency cannot be negative.")
            Integer yearsOfResidency,

            @NotNull(message = "Answer whether your parents are registered voters.")
            Boolean parentsRegisteredVoter,

            @NotNull(message = "Answer whether a sibling is already a grantee.")
            Boolean siblingEbdGrantee,

            @DecimalMin(value = "0", message = "Family income cannot be negative.")
            BigDecimal annualFamilyIncome,

            Boolean indigencyStatus,
            Boolean listahananBeneficiary,

            @NotNull(message = "Confirm your academic standing.")
            Boolean hasFailingDroppedIncompleteGrades,

            Double lowestSubjectGrade,
            SpecialTrack specialTrack,
            ClassRank classRank) {
    }

    /** PATH B: scholarship renewal inputs. */
    public record RenewalRequest(
            @NotBlank(message = "Select the scholarship you are renewing.")
            String activeScholarshipCode,

            @NotNull(message = "Enrolled units are required.")
            @Min(value = 0, message = "Enrolled units cannot be negative.")
            Integer enrolledUnits,

            @NotNull(message = "Previous term GWA is required.")
            Double previousTermGwaValue,

            @NotNull(message = "Previous term GWA scale is required.")
            GwaScale previousTermGwaScale,

            Set<String> documentsOnHand) {
    }

    /** Decision node: Path A or Path B. */
    public record PathSelectionRequest(
            @NotNull(message = "Choose a new application or a renewal.")
            ApplicationPath applicationPath,

            NewApplicationRequest newApplication,
            RenewalRequest renewal) {
    }

    // ------------------------------------------------------------------
    // Responses
    // ------------------------------------------------------------------

    public record BaseIdentity(
            String fullName,
            LocalDate birthday,
            Integer age,
            EducationLevel educationLevel,
            String school,
            String schoolYear,
            String program,
            Double gwaValue,
            GwaScale gwaScale,
            Double gwaPercent,
            String city,
            String province,
            String citizenship,
            Sex sex) {
    }

    public record NewApplicationDetails(
            String barangay,
            Integer yearsOfResidency,
            Boolean parentsRegisteredVoter,
            Boolean siblingEbdGrantee,
            BigDecimal annualFamilyIncome,
            Boolean indigencyStatus,
            Boolean listahananBeneficiary,
            Boolean hasFailingDroppedIncompleteGrades,
            Double lowestSubjectGrade,
            SpecialTrack specialTrack,
            ClassRank classRank) {
    }

    public record RenewalDetails(
            String activeScholarshipCode,
            String activeScholarshipName,
            Integer enrolledUnits,
            Double previousTermGwaValue,
            GwaScale previousTermGwaScale,
            Double previousTermGwaPercent,
            Set<String> documentsOnHand) {
    }

    public record StudentResponse(
            String id,
            String email,
            String username,
            Instant createdAt,
            ApplicationPath applicationPath,
            BaseIdentity baseIdentity,
            NewApplicationDetails newApplication,
            RenewalDetails renewal) {
    }

    public record ProfileStatus(
            boolean complete,
            List<String> missingFields,
            List<String> recommendations) {
    }

    public record DashboardResponse(
            StudentResponse student,
            ProfileStatus profile,
            ApplicationPath applicationPath,
            List<ScholarshipMatchResponse> matches,
            RenewalStatusResponse renewal,
            String message) {
    }
}
