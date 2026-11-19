package com.schohub.web;

import com.schohub.model.EducationLevel;
import com.schohub.model.ProviderType;
import com.schohub.model.Scholarship;
import com.schohub.repository.ScholarshipRepository;
import com.schohub.service.ScholarshipMapper;
import com.schohub.web.dto.ScholarshipDtos.ScholarshipDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/scholarships")
public class ScholarshipController {

    private final ScholarshipRepository scholarships;
    private final ScholarshipMapper mapper;

    public ScholarshipController(ScholarshipRepository scholarships, ScholarshipMapper mapper) {
        this.scholarships = scholarships;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ScholarshipDetail> list(@RequestParam(required = false) String q,
                                        @RequestParam(required = false) ProviderType providerType,
                                        @RequestParam(required = false) EducationLevel level) {
        String needle = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);

        return scholarships.findAllByOrderByNameAsc().stream()
                .filter(scholarship -> providerType == null || scholarship.getProviderType() == providerType)
                .filter(scholarship -> level == null || scholarship.getLevels().contains(level))
                .filter(scholarship -> needle.isEmpty() || matches(scholarship, needle))
                .map(mapper::toDetail)
                .toList();
    }

    @GetMapping("/{code}")
    public ScholarshipDetail byCode(@PathVariable String code) {
        Scholarship scholarship = scholarships.findByCodeIgnoreCase(code)
                .orElseThrow(() -> ApiException.notFound("No scholarship found with code " + code + "."));
        return mapper.toDetail(scholarship);
    }

    private static boolean matches(Scholarship scholarship, String needle) {
        return scholarship.getName().toLowerCase(Locale.ROOT).contains(needle)
                || scholarship.getProvider().toLowerCase(Locale.ROOT).contains(needle)
                || scholarship.getCode().toLowerCase(Locale.ROOT).contains(needle);
    }
}
