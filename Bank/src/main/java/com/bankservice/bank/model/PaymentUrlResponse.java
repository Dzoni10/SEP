package com.bankservice.bank.model;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"paymentId","paymentUrl"})
public class PaymentUrlResponse {
    private String paymentId;
    private String paymentUrl;
}
