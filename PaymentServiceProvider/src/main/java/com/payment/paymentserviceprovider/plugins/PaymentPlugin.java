package com.payment.paymentserviceprovider.plugins;

import com.payment.paymentserviceprovider.domain.*;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;

import java.util.Map;

public interface PaymentPlugin {

    /**
     * Unikatni ID plug-ina
     */
    String getPluginId();

    /**
     * Tip plaćanja koji ovaj plug-in podržava
     */
    PaymentMethodType getPaymentMethodType();

    /**
     * Inicijalizacija plug-ina sa konfiguracijom
     */
    void initialize(Map<String, String> config) throws PaymentPluginException;

    /**
     * Validacija konfiguracije
     */
    boolean validateConfiguration(Map<String, String> config);

    /**
     * Procesiranje plaćanja
     */
    PaymentResult processPayment(PaymentRequest request) throws PaymentPluginException;

    /**
     * Refundiranje
     */
    RefundResult refund(String externalTransactionId, double amount)
            throws PaymentPluginException;

    /**
     * Provera statusa
     */
    PaymentStatus checkStatus(String externalTransactionId) throws PaymentPluginException;

    /**
     * Health check
     */
    boolean isHealthy();
}


