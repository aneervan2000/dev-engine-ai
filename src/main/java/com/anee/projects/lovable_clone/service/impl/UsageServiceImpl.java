package com.anee.projects.lovable_clone.service.impl;

import com.anee.projects.lovable_clone.dto.subscription.PlanLimitsResponse;
import com.anee.projects.lovable_clone.dto.subscription.UsageTodayResponse;
import com.anee.projects.lovable_clone.service.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImpl implements UsageService {
    @Override
    public UsageTodayResponse getTodayUsage(Long userId) {
        return null;
    }

    @Override
    public PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId) {
        return null;
    }
}
