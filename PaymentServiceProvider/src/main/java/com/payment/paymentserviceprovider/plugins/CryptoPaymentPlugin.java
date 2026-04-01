package com.payment.paymentserviceprovider.plugins;

import com.payment.paymentserviceprovider.domain.*;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;

import java.util.Map;

@Component
public class CryptoPaymentPlugin implements PaymentPlugin {


    private final RestTemplate restTemplate;

    public CryptoPaymentPlugin(@Qualifier("publicRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getPluginId() { return "crypto-plugin"; }

    @Override
    public PaymentMethodType getPaymentMethodType() {
        return PaymentMethodType.CRYPTO;
    }

// Implementacija za crypto...
    @Override
    public void initialize(Map<String, String> config) throws PaymentPluginException {
// Inicijalizacija crypto provider (npr. Coinbase)
    }

    @Override
    public boolean validateConfiguration(Map<String, String> config) {
        return config.containsKey("crypto.api.key");
    }

    @Override
    public PaymentResult processPayment(PaymentRequest request)
            throws PaymentPluginException {
// Crypto logika
        return new PaymentResult(true, "crypto_txn_456", "https://payment.crypto", null,null);
    }

    @Override
    public RefundResult refund(String externalTransactionId, double amount) {
        return new RefundResult(true, "crypto_refund_456", null);
    }

    @Override
    public PaymentStatus checkStatus(String externalTransactionId) {
        return new PaymentStatus(externalTransactionId, TransactionStatus.PENDING, LocalDate.now());
    }

    @Override
    public boolean isHealthy() {
        return true;
    }

}
