package com.schohub.matching.rules;

import com.schohub.matching.EligibilityRule;
import com.schohub.matching.RequirementCheck;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Department endorsement cannot be verified from a student profile, so it is
 * reported as an advisory step instead of excluding the student.
 */
@Component
@Order(13)
public class EndorsementRule implements EligibilityRule {

    @Override
    public List<RequirementCheck> evaluate(Student student, Scholarship scholarship) {
        if (!Boolean.TRUE.equals(scholarship.getRequiresDepartmentEndorsement())) {
            return List.of();
        }
        return List.of(RequirementCheck.advisory("Department endorsement required",
                "Ask your department to endorse your application before you submit it."));
    }
}
