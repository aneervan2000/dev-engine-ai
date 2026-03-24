package com.anee.projects.lovable_clone.service.impl;

import com.anee.projects.lovable_clone.dto.subscription.SubscriptionResponse;
import com.anee.projects.lovable_clone.entities.Plan;
import com.anee.projects.lovable_clone.entities.Subscription;
import com.anee.projects.lovable_clone.entities.User;
import com.anee.projects.lovable_clone.enums.SubscriptionStatus;
import com.anee.projects.lovable_clone.error.ResourceNotFoundException;
import com.anee.projects.lovable_clone.mapper.SubscriptionMapper;
import com.anee.projects.lovable_clone.repository.PlanRepository;
import com.anee.projects.lovable_clone.repository.ProjectMemberRepository;
import com.anee.projects.lovable_clone.repository.SubscriptionRepository;
import com.anee.projects.lovable_clone.repository.UserRepository;
import com.anee.projects.lovable_clone.security.AuthUtil;
import com.anee.projects.lovable_clone.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final ProjectMemberRepository projectMemberRepository;

    private final int FREE_TIER_PROJECT_ALLOWED = 1;

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
     * to the repository. <b>This will happen after the checkout is done.</b>
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

    /**
     * Updates the subscription details based on the provided Stripe subscription ID and new values.
     * <p>
     * This method retrieves the subscription using the Stripe subscription ID, then updates its status,
     * associated plan, period start and end times, and whether it is set to cancel at the end of the period. The plan ID is updated to the new plan if provided. The subscription is only saved to the repository if any of the fields have been updated to avoid unnecessary database writes.
     * <b>This will get executed when the invoice is paid, and the subscription is renewed for the next period, or when the subscription is updated from the customer portal.</b>
     *
     * @param gatewaySubscriptionId
     * @param status
     * @param planId
     * @param periodStart
     * @param periodEnd
     * @param cancelAtPeriodEnd
     * @param planId1
     */
    @Override
    @Transactional
    public void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Long planId, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId1) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        boolean hasSubscriptionUpdated = false;

        // Updating the fields only if the new values are different from the existing ones to avoid unnecessary updates and database writes.
        if (status != null && status != subscription.getStatus()) {
            subscription.setStatus(status);
            hasSubscriptionUpdated = true;
        }

        if (periodStart != null && !periodStart.equals(subscription.getCurrentPeriodStart())) {
            subscription.setCurrentPeriodStart(periodStart);
            hasSubscriptionUpdated = true;
        }

        if (periodEnd != null && !periodEnd.equals(subscription.getCurrentPeriodEnd())) {
            subscription.setCurrentPeriodEnd(periodEnd);
            hasSubscriptionUpdated = true;
        }

        if (cancelAtPeriodEnd != null && cancelAtPeriodEnd != subscription.getCancelAtPeriodEnd()) {
            subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
            hasSubscriptionUpdated = true;
        }

        if (planId != null && !planId.equals(subscription.getPlan().getId())) {
            Plan newPlan = getPlan(planId);
            subscription.setPlan(newPlan);
            hasSubscriptionUpdated = true;
        }

        if (hasSubscriptionUpdated) {
            log.debug("Subscription has been updated: {}", gatewaySubscriptionId);
            subscriptionRepository.save(subscription);
        }

    }

    /**
     * Cancels a subscription based on the provided Stripe subscription ID.
     * <p>
     *     This method retrieves the subscription using the Stripe subscription ID, updates its status to CANCELED, and saves the updated subscription to the repository.
     *     Additionally, it can trigger a notification to the user via email to inform them about
     *     the cancellation. <b>This will get executed when the user cancels the subscription from the customer portal, or when the subscription is set to cancel at the end of the period, and the period ends.</b>
     *     <br>
     *     We are not checking the current status of the subscription before canceling it, because this method will only get triggered when the user has active subscription which we will be checking everytime before canceling the subscription.
     *
     * @param gatewaySubscriptionId
     */
    @Override
    public void cancelSubscription(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);
        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscriptionRepository.save(subscription);


        // Notify user via email...
    }

    /**
     * Updates/Renews the subscription details for a given Stripe subscription ID.
     * <p>
     * This method updates the subscription's status, associated plan, period start and end times,
     * and whether the subscription is set to cancel at the end of the period. The plan ID is updated
     * to the new plan if provided. <b>This will get executed when the invoice is paid, and the subscription is renewed for the next period.</b>
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

    /**
     * Marks a subscription as past due based on the provided Stripe subscription ID.
     * <p>
     *     This method checks the current status of the subscription. If it is already marked as PAST_DUE, it logs a debug message and returns without making any changes.
     *     If the subscription is not already past due, it updates the status to PAST_DUE and saves the updated subscription to the repository. Additionally, it can trigger a notification to the
     *     user via email to inform them about the past due status.
     *     <b>This will get executed when the payment fails, or the invoice is not paid, and the subscription is in incomplete or active status.</b>
     *
     * @param gatewaySubscriptionId
     */
    @Override
    public void markSubscriptionPastDue(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        // If the subscription is already past due, we can skip updating it again.
        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE) {
            log.debug("Subscription is already past due, {}", gatewaySubscriptionId);
            return;
        }

        // Update the subscription status to PAST_DUE if the payment fails or invoice is not paid.
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);

        // Notify user via email...
    }

    /**
     * Checks if the authenticated user can create a new project based on their current subscription plan and the number of projects they already own.
     * <p>
     *     The method retrieves the user's current subscription and counts the number of projects they own. If the user does not have an active subscription plan, they are allowed to create a new project only if they own fewer than the allowed number of projects for the free tier. If the user has an active subscription plan, they can create a new project as long as they own fewer projects than the maximum allowed by their subscription plan.
     *
     * @return true if the user can create a new project, false otherwise
     */
    @Override
    public boolean canCreateNewProject() {
        SubscriptionResponse currentSubscription = getCurrentSubscription();

        int countOfOwnedProjects = projectMemberRepository.countProjectOwnedByUser(authUtil.getCurrentUserId());

        if (currentSubscription.plan() == null) {
            return countOfOwnedProjects < FREE_TIER_PROJECT_ALLOWED;
        }

        return countOfOwnedProjects < currentSubscription.plan().maxProjects();
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
