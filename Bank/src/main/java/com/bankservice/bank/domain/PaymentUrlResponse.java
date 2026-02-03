package com.bankservice.bank.domain;

public record PaymentUrlResponse(
    String paymentId,
    String paymentUrl
) {}
