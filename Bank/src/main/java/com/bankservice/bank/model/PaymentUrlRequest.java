package com.bankservice.bank.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@JsonPropertyOrder({"merchantId","amount","currency","stan","pspTimestamp"})
public class PaymentUrlRequest {
    private String merchantId;
    private double amount;
    private String currency;
    private String stan;
    private LocalDateTime pspTimestamp;
}
