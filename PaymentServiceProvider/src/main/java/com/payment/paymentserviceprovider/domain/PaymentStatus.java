package com.payment.paymentserviceprovider.domain;

import java.time.LocalDate;
import java.util.Date;

public record PaymentStatus (
        String externalTransactionId,
        TransactionStatus status,
        LocalDate lastUpdated
) {
}
