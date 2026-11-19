package com.schohub.web;

import com.schohub.model.Student;
import com.schohub.service.ProfileService;
import com.schohub.web.dto.ScholarshipDtos.RenewalStatusResponse;
import com.schohub.web.dto.ScholarshipDtos.ScholarshipMatchResponse;
import com.schohub.web.dto.StudentDtos.DashboardResponse;
import com.schohub.web.dto.StudentDtos.PathSelectionRequest;
import com.schohub.web.dto.StudentDtos.ProfileStatus;
import com.schohub.web.dto.StudentDtos.ProfileUpdateRequest;
import com.schohub.web.dto.StudentDtos.StudentResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/me")
public class StudentController {

    private final ProfileService profileService;

    public StudentController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public StudentResponse me(@AuthenticationPrincipal Student student) {
        return profileService.get(student.getId());
    }

    @PutMapping("/profile")
    public StudentResponse updateProfile(@AuthenticationPrincipal Student student,
                                         @Valid @RequestBody ProfileUpdateRequest request) {
        return profileService.updateProfile(student.getId(), request);
    }

    @PutMapping("/path")
    public StudentResponse selectPath(@AuthenticationPrincipal Student student,
                                      @Valid @RequestBody PathSelectionRequest request) {
        return profileService.selectPath(student.getId(), request);
    }

    @GetMapping("/profile-status")
    public ProfileStatus profileStatus(@AuthenticationPrincipal Student student) {
        return profileService.status(student.getId());
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard(@AuthenticationPrincipal Student student,
                                       @RequestParam(defaultValue = "false") boolean includeIneligible) {
        return profileService.dashboard(student.getId(), includeIneligible);
    }

    @GetMapping("/matches")
    public List<ScholarshipMatchResponse> matches(@AuthenticationPrincipal Student student,
                                                  @RequestParam(defaultValue = "false") boolean includeIneligible) {
        return profileService.matches(student.getId(), includeIneligible);
    }

    @GetMapping("/renewal")
    public RenewalStatusResponse renewal(@AuthenticationPrincipal Student student) {
        return profileService.renewal(student.getId());
    }
}
