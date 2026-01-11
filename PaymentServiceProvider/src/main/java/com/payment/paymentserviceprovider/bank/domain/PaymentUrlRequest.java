package com.payment.paymentserviceprovider.bank.domain;

import java.time.LocalDateTime;

/**
 * Zahtev za dobijanje PAYMENT_URL i PAYMENT_ID
 * Parametri iz Tabele 2 (PSP → Bank)
 */
public record PaymentUrlRequest(
    String merchantId,      // MERCHANT_ID za banku (različit od MERCHANT_ID iz Tabele 1)
    double amount,           // AMOUNT
    String currency,        // CURRENCY
    String stan,            // STAN (System Trace Audit Number)
    LocalDateTime pspTimestamp  // PSP_TIMESTAMP
) {}
