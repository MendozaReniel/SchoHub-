package com.schohub.matching.rules;

import com.schohub.matching.EligibilityRule;
import com.schohub.matching.RequirementCheck;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(12)
public class ClassRankRule implements EligibilityRule {

    @Override
    public List<RequirementCheck> evaluate(Student student, Scholarship scholarship) {
        if (scholarship.getRequiredClassRank() == null) {
            return List.of();
        }
        String requirement = "Academic rank: " + scholarship.getRequiredClassRank().getLabel();
        if (student.getClassRank() == null) {
            return List.of(RequirementCheck.fail(requirement, "Select your graduating academic rank."));
        }
        return List.of(student.getClassRank() == scholarship.getRequiredClassRank()
                ? RequirementCheck.pass(requirement, "Your rank matches this grant.")
                : RequirementCheck.fail(requirement, "This grant is reserved for " + scholarship.getRequiredClassRank().getLabel() + "."));
    }
}
