package com.schohub.service;

import com.schohub.matching.RequirementCheck;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import com.schohub.repository.ScholarshipRepository;
import com.schohub.web.dto.ScholarshipDtos.ChecklistItem;
import com.schohub.web.dto.ScholarshipDtos.RenewalStatusResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * PATH B of the flowchart: validates a renewal using the enrolled units, the
 * previous term GWA and the standard four renewal documents.
 */
@Service
public class RenewalService {

    private final ScholarshipRepository scholarships;
    private final ScholarshipMapper mapper;

    public RenewalService(ScholarshipRepository scholarships, ScholarshipMapper mapper) {
        this.scholarships = scholarships;
        this.mapper = mapper;
    }

    public RenewalStatusResponse evaluate(Student student) {
        List<ChecklistItem> checklist = mapper.renewalChecklist(student.getRenewalDocumentsOnHand());

        if (student.getActiveScholarshipCode() == null || student.getActiveScholarshipCode().isBlank()) {
            return new RenewalStatusResponse(
                    null, null, false,
                    student.getEnrolledUnits(), null,
                    student.getPreviousTermGwaPercent(), null,
                    List.of(), checklist, null,
                    "Select the scholarship you are renewing to validate your renewal.");
        }

        Scholarship scholarship = scholarships.findByCodeIgnoreCase(student.getActiveScholarshipCode()).orElse(null);
        if (scholarship == null) {
            return new RenewalStatusResponse(
                    student.getActiveScholarshipCode(), null, false,
                    student.getEnrolledUnits(), null,
                    student.getPreviousTermGwaPercent(), null,
                    List.of(), checklist, null,
                    "The selected scholarship is no longer in the SchoHub catalog.");
        }

        List<RequirementCheck> checks = new ArrayList<>();

        if (!Boolean.TRUE.equals(scholarship.getRenewalEnabled())) {
            checks.add(RequirementCheck.fail("Renewal available through SchoHub",
                    "This program does not process renewals through SchoHub; follow the provider's instructions instead."));
        }

        Integer requiredUnits = scholarship.getMinUnitsForRenewal();
        if (requiredUnits != null) {
            String requirement = "Enrolled units: at least " + requiredUnits + " (full academic load)";
            if (student.getEnrolledUnits() == null) {
                checks.add(RequirementCheck.fail(requirement, "Add your enrolled units for the current semester."));
            } else if (student.getEnrolledUnits() >= requiredUnits) {
                checks.add(RequirementCheck.pass(requirement,
                        "You are enrolled in " + student.getEnrolledUnits() + " units."));
            } else {
                checks.add(RequirementCheck.fail(requirement,
                        "You are enrolled in " + student.getEnrolledUnits() + " units, below the required full load."));
            }
        }

        Double retention = scholarship.getRetentionGwaPercent() != null
                ? scholarship.getRetentionGwaPercent()
                : scholarship.gwaFor(student.getEducationLevel());
        if (retention != null) {
            String requirement = "Retention GWA: at least " + GwaConverter.describe(retention);
            Double actual = student.getPreviousTermGwaPercent();
            if (actual == null) {
                checks.add(RequirementCheck.fail(requirement, "Add your certified previous term GWA."));
            } else if (actual + 1e-9 >= retention) {
                checks.add(RequirementCheck.pass(requirement,
                        "Your GWA of " + GwaConverter.percentText(actual) + "% meets the retention requirement."));
            } else {
                checks.add(RequirementCheck.fail(requirement,
                        "Your GWA of " + GwaConverter.percentText(actual) + "% is below the retention requirement."));
            }
        }

        long missingDocuments = checklist.stream().filter(item -> !item.onHand()).count();
        if (missingDocuments == 0) {
            checks.add(RequirementCheck.pass("Renewal documents", "All four renewal documents are on hand."));
        } else {
            checks.add(RequirementCheck.fail("Renewal documents",
                    missingDocuments + " of " + checklist.size() + " renewal documents still need to be prepared."));
        }

        boolean eligible = checks.stream().noneMatch(RequirementCheck::blocking);
        String message = eligible
                ? "You meet the renewal requirements. Prepare the four documents and submit them to "
                        + scholarship.getProvider() + "."
                : "Some renewal requirements are not met yet. Review the items below before filing.";

        return new RenewalStatusResponse(
                scholarship.getCode(),
                scholarship.getName(),
                eligible,
                student.getEnrolledUnits(),
                requiredUnits,
                student.getPreviousTermGwaPercent(),
                retention,
                checks.stream()
                        .map(check -> new com.schohub.web.dto.ScholarshipDtos.RequirementCheckResponse(
                                check.requirement(), check.met(), check.detail(), check.advisory()))
                        .toList(),
                checklist,
                scholarship.getApplicationUrl(),
                message);
    }

    /** Convenience for callers that only need the on-hand document set. */
    public Set<String> documentsOnHand(Student student) {
        return student.getRenewalDocumentsOnHand() == null ? Set.of() : student.getRenewalDocumentsOnHand();
    }
}
