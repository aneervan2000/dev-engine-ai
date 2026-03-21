package com.anee.projects.lovable_clone.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * <h5>Configuration class for Stripe Payment</h5>
 * This class initializes the Stripe API key using the secret key from application properties.
 * The @PostConstruct annotation ensures that the Stripe API key is set after the bean is created and dependencies are injected.
 *
 */
@Configuration
public class PaymentConfig {

    @Value("${stripe.api.secret}")
    private String stripeSecretKey;

    // Initialize Stripe API key after the bean is constructed
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }
}
