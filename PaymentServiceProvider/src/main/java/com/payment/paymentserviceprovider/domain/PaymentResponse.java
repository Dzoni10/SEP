package com.payment.paymentserviceprovider.domain;

public record PaymentResponse(
        boolean success,
        String redirectUrl,
        String transactionId,
        String errorMessage
) {
}
