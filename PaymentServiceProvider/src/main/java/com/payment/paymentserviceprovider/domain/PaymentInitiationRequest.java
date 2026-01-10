package com.payment.paymentserviceprovider.domain;

import java.util.Map;

public record PaymentInitiationRequest(
        int orderId,
        double amount,
        String currency,
        PaymentMethodType paymentMethod,
        String callbackUrl,
        Map<String, String> metadata
) {
}
