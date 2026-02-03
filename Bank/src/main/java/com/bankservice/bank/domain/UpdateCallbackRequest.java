package com.bankservice.bank.domain;

public record UpdateCallbackRequest(
    String callbackUrl,
    Integer orderId,
    String successUrl,
    String failedUrl,
    String errorUrl
) {}
