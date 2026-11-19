package com.schohub.matching.rules;

import com.schohub.matching.EligibilityRule;
import com.schohub.matching.RequirementCheck;
import com.schohub.model.EducationLevel;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(1)
public class EducationLevelRule implements EligibilityRule {

    @Override
    public List<RequirementCheck> evaluate(Student student, Scholarship scholarship) {
        if (scholarship.getLevels() == null || scholarship.getLevels().isEmpty()) {
            return List.of();
        }
        String openTo = scholarship.getLevels().stream()
                .map(EducationLevel::getLabel)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining(", "));
        String requirement = "Educational level: " + openTo;

        if (student.getEducationLevel() == null) {
            return List.of(RequirementCheck.fail(requirement, "Set your education level in your profile."));
        }
        if (scholarship.getLevels().contains(student.getEducationLevel())) {
            return List.of(RequirementCheck.pass(requirement,
                    "You are enrolled as a " + student.getEducationLevel().getLabel() + " student."));
        }
        return List.of(RequirementCheck.fail(requirement, "Only " + openTo + " students may apply."));
    }
}
