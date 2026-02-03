package com.payment.paymentserviceprovider.config;

import com.payment.paymentserviceprovider.domain.PaymentMethodType;
import com.payment.paymentserviceprovider.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubscriptionInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionInitializer.class);
    private final SubscriptionService subscriptionService;

    public SubscriptionInitializer(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public void run(String... args) {
        try {
            subscriptionService.subscribeToPaymentMethods(1, List.of(PaymentMethodType.CARD));
            log.info("Web shop 1 subscribed to CARD");
        } catch (Exception e) {
            log.error("SubscriptionInitializer failed: {}", e.getMessage(), e);
        }
    }
}
