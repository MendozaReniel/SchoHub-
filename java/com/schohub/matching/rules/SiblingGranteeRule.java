package com.schohub.matching.rules;

import com.schohub.matching.EligibilityRule;
import com.schohub.matching.RequirementCheck;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(5)
public class SiblingGranteeRule implements EligibilityRule {

    @Override
    public List<RequirementCheck> evaluate(Student student, Scholarship scholarship) {
        if (!Boolean.TRUE.equals(scholarship.getRequiresNoSiblingGrantee())) {
            return List.of();
        }
        String requirement = "No sibling may already be a grantee of the same program";

        if (student.getSiblingEbdGrantee() == null) {
            return List.of(RequirementCheck.fail(requirement,
                    "Answer the sibling grantee question in your application details."));
        }
        return List.of(student.getSiblingEbdGrantee()
                ? RequirementCheck.fail(requirement, "A sibling is already a grantee of this program.")
                : RequirementCheck.pass(requirement, "No sibling is currently a grantee."));
    }
}
