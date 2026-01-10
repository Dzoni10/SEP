package com.payment.paymentserviceprovider.config;

import com.payment.paymentserviceprovider.domain.PaymentMethodType;

import java.time.LocalDate;
import java.util.Map;

public record PaymentMethodConfig(
        int id,
        PaymentMethodType type,
        String providerName,
        Map<String, String> config, // API keys, endpoints, itd
        boolean active,
        LocalDate createdAt
) {
}
