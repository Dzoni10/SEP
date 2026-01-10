package com.payment.paymentserviceprovider.domain;

public record RefundResult(
        boolean success,
        String externalRefundId,
        String errorMessage
) {
}
