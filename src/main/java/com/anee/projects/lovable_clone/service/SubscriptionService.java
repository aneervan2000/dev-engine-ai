package com.anee.projects.lovable_clone.service;

import com.anee.projects.lovable_clone.dto.subscription.SubscriptionResponse;
import com.anee.projects.lovable_clone.enums.SubscriptionStatus;

import java.time.Instant;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription();

    void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId);

    void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Long planId, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId1);

    void cancelSubscription(String gatewaySubscriptionId);

    void renewSubscription(String subId, Instant periodStart, Instant periodEnd);

    void markSubscriptionPastDue(String subId);

    boolean canCreateNewProject();
}
