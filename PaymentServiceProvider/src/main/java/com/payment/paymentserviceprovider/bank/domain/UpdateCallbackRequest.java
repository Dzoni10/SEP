package com.payment.paymentserviceprovider.bank.domain;

/**
 * Zahtev za ažuriranje transakcije sa callbackUrl i orderId
 */
public record UpdateCallbackRequest(
    String callbackUrl,
    Integer orderId
) {}
