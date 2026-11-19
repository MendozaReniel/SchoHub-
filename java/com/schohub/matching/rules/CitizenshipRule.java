package com.schohub.matching.rules;

import com.schohub.matching.EligibilityRule;
import com.schohub.matching.RequirementCheck;
import com.schohub.matching.RuleSupport;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(10)
public class CitizenshipRule implements EligibilityRule {

    @Override
    public List<RequirementCheck> evaluate(Student student, Scholarship scholarship) {
        if (!Boolean.TRUE.equals(scholarship.getRequiresPhilippineCitizenship())) {
            return List.of();
        }
        String requirement = "Philippine citizenship";
        if (RuleSupport.isBlank(student.getCitizenship())) {
            return List.of(RequirementCheck.fail(requirement, "Add your citizenship to your profile."));
        }
        String citizenship = RuleSupport.normalize(student.getCitizenship());
        boolean filipino = citizenship.contains("filipino") || citizenship.contains("philippin");
        return List.of(filipino
                ? RequirementCheck.pass(requirement, "You are a Philippine citizen.")
                : RequirementCheck.fail(requirement, "This grant is limited to Philippine citizens."));
    }
}
