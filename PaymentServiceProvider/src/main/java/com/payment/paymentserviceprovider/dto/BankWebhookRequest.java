package com.payment.paymentserviceprovider.dto;

import com.payment.paymentserviceprovider.domain.PaymentStatus;

import java.time.LocalDateTime;

public record BankWebhookRequest(
        String stan,
        String globalTransactionId,
        LocalDateTime acquirerTimestamp,
        String status
) {}
