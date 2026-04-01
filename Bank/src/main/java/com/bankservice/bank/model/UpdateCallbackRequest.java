package com.bankservice.bank.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonPropertyOrder({"callbackUrl","orderId","successUrl","failedUrl","errorUrl"})
public class UpdateCallbackRequest {
    private String callbackUrl;
    private Integer orderId;
    private String successUrl;
    private String failedUrl;
    private String errorUrl;
}
