package com.schohub.matching.rules;

import com.schohub.matching.EligibilityRule;
import com.schohub.matching.RequirementCheck;
import com.schohub.matching.RuleSupport;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(3)
public class ResidencyRule implements EligibilityRule {

    @Override
    public List<RequirementCheck> evaluate(Student student, Scholarship scholarship) {
        List<RequirementCheck> checks = new ArrayList<>();

        if (scholarship.getRequiredCity() != null) {
            String requirement = "Residency: " + scholarship.getRequiredCity();
            if (RuleSupport.isBlank(student.getCity())) {
                checks.add(RequirementCheck.fail(requirement, "Set your city / municipality in your profile."));
            } else if (RuleSupport.sameText(student.getCity(), scholarship.getRequiredCity())) {
                checks.add(RequirementCheck.pass(requirement, "You reside in " + student.getCity() + "."));
            } else {
                checks.add(RequirementCheck.fail(requirement,
                        "Your recorded residence is " + student.getCity() + "."));
            }
        }

        if (scholarship.getRequiredProvince() != null) {
            String requirement = "Residency: " + scholarship.getRequiredProvince();
            if (RuleSupport.isBlank(student.getProvince())) {
                checks.add(RequirementCheck.fail(requirement, "Set your province in your profile."));
            } else if (RuleSupport.sameProvince(student.getProvince(), scholarship.getRequiredProvince())) {
                checks.add(RequirementCheck.pass(requirement, "You reside in " + student.getProvince() + "."));
            } else {
                checks.add(RequirementCheck.fail(requirement,
                        "Your recorded province is " + student.getProvince() + "."));
            }
        }

        if (scholarship.getRequiredBarangay() != null) {
            String requirement = "Residency: Barangay " + scholarship.getRequiredBarangay();
            if (RuleSupport.isBlank(student.getBarangay())) {
                checks.add(RequirementCheck.fail(requirement, "Add your barangay in your application details."));
            } else if (RuleSupport.sameText(student.getBarangay(), scholarship.getRequiredBarangay())) {
                checks.add(RequirementCheck.pass(requirement, "You reside in Barangay " + student.getBarangay() + "."));
            } else {
                checks.add(RequirementCheck.fail(requirement,
                        "Your recorded barangay is " + student.getBarangay() + "."));
            }
        }

        if (scholarship.getMinYearsResidency() != null) {
            String requirement = "Minimum years of residency: " + scholarship.getMinYearsResidency();
            if (student.getYearsOfResidency() == null) {
                checks.add(RequirementCheck.fail(requirement,
                        "Add how many years you have lived in your city / municipality."));
            } else if (student.getYearsOfResidency() >= scholarship.getMinYearsResidency()) {
                checks.add(RequirementCheck.pass(requirement,
                        "You have been a resident for " + student.getYearsOfResidency() + " years."));
            } else {
                checks.add(RequirementCheck.fail(requirement,
                        "You have been a resident for " + student.getYearsOfResidency() + " years."));
            }
        }

        return checks;
    }
}
