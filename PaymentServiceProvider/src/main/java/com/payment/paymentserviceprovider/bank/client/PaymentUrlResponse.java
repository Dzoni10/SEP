package com.payment.paymentserviceprovider.bank.client;

public record PaymentUrlResponse(
    String paymentId,
    String paymentUrl
) {}
