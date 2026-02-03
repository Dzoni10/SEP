package com.bankservice.bank.domain;

import java.time.LocalDateTime;

/**
 * Zahtev za dobijanje PAYMENT_URL i PAYMENT_ID
 * Parametri iz Tabele 2 (PSP → Bank)
 */
public record PaymentUrlRequest(
    String merchantId,
    double amount,
    String currency,
    String stan,
    LocalDateTime pspTimestamp
) {}
