package com.bankservice.bank.dto;

import com.bankservice.bank.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PspWebhookRequest {
    private String stan;
    private String globalTransactionId;
    private LocalDateTime acquirerTimestamp;
    private PaymentStatus status;
}
