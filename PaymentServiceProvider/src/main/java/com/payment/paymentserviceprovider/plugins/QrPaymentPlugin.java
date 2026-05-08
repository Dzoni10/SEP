package com.payment.paymentserviceprovider.plugins;

import com.payment.paymentserviceprovider.domain.*;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
public class QrPaymentPlugin implements PaymentPlugin {

    @Override
    public String getPluginId() { return "qr-payment-plugin"; }

    @Override
    public PaymentMethodType getPaymentMethodType() {
        return PaymentMethodType.QR;
    }

    @Override
    public void initialize(Map<String, String> config) throws PaymentPluginException {
    }

    @Override
    public boolean validateConfiguration(Map<String, String> config) {
        return true;
    }

    @Override
    public PaymentResult processPayment(PaymentRequest request) throws PaymentPluginException {
        return new PaymentResult(true, "qr_" + System.currentTimeMillis(), "https://localhost:8081/neki-qr-url", null,null);
    }

    @Override
    public RefundResult refund(String externalTransactionId, double amount) {
        return new RefundResult(true, "refund_qr_123", null);
    }

    @Override
    public PaymentStatus checkStatus(String externalTransactionId) {
        return new PaymentStatus(externalTransactionId, TransactionStatus.SUCCESS, LocalDate.now());
    }

    @Override
    public boolean isHealthy() {
        return true;
    }
}
