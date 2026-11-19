package com.schohub.matching.rules;

import com.schohub.matching.EligibilityRule;
import com.schohub.matching.RequirementCheck;
import com.schohub.matching.RuleSupport;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@Order(9)
public class SchoolRule implements EligibilityRule {

    @Override
    public List<RequirementCheck> evaluate(Student student, Scholarship scholarship) {
        if (scholarship.getSchoolKeywords() == null || scholarship.getSchoolKeywords().isEmpty()) {
            return List.of();
        }
        String schools = scholarship.getSchoolKeywords().stream()
                .sorted(Comparator.naturalOrder())
                .reduce((a, b) -> a + " / " + b)
                .orElse("");
        String requirement = "Enrolled at " + schools;

        if (RuleSupport.isBlank(student.getSchool())) {
            return List.of(RequirementCheck.fail(requirement, "Set your school in your profile."));
        }
        if (RuleSupport.containsAny(student.getSchool(), scholarship.getSchoolKeywords())) {
            return List.of(RequirementCheck.pass(requirement, "You are enrolled at " + student.getSchool() + "."));
        }
        return List.of(RequirementCheck.fail(requirement,
                "This is an institutional grant for " + schools + " students."));
    }
}
