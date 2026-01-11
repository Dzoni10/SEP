package com.payment.paymentserviceprovider.domain;

import java.util.Map;

public record PaymentRequest(
        int webShopId,
        int orderId,
        double amount,
        String currency,
        String callbackUrl,
        String successUrl,
        String failedUrl,
        String errorUrl,
        Map<String, String> metadata
) {
}
