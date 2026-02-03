package com.bankservice.bank.domain;

public record CardPaymentRequest(
    String paymentId,
    String pan,
    String securityCode,
    String cardHolderName,
    String expiryDate
) {}
