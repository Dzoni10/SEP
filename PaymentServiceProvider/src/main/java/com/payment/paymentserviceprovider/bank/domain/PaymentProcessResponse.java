package com.payment.paymentserviceprovider.bank.domain;

import java.time.LocalDateTime;

/**
 * Odgovor nakon procesiranja plaćanja
 * Sadrži GLOBAL_TRANSACTION_ID i ACQUIRER_TIMESTAMP
 */
public record PaymentProcessResponse(
    boolean success,                    // Status transakcije
    String errorMessage,                // Poruka greške (ako postoji)
    String globalTransactionId,         // GLOBAL_TRANSACTION_ID
    LocalDateTime acquirerTimestamp,    // ACQUIRER_TIMESTAMP
    String stan                         // STAN (za praćenje)
) {}
