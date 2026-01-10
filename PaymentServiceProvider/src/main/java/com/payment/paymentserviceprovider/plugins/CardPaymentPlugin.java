package com.payment.paymentserviceprovider.plugins;

import com.payment.paymentserviceprovider.domain.*;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class CardPaymentPlugin implements PaymentPlugin {

    @Override
    public String getPluginId() { return "stripe-card-plugin"; }

    @Override
    public PaymentMethodType getPaymentMethodType() {
        return PaymentMethodType.CARD;
    }

    @Override
    public void initialize(Map<String, String> config) throws PaymentPluginException {
        String apiKey = config.get("stripe.api.key");
        String secretKey = config.get("stripe.secret.key");

        if (apiKey == null || secretKey == null) {
            throw new PaymentPluginException("Missing Stripe credentials");
        }
        // Inicijalizacija Stripe client-a
    }

    @Override
    public boolean validateConfiguration(Map<String, String> config) {
        return config.containsKey("stripe.api.key") &&
                config.containsKey("stripe.secret.key");
    }

    @Override
    public PaymentResult processPayment(PaymentRequest request)
            throws PaymentPluginException {
        // Stripe logika
        return new PaymentResult(true, "stripe_txn_123", null, null);
    }

    @Override
    public RefundResult refund(String externalTransactionId, double amount) {
        // Refund logika
        return new RefundResult(true, "refund_123", null);
    }

    @Override
    public PaymentStatus checkStatus(String externalTransactionId) {
        // Status check logika
        return new PaymentStatus(externalTransactionId, TransactionStatus.SUCCESS, LocalDate.now());
    }

    @Override
    public boolean isHealthy() {
        // Health check prema Stripe API
        return true;
    }
}
