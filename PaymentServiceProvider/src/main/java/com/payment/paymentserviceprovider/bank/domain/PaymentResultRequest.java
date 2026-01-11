package com.payment.paymentserviceprovider.bank.domain;

import java.time.LocalDateTime;

/**
 * Zahtev za callback rezultata plaćanja
 * Banka šalje PSP-u nakon procesiranja
 */
public record PaymentResultRequest(
    String stan,                    // STAN
    String globalTransactionId,      // GLOBAL_TRANSACTION_ID
    LocalDateTime acquirerTimestamp, // ACQUIRER_TIMESTAMP
    boolean success,                 // Status transakcije
    int orderId                     // Order ID (za callback)
) {}
