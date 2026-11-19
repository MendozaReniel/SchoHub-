package com.schohub.matching.rules;

import com.schohub.matching.EligibilityRule;
import com.schohub.matching.RequirementCheck;
import com.schohub.model.Scholarship;
import com.schohub.model.Sex;
import com.schohub.model.Student;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(11)
public class GenderRule implements EligibilityRule {

    @Override
    public List<RequirementCheck> evaluate(Student student, Scholarship scholarship) {
        if (!Boolean.TRUE.equals(scholarship.getFemaleOnly())) {
            return List.of();
        }
        String requirement = "Open to female students";
        if (student.getSex() == null) {
            return List.of(RequirementCheck.fail(requirement, "Add your sex to your profile."));
        }
        return List.of(student.getSex() == Sex.FEMALE
                ? RequirementCheck.pass(requirement, "You meet the grant's beneficiary requirement.")
                : RequirementCheck.fail(requirement, "This grant is for female students."));
    }
}
