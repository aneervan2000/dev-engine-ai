package com.anee.projects.lovable_clone.controllers;

import com.anee.projects.lovable_clone.dto.subscription.*;
import com.anee.projects.lovable_clone.service.PlanService;
import com.anee.projects.lovable_clone.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BillingController {

    private final SubscriptionService subscriptionService;
    private final PlanService planService;

    @GetMapping("/api/plans")
    public ResponseEntity<List<PlanResponse>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllActivePlans()); // Placeholder
    }

    @GetMapping("/api/me/subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription() {
        Long userId = 1L; // Placeholder for authenticated user ID
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription(userId)); // Placeholder
    }

    @PostMapping("/api/stripe/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutSession(@RequestBody CheckoutRequest request) {
        Long userId = 1L;
        return ResponseEntity.ok(subscriptionService.createCheckoutSessionUrl(request, userId)); // Placeholder
    }

    @PostMapping("/api/stripe/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal() {
        Long userId = 1L;
        return ResponseEntity.ok(subscriptionService.openCustomerPortal(userId)); // Placeholder
    }

}
