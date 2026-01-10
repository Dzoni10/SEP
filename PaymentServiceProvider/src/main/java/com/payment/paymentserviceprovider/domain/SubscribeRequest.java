package com.payment.paymentserviceprovider.domain;

import java.util.List;

public record SubscribeRequest(
        List<PaymentMethodType> methods
) {
}
