package com.payment.paymentserviceprovider.bank.client;

import java.time.LocalDateTime;

public record PaymentUrlRequest(
    String merchantId,
    double amount,
    String currency,
    String stan,
    LocalDateTime pspTimestamp
) {}
