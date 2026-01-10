package com.payment.paymentapp.shared;

public record PaymentResponse (
        boolean success,
        String redirectUrl,
        String transactionId,
        String errorMessage
) {
}
