package com.schohub.service;

import com.schohub.matching.EligibilityRule;
import com.schohub.matching.RequirementCheck;
import com.schohub.model.Scholarship;
import com.schohub.model.Student;
import com.schohub.repository.ScholarshipRepository;
import com.schohub.web.dto.ScholarshipDtos.ScholarshipMatchResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Runs every registered {@link EligibilityRule} against every scholarship and
 * keeps the ones the student is qualified for (PHASE 3 of the flowchart).
 */
@Service
public class MatchingService {

    private final ScholarshipRepository scholarships;
    private final ScholarshipMapper mapper;
    private final List<EligibilityRule> rules;

    public MatchingService(ScholarshipRepository scholarships, ScholarshipMapper mapper, List<EligibilityRule> rules) {
        this.scholarships = scholarships;
        this.mapper = mapper;
        this.rules = rules;
    }

    public List<ScholarshipMatchResponse> matchesFor(Student student, boolean includeIneligible) {
        return scholarships.findAllByOrderByNameAsc().stream()
                .map(scholarship -> evaluate(student, scholarship))
                .filter(match -> includeIneligible || match.eligible())
                .sorted(Comparator
                        .comparing((ScholarshipMatchResponse match) -> !match.eligible())
                        .thenComparing(match -> match.scholarship().name()))
                .toList();
    }

    public ScholarshipMatchResponse evaluate(Student student, Scholarship scholarship) {
        List<RequirementCheck> checks = rules.stream()
                .flatMap(rule -> rule.evaluate(student, scholarship).stream())
                .toList();
        return mapper.toMatch(scholarship, checks);
    }
}
