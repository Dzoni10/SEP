package com.payment.paymentserviceprovider.bank.client;

public record UpdateCallbackRequest(
    String callbackUrl,
    Integer orderId,
    String successUrl,
    String failedUrl,
    String errorUrl
) {}
