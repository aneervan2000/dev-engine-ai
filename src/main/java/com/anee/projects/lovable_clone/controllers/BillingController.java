package com.anee.projects.lovable_clone.controllers;

import com.anee.projects.lovable_clone.dto.subscription.*;
import com.anee.projects.lovable_clone.service.PaymentProcessor;
import com.anee.projects.lovable_clone.service.PlanService;
import com.anee.projects.lovable_clone.service.SubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BillingController {

    private final SubscriptionService subscriptionService;
    private final PlanService planService;
    private final PaymentProcessor paymentProcessor;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;


    @GetMapping("/api/plans")
    public ResponseEntity<List<PlanResponse>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllActivePlans()); // Placeholder
    }

    @GetMapping("/api/me/subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription() {
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription()); // Placeholder
    }

    @PostMapping("/api/payments/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutResponse(@RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(paymentProcessor.createCheckoutSessionUrl(request)); // Placeholder
    }

    @PostMapping("/api/payments/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal() {
        return ResponseEntity.ok(paymentProcessor.openCustomerPortal()); // Placeholder
    }

    /**
     * This endpoint handles incoming Stripe webhook events related to payments.
     * It verifies the webhook signature, deserializes the event data, and processes the event based on its type.
     *
     * @param payload
     * @param sigHeader
     * @return
     */
    @PostMapping("/webhooks/payment")
    public ResponseEntity<String> handlePaymentWebhooks(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            // Verify the webhook signature and construct the event object.
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            // Deserialize the event data object safely
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;

            // if the version of Stripe SDK is equal to the version of event that we are getting from stripe, then we can safely deserialize it using getObject() method.
            // Otherwise, we need to fallback to unsafe deserialization.
            if (deserializer.getObject().isPresent()) {
                stripeObject = deserializer.getObject().get();
            } else {
                // Fallback: Desrialize from raw JSON
                try {
                    stripeObject = deserializer.deserializeUnsafe();
                    if (stripeObject == null) {
                        log.warn("Failed to desrialize failed for event: {}", event.getType());
                        return ResponseEntity.ok().build();
                    }
                } catch (Exception e) {
                    log.error("Unsafe deserialization failed for event: {}", event.getType(), e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Desrialization error");
                }
            }

            // Now extract metadata only if it's a Checkout Session
            Map<String, String> metadata = new HashMap<>();
            if (stripeObject instanceof Session session) {
                metadata = session.getMetadata();
            }

            // Pass to your processor
            paymentProcessor.handleWebhookEvent(event.getType(), stripeObject, metadata);
            return ResponseEntity.ok().build();

        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
    }

}
