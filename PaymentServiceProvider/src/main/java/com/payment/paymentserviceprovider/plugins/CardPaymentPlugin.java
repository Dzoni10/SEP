package com.payment.paymentserviceprovider.plugins;

import com.payment.paymentserviceprovider.domain.*;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;
import com.payment.paymentserviceprovider.bank.domain.PaymentUrlRequest;
import com.payment.paymentserviceprovider.bank.domain.PaymentUrlResponse;
import com.payment.paymentserviceprovider.bank.domain.UpdateCallbackRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class CardPaymentPlugin implements PaymentPlugin {

    private final RestTemplate restTemplate;
    private static final String BANK_BASE_URL = "http://localhost:8081/api/v1/bank";

    public CardPaymentPlugin(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getPluginId() { return "card-payment-plugin"; }

    @Override
    public PaymentMethodType getPaymentMethodType() {
        return PaymentMethodType.CARD;
    }

    @Override
    public void initialize(Map<String, String> config) throws PaymentPluginException {
        // Konfiguracija za bank servis
        // Za sada nema potrebe za dodatnom konfiguracijom
    }

    @Override
    public boolean validateConfiguration(Map<String, String> config) {
        // Za sada uvek validno
        return true;
    }

    @Override
    public PaymentResult processPayment(PaymentRequest request)
            throws PaymentPluginException {
        
        try {
            // 1. Generisanje STAN (System Trace Audit Number)
            String stan = generateSTAN(request.webShopId());
            
            // 2. Generisanje PSP_TIMESTAMP
            LocalDateTime pspTimestamp = LocalDateTime.now();
            
            // 3. MERCHANT_ID za banku (hardkodovano za sada, kasnije iz Merchant entiteta)
            String bankMerchantId = "MERCHANT_BANK_001";
            
            // 4. Zahtev za PAYMENT_URL i PAYMENT_ID (Tabela 2)
            PaymentUrlRequest bankRequest = new PaymentUrlRequest(
                bankMerchantId,
                request.amount(),
                request.currency(),
                stan,
                pspTimestamp
            );
            
            // 5. Poziv bank servisa
            PaymentUrlResponse bankResponse = restTemplate.postForObject(
                BANK_BASE_URL + "/payment-url",
                bankRequest,
                PaymentUrlResponse.class
            );
            
            if (bankResponse == null) {
                throw new PaymentPluginException("Failed to get payment URL from bank");
            }
            
            // 6. Ažuriranje transakcije u banci sa callbackUrl i orderId
            if (request.callbackUrl() != null) {
                UpdateCallbackRequest callbackRequest = new UpdateCallbackRequest(
                    request.callbackUrl(),
                    request.orderId()
                );
                
                restTemplate.put(
                    BANK_BASE_URL + "/payment/" + bankResponse.paymentId() + "/callback",
                    callbackRequest
                );
            }
            
            // 7. Vraćanje PAYMENT_URL korisniku
            return new PaymentResult(
                true,
                bankResponse.paymentId(),
                bankResponse.paymentUrl(),  // URL na formu za unos kartice
                null
            );
            
        } catch (Exception e) {
            throw new PaymentPluginException("Error processing card payment: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generisanje STAN-a (System Trace Audit Number)
     * Format: webShopId-timestamp-random
     */
    private String generateSTAN(int webShopId) {
        return webShopId + "-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public RefundResult refund(String externalTransactionId, double amount) {
        // Refund logika
        return new RefundResult(true, "refund_123", null);
    }

    @Override
    public PaymentStatus checkStatus(String externalTransactionId) {
        // Status check logika
        return new PaymentStatus(externalTransactionId, TransactionStatus.SUCCESS, LocalDate.now());
    }

    @Override
    public boolean isHealthy() {
        // Health check prema Stripe API
        return true;
    }
}
