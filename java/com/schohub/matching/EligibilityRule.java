package com.schohub.matching;

import com.schohub.model.Scholarship;
import com.schohub.model.Student;

import java.util.List;

/**
 * One criterion of the multi-criteria matching engine. Each implementation
 * inspects only the part of the student profile it cares about and returns an
 * empty list when the scholarship does not define that criterion.
 */
public interface EligibilityRule {

    List<RequirementCheck> evaluate(Student student, Scholarship scholarship);
}
