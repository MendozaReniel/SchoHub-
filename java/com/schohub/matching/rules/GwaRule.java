package com.schohub.matching.rules;

import com.schohub.matching.EligibilityRule;
import com.schohub.matching.RequirementCheck;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import com.schohub.service.GwaConverter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class GwaRule implements EligibilityRule {

    @Override
    public List<RequirementCheck> evaluate(Student student, Scholarship scholarship) {
        Double threshold = scholarship.gwaFor(student.getEducationLevel());
        if (threshold == null) {
            return List.of();
        }
        String requirement = "Minimum GWA: " + GwaConverter.describe(threshold);
        Double actual = student.getGwaPercent();

        if (actual == null) {
            return List.of(RequirementCheck.fail(requirement,
                    "Add your previous semester GWA to your profile."));
        }
        if (actual + 1e-9 >= threshold) {
            return List.of(RequirementCheck.pass(requirement,
                    "Your GWA of " + GwaConverter.percentText(actual) + "% meets the cut-off."));
        }
        return List.of(RequirementCheck.fail(requirement,
                "Your GWA of " + GwaConverter.percentText(actual) + "% is below the cut-off."));
    }
}
