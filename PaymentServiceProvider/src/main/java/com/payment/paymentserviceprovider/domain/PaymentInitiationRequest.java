package com.payment.paymentserviceprovider.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public record PaymentInitiationRequest(
        String merchantId,          //  ID koji je Web Shop dobio od PSP-a
        String merchantPassword, // Sifra koju je Web shop dobio od PSP
        double amount,
        String currency,
        String merchantOrderId, //Generise se od strane web shopa
        LocalDateTime merchantTimeStamp,
        String successUrl,
        String failedUrl,
        String errorUrl,
        PaymentMethodType paymentMethod
) {
}
