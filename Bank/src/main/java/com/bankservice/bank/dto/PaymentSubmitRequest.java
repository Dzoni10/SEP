package com.bankservice.bank.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentSubmitRequest {
    private String pan;
    private String securityCode;
    private String cardHolderName;
    private String expirationDate; // MM/YY
}
