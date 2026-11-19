package com.schohub.service;

import com.schohub.model.ApplicationPath;
import com.schohub.model.BatangasCityBarangays;
import com.schohub.model.GwaScale;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import com.schohub.repository.ScholarshipRepository;
import com.schohub.repository.StudentRepository;
import com.schohub.web.ApiException;
import com.schohub.web.dto.ScholarshipDtos.RenewalStatusResponse;
import com.schohub.web.dto.ScholarshipDtos.ScholarshipMatchResponse;
import com.schohub.web.dto.StudentDtos.DashboardResponse;
import com.schohub.web.dto.StudentDtos.NewApplicationRequest;
import com.schohub.web.dto.StudentDtos.PathSelectionRequest;
import com.schohub.web.dto.StudentDtos.ProfileStatus;
import com.schohub.web.dto.StudentDtos.ProfileUpdateRequest;
import com.schohub.web.dto.StudentDtos.RenewalRequest;
import com.schohub.web.dto.StudentDtos.StudentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProfileService {

    private final StudentRepository students;
    private final ScholarshipRepository scholarships;
    private final StudentMapper studentMapper;
    private final MatchingService matchingService;
    private final RenewalService renewalService;

    public ProfileService(StudentRepository students,
                          ScholarshipRepository scholarships,
                          StudentMapper studentMapper,
                          MatchingService matchingService,
                          RenewalService renewalService) {
        this.students = students;
        this.scholarships = scholarships;
        this.studentMapper = studentMapper;
        this.matchingService = matchingService;
        this.renewalService = renewalService;
    }

    public StudentResponse get(String studentId) {
        return studentMapper.toResponse(load(studentId));
    }

    public ProfileStatus status(String studentId) {
        return studentMapper.profileStatus(load(studentId));
    }

    @Transactional
    public StudentResponse updateProfile(String studentId, ProfileUpdateRequest request) {
        Student student = load(studentId);
        validateGwa(request.gwaValue(), request.gwaScale(), "GWA");
        if (!BatangasCityBarangays.isCity(request.city())) {
            throw ApiException.badRequest("SchoHub is for students who live in Batangas City. \""
                    + request.city().trim() + "\" is outside the city.");
        }

        student.setFullName(request.fullName().trim());
        student.setBirthday(request.birthday());
        student.setEducationLevel(request.educationLevel());
        student.setSchool(request.school().trim());
        student.setSchoolYear(request.schoolYear().trim());
        student.setProgram(request.program().trim());
        student.setGwaValue(request.gwaValue());
        student.setGwaScale(request.gwaScale());
        student.setCity(BatangasCityBarangays.CITY);
        student.setProvince(request.province().trim());
        student.setCitizenship(request.citizenship() == null ? null : request.citizenship().trim());
        student.setSex(request.sex());

        return studentMapper.toResponse(students.save(student));
    }

    /** PHASE 2: records which branch the student chose and its inputs. */
    @Transactional
    public StudentResponse selectPath(String studentId, PathSelectionRequest request) {
        Student student = load(studentId);

        if (request.applicationPath() == ApplicationPath.NEW_APPLICATION) {
            NewApplicationRequest branch = request.newApplication();
            if (branch == null) {
                throw ApiException.badRequest("New application details are required for the new application path.");
            }
            if (branch.lowestSubjectGrade() != null
                    && (branch.lowestSubjectGrade() < 0 || branch.lowestSubjectGrade() > 100)) {
                throw ApiException.badRequest("Lowest subject grade must be between 0 and 100.");
            }
            String barangay = BatangasCityBarangays.canonical(branch.barangay());
            if (barangay == null) {
                throw ApiException.badRequest("Choose a barangay of Batangas City. Batangas City has 105 barangays; \""
                        + branch.barangay().trim() + "\" is not one of them.");
            }
            student.setBarangay(barangay);
            student.setYearsOfResidency(branch.yearsOfResidency());
            student.setParentsRegisteredVoter(branch.parentsRegisteredVoter());
            student.setSiblingEbdGrantee(branch.siblingEbdGrantee());
            student.setAnnualFamilyIncome(branch.annualFamilyIncome());
            student.setIndigencyStatus(branch.indigencyStatus());
            student.setListahananBeneficiary(branch.listahananBeneficiary());
            student.setHasFailingDroppedIncompleteGrades(branch.hasFailingDroppedIncompleteGrades());
            student.setLowestSubjectGrade(branch.lowestSubjectGrade());
            student.setSpecialTrack(branch.specialTrack());
            student.setClassRank(branch.classRank());
        } else {
            RenewalRequest branch = request.renewal();
            if (branch == null) {
                throw ApiException.badRequest("Renewal details are required for the renewal path.");
            }
            validateGwa(branch.previousTermGwaValue(), branch.previousTermGwaScale(), "Previous term GWA");

            Scholarship scholarship = scholarships.findByCodeIgnoreCase(branch.activeScholarshipCode().trim())
                    .orElseThrow(() -> ApiException.notFound(
                            "No scholarship found with code " + branch.activeScholarshipCode() + "."));
            if (!Boolean.TRUE.equals(scholarship.getRenewalEnabled())) {
                throw ApiException.badRequest(scholarship.getName() + " does not accept renewals through SchoHub.");
            }

            student.setActiveScholarshipCode(scholarship.getCode());
            student.setEnrolledUnits(branch.enrolledUnits());
            student.setPreviousTermGwaValue(branch.previousTermGwaValue());
            student.setPreviousTermGwaScale(branch.previousTermGwaScale());
            Set<String> documents = branch.documentsOnHand() == null
                    ? Set.of()
                    : new LinkedHashSet<>(branch.documentsOnHand());
            student.setRenewalDocumentsOnHand(new LinkedHashSet<>(documents));
        }

        student.setApplicationPath(request.applicationPath());
        return studentMapper.toResponse(students.save(student));
    }

    /** PHASE 3: the dashboard differs per branch. */
    public DashboardResponse dashboard(String studentId, boolean includeIneligible) {
        Student student = load(studentId);
        ProfileStatus profile = studentMapper.profileStatus(student);

        List<ScholarshipMatchResponse> matches = List.of();
        RenewalStatusResponse renewal = null;
        String message;

        if (student.getApplicationPath() == null) {
            message = "Choose how you will use SchoHub: a new scholarship application or a scholarship renewal.";
        } else if (student.getApplicationPath() == ApplicationPath.RENEWAL) {
            renewal = renewalService.evaluate(student);
            message = renewal.message();
        } else if (!profile.complete()) {
            message = "Complete your student profile to unlock matching scholarships.";
        } else {
            matches = matchingService.matchesFor(student, includeIneligible);
            long eligible = matches.stream().filter(ScholarshipMatchResponse::eligible).count();
            message = eligible == 0
                    ? "No scholarship matches your current profile yet. Review the requirements shown below."
                    : eligible + " scholarship" + (eligible == 1 ? "" : "s") + " match your qualifications.";
        }

        return new DashboardResponse(
                studentMapper.toResponse(student),
                profile,
                student.getApplicationPath(),
                matches,
                renewal,
                message);
    }

    public List<ScholarshipMatchResponse> matches(String studentId, boolean includeIneligible) {
        return matchingService.matchesFor(load(studentId), includeIneligible);
    }

    public RenewalStatusResponse renewal(String studentId) {
        return renewalService.evaluate(load(studentId));
    }

    private Student load(String studentId) {
        return students.findById(studentId)
                .orElseThrow(() -> ApiException.notFound("Student account not found."));
    }

    private static void validateGwa(Double value, GwaScale scale, String label) {
        if (value == null || scale == null) {
            throw ApiException.badRequest(label + " and its scale are required.");
        }
        if (scale == GwaScale.PERCENTAGE && (value < 0 || value > 100)) {
            throw ApiException.badRequest(label + " must be between 0 and 100 when using the percentage scale.");
        }
        if (scale == GwaScale.COLLEGE_1_TO_5 && (value < 1.0 || value > 5.0)) {
            throw ApiException.badRequest(label + " must be between 1.00 and 5.00 when using the college scale.");
        }
    }
}
