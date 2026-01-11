package com.payment.paymentserviceprovider.bank.domain;

/**
 * Zahtev za ažuriranje transakcije sa callbackUrl, orderId i redirect URL-ovima
 */
public record UpdateCallbackRequest(
    String callbackUrl,
    Integer orderId,
    String successUrl,
    String failedUrl,
    String errorUrl
) {}
