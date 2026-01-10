package com.payment.paymentserviceprovider.domain;

public record PaymentResult(
        boolean success,
        String externalTransactionId,
        String redirectUrl,
        String errorMessage
) {
}
