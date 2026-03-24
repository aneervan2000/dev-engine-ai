package com.anee.projects.lovable_clone.service.impl;

import com.anee.projects.lovable_clone.dto.subscription.SubscriptionResponse;
import com.anee.projects.lovable_clone.entities.Plan;
import com.anee.projects.lovable_clone.entities.Subscription;
import com.anee.projects.lovable_clone.entities.User;
import com.anee.projects.lovable_clone.enums.SubscriptionStatus;
import com.anee.projects.lovable_clone.error.ResourceNotFoundException;
import com.anee.projects.lovable_clone.mapper.SubscriptionMapper;
import com.anee.projects.lovable_clone.repository.PlanRepository;
import com.anee.projects.lovable_clone.repository.SubscriptionRepository;
import com.anee.projects.lovable_clone.repository.UserRepository;
import com.anee.projects.lovable_clone.security.AuthUtil;
import com.anee.projects.lovable_clone.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    /**
     * Retrieves the current active subscription for the authenticated user.
     * <p>
     * The method fetches the user ID from the authentication context, then queries
     * the repository for a subscription with status ACTIVE, PAST_DUE, or TRIALING.
     * If no such subscription exists, an empty Subscription is returned.
     * The result is mapped to a SubscriptionResponse DTO.
     *
     * @return the current user's active subscription as a SubscriptionResponse,
     *         or a response with null fields if no active subscription exists
     */
    @Override
    public SubscriptionResponse getCurrentSubscription() {
        // Get the current user ID from the authentication context
        Long userId = authUtil.getCurrentUserId();

        // Find the user's current active subscription (ACTIVE, PAST_DUE, or TRIALING).
        // Returns an empty Subscription if none found.
        var currentSubscription = subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(
                SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE, SubscriptionStatus.TRIALING
        )).orElse(new Subscription());

        // Map the Subscription entity to a SubscriptionResponse DTO and return it. If there is no active subscription, the response will have null fields.
        return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    /**
     * Activates a subscription for a user with the given details.
     * <p>
     * This method checks if a subscription with the provided Stripe subscription ID already exists.
     * If it does, the method returns without performing any action. Otherwise, it retrieves the user
     * and plan details, creates a new subscription with an initial status of INCOMPLETE, and saves it
     * to the repository. This will happen after the checkout is done.
     *
     * @param userId           the ID of the user for whom the subscription is being activated
     * @param planId           the ID of the plan associated with the subscription
     * @param subscriptionId   the Stripe subscription ID
     * @param customerId       the Stripe customer ID (currently unused in this method)
     */
    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {

        // Check if a subscription with the given Stripe subscription ID already exists
        boolean exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
        if (exists) return;

        User user = getUser(userId);
        Plan plan = getPlan(planId);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscriptionId(subscriptionId)
                .status(SubscriptionStatus.INCOMPLETE) // Initial status, will be updated to ACTIVE once payment is confirmed, after the checkout is done
                .build();

        subscriptionRepository.save(subscription);
    }

    @Override
    public void updateSubscription(String subscriptionId, SubscriptionStatus status, Long planId, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId1) {

    }

    @Override
    public void cancelSubscription(String subscriptionId) {

    }

    /**
     * Updates/Renews the subscription details for a given Stripe subscription ID.
     * <p>
     * This method updates the subscription's status, associated plan, period start and end times,
     * and whether the subscription is set to cancel at the end of the period. The plan ID is updated
     * to the new plan if provided. This will get executed when the invoice is paid, and the subscription is renewed for the next period.
     * If the subscription was previously in a PAST_DUE status, it will be updated to ACTIVE upon renewal.
     *
     * @param gatewaySubscriptionId      the Stripe subscription ID to identify the subscription
     * @param periodStart         the start time of the subscription's current period
     * @param periodEnd           the end time of the subscription's current period
     */
    @Override
    public void renewSubscription(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
        // Extracting the subscription using the gateway subscription ID (e.g., Stripe subscription ID)
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        // Update the subscription's current period start and end times.
        // If periodStart is null, use the existing currentPeriodEnd as the new start; otherwise, use the provided periodStart.
        // Set the current period end to the provided periodEnd.
        Instant newStart = periodStart != null ? periodStart : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE || subscription.getStatus() == SubscriptionStatus.INCOMPLETE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }

        subscriptionRepository.save(subscription);
    }

    @Override
    public void markSubscriptionPastDue(String gatewaySubscriptionId) {

    }

    /// UTILITY METHODS
    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId).orElseThrow(() -> new RuntimeException("Plan not found with id: " + planId));
    }

    private Subscription getSubscription(String gatewaySubscriptionId) {
        return subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with Stripe subscription ID: ", gatewaySubscriptionId));
    }
}
