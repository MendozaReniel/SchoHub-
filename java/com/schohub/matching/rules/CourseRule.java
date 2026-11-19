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
@Order(8)
public class CourseRule implements EligibilityRule {

    @Override
    public List<RequirementCheck> evaluate(Student student, Scholarship scholarship) {
        if (scholarship.getCourseKeywords() == null || scholarship.getCourseKeywords().isEmpty()) {
            return List.of();
        }
        String courses = scholarship.getCourseKeywords().stream()
                .sorted(Comparator.naturalOrder())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        String requirement = "Priority courses: " + courses;

        if (RuleSupport.isBlank(student.getProgram())) {
            return List.of(RequirementCheck.fail(requirement, "Set your program / course in your profile."));
        }
        if (RuleSupport.containsAny(student.getProgram(), scholarship.getCourseKeywords())) {
            return List.of(RequirementCheck.pass(requirement, student.getProgram() + " is a priority course."));
        }
        return List.of(RequirementCheck.fail(requirement,
                student.getProgram() + " is not one of the priority courses for this grant."));
    }
}
