package com.bankservice.bank.domain;

import java.time.LocalDateTime;

public record PaymentProcessResponse(
    boolean success,
    String errorMessage,
    String globalTransactionId,
    LocalDateTime acquirerTimestamp,
    String stan,
    String redirectUrl
) {}
