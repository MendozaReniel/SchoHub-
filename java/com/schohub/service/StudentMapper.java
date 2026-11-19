package com.schohub.service;

import com.schohub.model.ApplicationPath;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import com.schohub.repository.ScholarshipRepository;
import com.schohub.web.dto.StudentDtos.BaseIdentity;
import com.schohub.web.dto.StudentDtos.NewApplicationDetails;
import com.schohub.web.dto.StudentDtos.ProfileStatus;
import com.schohub.web.dto.StudentDtos.RenewalDetails;
import com.schohub.web.dto.StudentDtos.StudentResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class StudentMapper {

    private final ScholarshipRepository scholarships;

    public StudentMapper(ScholarshipRepository scholarships) {
        this.scholarships = scholarships;
    }

    public StudentResponse toResponse(Student student) {
        BaseIdentity identity = new BaseIdentity(
                student.getFullName(),
                student.getBirthday(),
                student.getAge(),
                student.getEducationLevel(),
                student.getSchool(),
                student.getSchoolYear(),
                student.getProgram(),
                student.getGwaValue(),
                student.getGwaScale(),
                student.getGwaPercent(),
                student.getCity(),
                student.getProvince(),
                student.getCitizenship(),
                student.getSex());

        NewApplicationDetails newApplication = new NewApplicationDetails(
                student.getBarangay(),
                student.getYearsOfResidency(),
                student.getParentsRegisteredVoter(),
                student.getSiblingEbdGrantee(),
                student.getAnnualFamilyIncome(),
                student.getIndigencyStatus(),
                student.getListahananBeneficiary(),
                student.getHasFailingDroppedIncompleteGrades(),
                student.getLowestSubjectGrade(),
                student.getSpecialTrack(),
                student.getClassRank());

        String activeName = null;
        if (student.getActiveScholarshipCode() != null) {
            activeName = scholarships.findByCodeIgnoreCase(student.getActiveScholarshipCode())
                    .map(Scholarship::getName)
                    .orElse(null);
        }
        RenewalDetails renewal = new RenewalDetails(
                student.getActiveScholarshipCode(),
                activeName,
                student.getEnrolledUnits(),
                student.getPreviousTermGwaValue(),
                student.getPreviousTermGwaScale(),
                student.getPreviousTermGwaPercent(),
                new LinkedHashSet<>(student.getRenewalDocumentsOnHand()));

        return new StudentResponse(
                student.getId(),
                student.getEmail(),
                student.getUsername(),
                student.getCreatedAt(),
                student.getApplicationPath(),
                identity,
                newApplication,
                renewal);
    }

    /**
     * Tells the frontend which fields are still missing before the dashboard
     * can show reliable matches, and which optional fields unlock extra grants.
     */
    public ProfileStatus profileStatus(Student student) {
        List<String> missing = new ArrayList<>();
        if (isBlank(student.getFullName())) {
            missing.add("Full name");
        }
        if (student.getBirthday() == null) {
            missing.add("Birthday");
        }
        if (student.getEducationLevel() == null) {
            missing.add("Education level");
        }
        if (isBlank(student.getSchool())) {
            missing.add("School / university");
        }
        if (isBlank(student.getSchoolYear())) {
            missing.add("School year");
        }
        if (isBlank(student.getProgram())) {
            missing.add("Program / course");
        }
        if (student.getGwaValue() == null || student.getGwaScale() == null) {
            missing.add("Previous semester GWA");
        }
        if (isBlank(student.getCity())) {
            missing.add("City / municipality");
        }
        if (isBlank(student.getProvince())) {
            missing.add("Province");
        }

        if (student.getApplicationPath() == ApplicationPath.NEW_APPLICATION) {
            if (isBlank(student.getBarangay())) {
                missing.add("Barangay");
            }
            if (student.getYearsOfResidency() == null) {
                missing.add("Years of residency");
            }
            if (student.getParentsRegisteredVoter() == null) {
                missing.add("Parents' voter registration status");
            }
            if (student.getSiblingEbdGrantee() == null) {
                missing.add("Sibling grantee status");
            }
            if (student.getHasFailingDroppedIncompleteGrades() == null) {
                missing.add("Academic standing confirmation");
            }
        }

        if (student.getApplicationPath() == ApplicationPath.RENEWAL) {
            if (isBlank(student.getActiveScholarshipCode())) {
                missing.add("Scholarship being renewed");
            }
            if (student.getEnrolledUnits() == null) {
                missing.add("Enrolled units");
            }
            if (student.getPreviousTermGwaValue() == null || student.getPreviousTermGwaScale() == null) {
                missing.add("Previous term GWA");
            }
        }

        List<String> recommendations = new ArrayList<>();
        if (isBlank(student.getCitizenship())) {
            recommendations.add("Add your citizenship so CHED-sponsored grants can be evaluated.");
        }
        if (student.getSex() == null) {
            recommendations.add("Add your sex so gender-restricted grants (e.g. Soroptimist International) can be evaluated.");
        }
        if (student.getAnnualFamilyIncome() == null) {
            recommendations.add("Add your household income to check income-capped grants such as CHED Tulong Dunong.");
        }
        if (student.getLowestSubjectGrade() == null) {
            recommendations.add("Add your lowest subject grade to check grants that require no grade below a certain mark (e.g. Westmead).");
        }
        if (student.getClassRank() == null) {
            recommendations.add("Add your graduating rank to check merit grants for valedictorians and salutatorians (e.g. University of Batangas).");
        }

        return new ProfileStatus(missing.isEmpty(), missing, recommendations);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
