package com.anee.projects.lovable_clone.service.impl;

import com.anee.projects.lovable_clone.dto.subscription.CheckoutRequest;
import com.anee.projects.lovable_clone.dto.subscription.CheckoutResponse;
import com.anee.projects.lovable_clone.dto.subscription.PortalResponse;
import com.anee.projects.lovable_clone.entities.Plan;
import com.anee.projects.lovable_clone.entities.User;
import com.anee.projects.lovable_clone.error.ResourceNotFoundException;
import com.anee.projects.lovable_clone.repository.PlanRepository;
import com.anee.projects.lovable_clone.repository.UserRepository;
import com.anee.projects.lovable_clone.security.AuthUtil;
import com.anee.projects.lovable_clone.service.PaymentProcessor;
import com.stripe.exception.StripeException;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentProcessor implements PaymentProcessor {

    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id:", userId.toString()));


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
    public PortalResponse openCustomerPortal(Long userId) {
        return null;
    }

    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {
        log.info("type");
    }
}
