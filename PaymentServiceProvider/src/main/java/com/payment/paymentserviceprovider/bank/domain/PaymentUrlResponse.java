package com.payment.paymentserviceprovider.bank.domain;

/**
 * Odgovor sa PAYMENT_URL i PAYMENT_ID
 */
public record PaymentUrlResponse(
    String paymentId,   // PAYMENT_ID
    String paymentUrl   // PAYMENT_URL - URL na formu za unos kartice
) {}
