package com.anee.projects.lovable_clone.controllers;

import com.anee.projects.lovable_clone.dto.subscription.PlanLimitsResponse;
import com.anee.projects.lovable_clone.dto.subscription.UsageTodayResponse;
import com.anee.projects.lovable_clone.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usage")
public class UsageController {

    private final UsageService usageService;

    @GetMapping("/today")
    public ResponseEntity<UsageTodayResponse> getTodayUsage() {
        Long userId = 1L; // This should be replaced with actual user ID retrieval logic
        return ResponseEntity.ok(usageService.getTodayUsage(userId));
    }

    @GetMapping("/limits")
    public ResponseEntity<PlanLimitsResponse> getPlanLimits() {
        Long userId = 1L; // This should be replaced with actual user ID retrieval logic
        return ResponseEntity.ok(usageService.getCurrentSubscriptionLimitsOfUser(userId));
    }

}
