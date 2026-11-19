package com.schohub.matching.rules;

import com.schohub.matching.EligibilityRule;
import com.schohub.matching.RequirementCheck;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(4)
public class ParentsVoterRule implements EligibilityRule {

    @Override
    public List<RequirementCheck> evaluate(Student student, Scholarship scholarship) {
        if (!Boolean.TRUE.equals(scholarship.getRequiresRegisteredVoterParents())) {
            return List.of();
        }
        String locality = scholarship.getRequiredCity() != null ? scholarship.getRequiredCity() : "the program's locality";
        String requirement = "Parents must be registered voters in " + locality;

        if (student.getParentsRegisteredVoter() == null) {
            return List.of(RequirementCheck.fail(requirement,
                    "Answer the voter registration question in your application details."));
        }
        return List.of(student.getParentsRegisteredVoter()
                ? RequirementCheck.pass(requirement, "Your parents are registered voters there.")
                : RequirementCheck.fail(requirement, "Your parents are not registered voters there."));
    }
}
