package com.schohub.matching.rules;

import com.schohub.matching.EligibilityRule;
import com.schohub.matching.RequirementCheck;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import com.schohub.service.GwaConverter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(7)
public class AcademicStandingRule implements EligibilityRule {

    @Override
    public List<RequirementCheck> evaluate(Student student, Scholarship scholarship) {
        List<RequirementCheck> checks = new ArrayList<>();

        if (Boolean.TRUE.equals(scholarship.getRequiresNoFailingGrades())) {
            String requirement = "No failing, dropped, or incomplete grades";
            if (student.getHasFailingDroppedIncompleteGrades() == null) {
                checks.add(RequirementCheck.fail(requirement,
                        "Confirm your academic standing in your application details."));
            } else {
                checks.add(student.getHasFailingDroppedIncompleteGrades()
                        ? RequirementCheck.fail(requirement, "You reported a failing, dropped, or incomplete grade.")
                        : RequirementCheck.pass(requirement, "You have no failing, dropped, or incomplete grades."));
            }
        }

        Double floor = scholarship.getMinLowestSubjectGrade();
        if (floor != null) {
            String requirement = "No single grade below " + GwaConverter.percentText(floor) + "%";
            if (student.getLowestSubjectGrade() == null) {
                checks.add(RequirementCheck.fail(requirement,
                        "Add your lowest individual subject grade."));
            } else if (student.getLowestSubjectGrade() >= floor) {
                checks.add(RequirementCheck.pass(requirement,
                        "Your lowest grade is " + GwaConverter.percentText(student.getLowestSubjectGrade()) + "%."));
            } else {
                checks.add(RequirementCheck.fail(requirement,
                        "Your lowest grade is " + GwaConverter.percentText(student.getLowestSubjectGrade()) + "%."));
            }
        }

        return checks;
    }
}
