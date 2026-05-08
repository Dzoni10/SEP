package com.bankservice.bank.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {

    private String globalTransactionId;
    private int merchantId;
    private LocalDateTime acquirerTimestamp;
    private String pspTimestamp;
    private String stan;
    private PaymentStatus status;

    public PaymentResponse(String globalTransactionId, int merchantId,LocalDateTime acquirerTimestamp,String pspTimestamp,String stan, PaymentStatus paymentStatus) {
        this.status = paymentStatus;
        this.globalTransactionId = globalTransactionId;
        this.pspTimestamp = pspTimestamp;
        this.stan = stan;
        this.merchantId = merchantId;
        this.acquirerTimestamp = acquirerTimestamp;
    }
}
