package com.schohub.service;

import com.schohub.matching.RequirementCheck;
import com.schohub.model.EducationLevel;
import com.schohub.model.GwaThreshold;
import com.schohub.model.Scholarship;
import com.schohub.web.dto.ScholarshipDtos.ChecklistItem;
import com.schohub.web.dto.ScholarshipDtos.RequirementCheckResponse;
import com.schohub.web.dto.ScholarshipDtos.ScholarshipDetail;
import com.schohub.web.dto.ScholarshipDtos.ScholarshipMatchResponse;
import com.schohub.web.dto.ScholarshipDtos.ScholarshipSummary;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class ScholarshipMapper {

    private static final Locale PH = Locale.forLanguageTag("en-PH");

    public ScholarshipSummary toSummary(Scholarship scholarship) {
        return new ScholarshipSummary(
                scholarship.getCode(),
                scholarship.getName(),
                scholarship.getProvider(),
                scholarship.getProviderType(),
                scholarship.getBenefit(),
                levelsOf(scholarship),
                Boolean.TRUE.equals(scholarship.getRenewalEnabled()));
    }

    public ScholarshipDetail toDetail(Scholarship scholarship) {
        return new ScholarshipDetail(
                scholarship.getCode(),
                scholarship.getName(),
                scholarship.getProvider(),
                scholarship.getProviderType(),
                scholarship.getDescription(),
                scholarship.getBenefit(),
                levelsOf(scholarship),
                gwaRequirementTexts(scholarship),
                residencyRequirementTexts(scholarship),
                otherRequirementTexts(scholarship),
                List.copyOf(scholarship.getSchoolKeywords()),
                List.copyOf(scholarship.getCourseKeywords()),
                DocumentCatalog.CORE_DOCUMENTS,
                List.copyOf(scholarship.getSpecificDocuments()),
                DocumentCatalog.RENEWAL_DOCUMENTS,
                scholarship.getApplicationUrl(),
                scholarship.getNotes(),
                Boolean.TRUE.equals(scholarship.getRenewalEnabled()),
                scholarship.getMinUnitsForRenewal(),
                scholarship.getRetentionGwaPercent(),
                scholarship.gwaFor(EducationLevel.COLLEGE));
    }

    public ScholarshipMatchResponse toMatch(Scholarship scholarship, List<RequirementCheck> checks) {
        boolean eligible = checks.stream().noneMatch(RequirementCheck::blocking);
        long met = checks.stream().filter(RequirementCheck::met).count();
        long unmet = checks.stream().filter(RequirementCheck::blocking).count();

        String summary;
        if (eligible) {
            summary = "You meet all " + checks.size() + " checked requirement"
                    + (checks.size() == 1 ? "" : "s") + " for this grant.";
        } else {
            summary = "You meet " + met + " of " + checks.size() + " requirements; "
                    + unmet + " still need" + (unmet == 1 ? "s" : "") + " attention.";
        }

        List<RequirementCheckResponse> responses = checks.stream()
                .map(check -> new RequirementCheckResponse(
                        check.requirement(), check.met(), check.detail(), check.advisory()))
                .toList();

        return new ScholarshipMatchResponse(
                toSummary(scholarship),
                eligible,
                responses,
                DocumentCatalog.CORE_DOCUMENTS,
                List.copyOf(scholarship.getSpecificDocuments()),
                DocumentCatalog.RENEWAL_DOCUMENTS,
                scholarship.getApplicationUrl(),
                summary);
    }

    public List<ChecklistItem> renewalChecklist(Set<String> documentsOnHand) {
        Set<String> onHand = documentsOnHand == null ? Set.of() : documentsOnHand;
        return DocumentCatalog.RENEWAL_DOCUMENTS.stream()
                .map(document -> new ChecklistItem(document, onHand.contains(document)))
                .toList();
    }

    // ------------------------------------------------------------------
    // Human readable requirement summaries
    // ------------------------------------------------------------------

    public List<String> gwaRequirementTexts(Scholarship scholarship) {
        List<GwaThreshold> thresholds = new ArrayList<>(scholarship.getGwaThresholds());
        thresholds.sort(Comparator.comparing(
                threshold -> threshold.getLevel() == null ? 0 : threshold.getLevel().ordinal() + 1));
        return thresholds.stream()
                .filter(threshold -> threshold.getMinPercent() != null)
                .map(threshold -> (threshold.getLevel() == null ? "All levels" : threshold.getLevel().getLabel())
                        + ": at least " + GwaConverter.describe(threshold.getMinPercent()))
                .toList();
    }

    public List<String> residencyRequirementTexts(Scholarship scholarship) {
        List<String> texts = new ArrayList<>();
        if (scholarship.getRequiredBarangay() != null) {
            texts.add("Resident of Barangay " + scholarship.getRequiredBarangay());
        }
        if (scholarship.getRequiredCity() != null) {
            texts.add("Resident of " + scholarship.getRequiredCity());
        }
        if (scholarship.getRequiredProvince() != null) {
            texts.add("Resident of " + scholarship.getRequiredProvince());
        }
        if (scholarship.getMinYearsResidency() != null) {
            texts.add("At least " + scholarship.getMinYearsResidency() + " years of residency");
        }
        return texts;
    }

    public List<String> otherRequirementTexts(Scholarship scholarship) {
        List<String> texts = new ArrayList<>();
        if (Boolean.TRUE.equals(scholarship.getRequiresRegisteredVoterParents())) {
            texts.add("Parents must be registered voters");
        }
        if (Boolean.TRUE.equals(scholarship.getRequiresNoSiblingGrantee())) {
            texts.add("No sibling currently receiving the same grant");
        }
        if (scholarship.getMaxAnnualIncome() != null) {
            texts.add("Annual family income not exceeding "
                    + NumberFormat.getCurrencyInstance(PH).format(scholarship.getMaxAnnualIncome()));
        }
        if (Boolean.TRUE.equals(scholarship.getRequiresIndigency())) {
            texts.add("Indigent / low-income household");
        }
        if (Boolean.TRUE.equals(scholarship.getRequiresListahanan())) {
            texts.add("Listahanan (DSWD) beneficiary household");
        }
        if (Boolean.TRUE.equals(scholarship.getRequiresNoFailingGrades())) {
            texts.add("No failing, dropped, or incomplete grades");
        }
        if (scholarship.getMinLowestSubjectGrade() != null) {
            texts.add("No individual grade below "
                    + GwaConverter.percentText(scholarship.getMinLowestSubjectGrade()) + "%");
        }
        if (!scholarship.getCourseKeywords().isEmpty()) {
            texts.add("Priority courses: " + String.join(", ", scholarship.getCourseKeywords()));
        }
        if (!scholarship.getSchoolKeywords().isEmpty()) {
            texts.add("Enrolled at " + String.join(" / ", scholarship.getSchoolKeywords()));
        }
        if (Boolean.TRUE.equals(scholarship.getRequiresPhilippineCitizenship())) {
            texts.add("Philippine citizen");
        }
        if (Boolean.TRUE.equals(scholarship.getFemaleOnly())) {
            texts.add("Female students only");
        }
        if (scholarship.getRequiredClassRank() != null) {
            texts.add("Academic rank: " + scholarship.getRequiredClassRank().getLabel());
        }
        if (Boolean.TRUE.equals(scholarship.getRequiresDepartmentEndorsement())) {
            texts.add("Department endorsement required");
        }
        return texts;
    }

    private static List<EducationLevel> levelsOf(Scholarship scholarship) {
        return scholarship.getLevels().stream()
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .toList();
    }
}
