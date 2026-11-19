package com.schohub.web.dto;

import com.schohub.model.EducationLevel;
import com.schohub.model.ProviderType;

import java.util.List;

public final class ScholarshipDtos {

    private ScholarshipDtos() {
    }

    public record ScholarshipSummary(
            String code,
            String name,
            String provider,
            ProviderType providerType,
            String benefit,
            List<EducationLevel> levels,
            boolean renewalEnabled) {
    }

    public record ScholarshipDetail(
            String code,
            String name,
            String provider,
            ProviderType providerType,
            String description,
            String benefit,
            List<EducationLevel> levels,
            List<String> gwaRequirements,
            List<String> residencyRequirements,
            List<String> otherRequirements,
            List<String> schoolKeywords,
            List<String> courseKeywords,
            List<String> coreDocuments,
            List<String> specificDocuments,
            List<String> renewalDocuments,
            String applicationUrl,
            String notes,
            boolean renewalEnabled,
            Integer minUnitsForRenewal,
            Double retentionGwaPercent,
            Double minGwaPercent) {
    }

    public record RequirementCheckResponse(String requirement, boolean met, String detail, boolean advisory) {
    }

    public record ScholarshipMatchResponse(
            ScholarshipSummary scholarship,
            boolean eligible,
            List<RequirementCheckResponse> checks,
            List<String> coreDocuments,
            List<String> specificDocuments,
            List<String> renewalDocuments,
            String applicationUrl,
            String summary) {
    }

    public record ChecklistItem(String name, boolean onHand) {
    }

    public record RenewalStatusResponse(
            String scholarshipCode,
            String scholarshipName,
            boolean eligible,
            Integer enrolledUnits,
            Integer requiredUnits,
            Double previousTermGwaPercent,
            Double retentionGwaPercent,
            List<RequirementCheckResponse> checks,
            List<ChecklistItem> checklist,
            String applicationUrl,
            String message) {
    }
}
