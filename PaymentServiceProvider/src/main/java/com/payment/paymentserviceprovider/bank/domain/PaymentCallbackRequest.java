package com.payment.paymentserviceprovider.bank.domain;

/**
 * Callback zahtev koji se šalje Web Shop-u
 */
public record PaymentCallbackRequest(
    boolean success,
    String globalTransactionId,
    String errorMessage
) {}
