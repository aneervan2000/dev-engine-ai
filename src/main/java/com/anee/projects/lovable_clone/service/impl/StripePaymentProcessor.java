package com.anee.projects.lovable_clone.service.impl;

import com.anee.projects.lovable_clone.dto.subscription.CheckoutRequest;
import com.anee.projects.lovable_clone.dto.subscription.CheckoutResponse;
import com.anee.projects.lovable_clone.dto.subscription.PortalResponse;
import com.anee.projects.lovable_clone.entities.Plan;
import com.anee.projects.lovable_clone.entities.User;
import com.anee.projects.lovable_clone.enums.SubscriptionStatus;
import com.anee.projects.lovable_clone.error.ResourceNotFoundException;
import com.anee.projects.lovable_clone.repository.PlanRepository;
import com.anee.projects.lovable_clone.repository.UserRepository;
import com.anee.projects.lovable_clone.security.AuthUtil;
import com.anee.projects.lovable_clone.service.PaymentProcessor;
import com.anee.projects.lovable_clone.service.SubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentProcessor implements PaymentProcessor {

    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    @Value("${client.url}")
    private String frontendUrl;


    /**
     * As soon as the user clicks the button, the user will get redirect to another page where he will pay via Stripe.
     * User will give the planId and using that planId we will create a checkout session url and send it to the frontend and then frontend will redirect the user to that url.
     *
     * @param request the request containing the planId for which the user wants to subscribe
     * @return
     */
    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request) {

        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id:", request.planId().toString()));

        Long userId = authUtil.getCurrentUserId();
        User user = getUser(userId);


        var params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setPrice(plan.getStripePriceId()).setQuantity(1L).build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSubscriptionData(
                        new SessionCreateParams.SubscriptionData.Builder()
                                .setBillingMode(SessionCreateParams.SubscriptionData.BillingMode.builder()
                                        .setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE)
                                        .build())
                                .build()
                )
                .setSuccessUrl(frontendUrl + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/cancel.html")
                .putMetadata("user_id", userId.toString())
                .putMetadata("plan_id", plan.getId().toString());

        try {

            String stripeCustomerId = user.getStripeCustomerId();

            /*
             * If the user does not have a Stripe customer ID, we set the customer's email to create a new customer in Stripe during the checkout session creation.
             * If the user already has a Stripe customer ID, we use that to associate the checkout session with the existing customer
             * and in the checkout page email will be pre-filled and user can directly pay without entering email.
             * This allows for seamless integration with Stripe's customer management and
             * ensures that returning users are recognized without creating duplicate customer records in Stripe.
             *
             */
            if (stripeCustomerId == null || stripeCustomerId.isEmpty()) {
                params.setCustomerEmail(user.getUsername());
            } else {
                params.setCustomer(stripeCustomerId);
            }

            Session session = Session.create(params.build());  // calling Stripe API to create a checkout session
            return new CheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PortalResponse openCustomerPortal() {
        return null;
    }

    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {
        log.debug("Hndling stripe event: {}", type);

        switch (type) {
            case "checkout.session.completed" -> handleCheckoutSessionCompleted((Session) stripeObject, metadata); // one-time on checkout completed
            case "customer.subscription.updated" -> handleCustomerSubscriptionUpdated((Subscription) stripeObject); // when user cancels, or any updates in subscription
            case "customer.subscription.deleted" -> handleCustomerSubscriptionDeleted((Subscription) stripeObject); // when subscription ends, revoke the access to the service
            case "invoice.paid" -> handleInvoicePaid((Invoice) stripeObject); // when invoice is paid
            case "invoice.payment_failed" -> handleInvoicePaymentFailed((Invoice) stripeObject); // when invoice is not paid, mark as AST_DUE
            default -> log.debug("Ignoring the event: {}", type);
        }
    }

    /**
     * This method handles the <b>"checkout.session.completed"</b> event from Stripe. It extracts the user ID and plan ID from the session metadata,
     * and then activates the subscription for the user in the system.<br>
     * If the user does not have a Stripe customer ID, it sets it after successful checkout to ensure proper association with Stripe for future billing and subscription management.
     * @param session
     * @param metadata
     */
    private void handleCheckoutSessionCompleted(Session session,  Map<String, String> metadata) {

        if (session == null) {
            log.error("session object was null");
            return;
        }

        // Extract user ID and plan ID from the session metadata
        Long userId = Long.parseLong(metadata.get("user_id"));
        Long planId = Long.parseLong(metadata.get("plan_id"));

        // You can also extract subscription ID and customer ID if needed
        String subscriptionId = session.getSubscription();
        String customerId = session.getCustomer(); // stripe customer ID, not the same as user ID in your database

        // Update the user's subscription status in the database
        User user = getUser(userId);

        // If the user does not have a Stripe customer ID, set it now after successful checkout.
        // This ensures that the user is associated with the correct Stripe customer for future billing and subscription management.
        if (user.getStripeCustomerId() == null) {
            user.setStripeCustomerId(customerId);
            userRepository.save(user);
        }

        subscriptionService.activateSubscription(userId, planId, subscriptionId, customerId); // Activate the subscription for the user in your system
    }

    /**
     * This method handles the <b>"customer.subscription.updated"</b> event from Stripe.<br>
     * It updates the subscription status and details in the system based on the information received from Stripe.<br>
     * The method maps the Stripe subscription status to the application's SubscriptionStatus enum, extracts the subscription item to get plan details, and manages access based on the billing period.
     * It also resolves the plan ID using the Stripe price ID from the subscription item to ensure accurate updates in the system.
     *
     * @param subscription
     */
    private void handleCustomerSubscriptionUpdated(Subscription subscription) {

        if (subscription == null) {
            log.error("subscription object was null inside handleCustomerSubscriptionUpdated");
            return;
        }

        // Map the Stripe subscription status to your application's SubscriptionStatus enum
        SubscriptionStatus status = mapStripeStatusToEnum(subscription.getStatus());
        if (status == null) {
            log.warn("Unknown status '{}' for subscription: {}", subscription.getStatus(), subscription.getId());
            return;
        }

        // Extract the subscription item to get the plan details. Assuming there's only one item in the subscription for simplicity.
        // Get the start and end time of the current billing period to manage access accordingly.
        SubscriptionItem item = subscription.getItems().getData().get(0);
        Instant periodStart = toInstant(item.getCurrentPeriodStart());
        Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

        // Extracting the plan ID by the price ID from the subscription item.
        // This is necessary to identify which plan the subscription is associated with, especially if you have multiple plans.
        Long planId = resolvePlanId(item.getPrice());

        subscriptionService.updateSubscription(
                subscription.getId(), status, planId, periodStart, periodEnd, subscription.getCancelAtPeriodEnd(), planId); // Update the subscription status and details in your system
    }

    /**
     * This method handles the <b>"customer.subscription.deleted"</b> event from Stripe.<br>
     * It cancels the subscription in the system and revokes access to the service for the user.
     *
     * @param subscription
     */
    private void handleCustomerSubscriptionDeleted(Subscription subscription) {
        if (subscription == null) {
            log.error("subscription object was null inside handleCustomerSubscriptionDeleted");
            return;
        }

        subscriptionService.cancelSubscription(subscription.getId()); // Cancel the subscription in your system and revoke access to the service

     }

    /**
     * This method handles the <b>"invoice.paid"</b> event from Stripe.<br>
     * It renews the subscription in the system based on the invoice details received from Stripe.<br>
     * The method extracts the subscription ID from the invoice, retrieves the subscription details from Stripe, and then updates the subscription in the system with the new billing period information to ensure continuous access for the user.
     *
     * @param invoice
     */
     private void handleInvoicePaid(Invoice invoice) {
        String subId = extractSubscriptionId(invoice);
        if (subId == null)  return;

         try {
             Subscription subscription = Subscription.retrieve(subId); // sdk calling the Stripe server
             var item = subscription.getItems().getData().get(0);

             Instant periodStart = toInstant(item.getCurrentPeriodStart());
             Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

             subscriptionService.renewSubscription(subId, periodStart, periodEnd);
         } catch (StripeException e) {
             throw new RuntimeException(e);
         }
     }

    /**
     * This method handles the <b>"invoice.payment_failed"</b> event from Stripe.<br>
     * It marks the subscription as past due in the system when a payment fails for an invoice.
     * The method extracts the subscription ID from the invoice and then updates the subscription status in the system to reflect the payment failure, which can be used to restrict access to the service until the payment issue is resolved.
     *
     * @param invoice
     */
     private void handleInvoicePaymentFailed(Invoice invoice) {
         String subId = extractSubscriptionId(invoice);
         if (subId == null)  return;

         subscriptionService.markSubscriptionPastDue(subId);
     }

     /// UTILITY METHODS

    /**
     * This method retrieves a user from the database based on the provided user ID.
     * If the user is not found, it throws a ResourceNotFoundException with a message indicating that the user was not found.
     * @param userId
     * @return User object corresponding to the provided user ID
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id:", userId.toString()));
    }

    /**
     * This method maps the subscription status received from Stripe to the corresponding SubscriptionStatus enum used in the application.
     *
     * @param status
     * @return SubscriptionStatus enum value corresponding to the Stripe status, or null if the status is unmapped
     */
    private SubscriptionStatus mapStripeStatusToEnum(String status) {
        return switch (status) {
            case "active" -> SubscriptionStatus.ACTIVE;
            case "trialing" -> SubscriptionStatus.TRIALING;
            case "canceled" -> SubscriptionStatus.CANCELED;
            case "past_due", "unpaid", "paused", "incomplete_expired" -> SubscriptionStatus.PAST_DUE;
            case "incomplete" -> SubscriptionStatus.INCOMPLETE;
            default -> {
                log.warn("Unmapped Stripe status: {}", status);
                yield null;
            }
        };
    }

    /**
     * Takes a Long value and convert it into Epoch Instant
     *
     * @param epoch
     * @return Instant object representing the epoch time, or null if the input is null
     */
    private Instant toInstant(Long epoch) {
        return epoch != null ? Instant.ofEpochSecond(epoch) : null;
    }

    /**
     * This method resolves the plan ID in the application's database based on the Stripe price ID provided in the subscription item.
     * It queries the PlanRepository to find a plan that matches the given Stripe price ID and
     * returns the corresponding plan ID. If no matching plan is found, it throws a ResourceNotFoundException indicating that the plan was not found for the provided Stripe price ID.
     *
     * @param price
     * @return the plan ID corresponding to the Stripe price ID, or null if the price is null or does not have an ID
     */
    private Long resolvePlanId(Price price) {
        if (price == null || price.getId() == null) {
            return null;
        }
        return planRepository.findByStripePriceId(price.getId())
                .map(Plan::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found for Stripe price ID:", price.getId()));
    }

    /**
     * Extarctting the subscription ID from the invoice object.<br>
     * This is necessary to link the invoice to the correct subscription in the system, especially when handling events related to invoicing and payments.
     *
     * @param invoice
     * @return
     */
    private String extractSubscriptionId(Invoice invoice) {

        // The subscription ID is not directly available in the invoice object,
        // but it can be accessed through the parent object of the invoice, which is typically a subscription.
        var parent = invoice.getParent();
        if (parent == null) return null;

        var subDetails = parent.getSubscriptionDetails();
        if (subDetails == null) return null;

        return subDetails.getSubscription();
    }

}
