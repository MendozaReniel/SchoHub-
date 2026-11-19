package com.schohub.matching.rules;

import com.schohub.matching.EligibilityRule;
import com.schohub.matching.RequirementCheck;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@Order(6)
public class IncomeRule implements EligibilityRule {

    private static final Locale PH = Locale.forLanguageTag("en-PH");

    @Override
    public List<RequirementCheck> evaluate(Student student, Scholarship scholarship) {
        List<RequirementCheck> checks = new ArrayList<>();

        BigDecimal ceiling = scholarship.getMaxAnnualIncome();
        if (ceiling != null) {
            String requirement = "Annual family income: at most " + peso(ceiling);
            if (student.getAnnualFamilyIncome() == null) {
                checks.add(RequirementCheck.fail(requirement,
                        "Add your household's annual family income."));
            } else if (student.getAnnualFamilyIncome().compareTo(ceiling) <= 0) {
                checks.add(RequirementCheck.pass(requirement,
                        "Your declared income of " + peso(student.getAnnualFamilyIncome()) + " is within the limit."));
            } else {
                checks.add(RequirementCheck.fail(requirement,
                        "Your declared income of " + peso(student.getAnnualFamilyIncome()) + " exceeds the limit."));
            }
        }

        if (Boolean.TRUE.equals(scholarship.getRequiresIndigency())) {
            String requirement = "Low-income / indigent household";
            if (student.getIndigencyStatus() == null) {
                checks.add(RequirementCheck.fail(requirement,
                        "Confirm your household's indigency status in your application details."));
            } else {
                checks.add(student.getIndigencyStatus()
                        ? RequirementCheck.pass(requirement, "You declared an indigent household.")
                        : RequirementCheck.fail(requirement, "This grant is for indigent households only."));
            }
        }

        if (Boolean.TRUE.equals(scholarship.getRequiresListahanan())) {
            String requirement = "Listahanan (DSWD) beneficiary household";
            if (student.getListahananBeneficiary() == null) {
                checks.add(RequirementCheck.fail(requirement,
                        "Confirm whether your household is in the Listahanan database."));
            } else {
                checks.add(student.getListahananBeneficiary()
                        ? RequirementCheck.pass(requirement, "Your household is a Listahanan beneficiary.")
                        : RequirementCheck.fail(requirement, "This grant is for Listahanan beneficiaries only."));
            }
        }

        return checks;
    }

    private static String peso(BigDecimal amount) {
        return NumberFormat.getCurrencyInstance(PH).format(amount);
    }
}
