package com.payment.paymentserviceprovider.plugins;

import com.payment.paymentserviceprovider.config.HmacKey;
import com.payment.paymentserviceprovider.domain.*;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;
import com.payment.paymentserviceprovider.bank.domain.PaymentUrlRequest;
import com.payment.paymentserviceprovider.bank.domain.PaymentUrlResponse;
import com.payment.paymentserviceprovider.bank.domain.UpdateCallbackRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class CardPaymentPlugin implements PaymentPlugin {

    private final RestTemplate restTemplate;

    @Value("${BANK_BASE_URL}")
    private String BANK_BASE_URL;
    private final HmacKey hmacKey;

    public CardPaymentPlugin(RestTemplate restTemplate, HmacKey hmacKey) {
        this.restTemplate = restTemplate;
        this.hmacKey=hmacKey;
    }

    @Override
    public String getPluginId() { return "card-payment-plugin"; }

    @Override
    public PaymentMethodType getPaymentMethodType() {
        return PaymentMethodType.CARD;
    }

    @Override
    public void initialize(Map<String, String> config) throws PaymentPluginException {
    }

    @Override
    public boolean validateConfiguration(Map<String, String> config) {
        return true;
    }

    @Override
    public PaymentResult processPayment(PaymentRequest request)
            throws PaymentPluginException {
        
        try {
            String stan = generateSTAN(request.webShopId());
            LocalDateTime pspTimestamp = LocalDateTime.now();
            String bankMerchantId = "MERCHANT_BANK_001";
            
            PaymentUrlRequest bankRequest = new PaymentUrlRequest(
                bankMerchantId,
                request.amount(),
                request.currency(),
                stan,
                pspTimestamp
            );

            String rawData = bankRequest.merchantId() + bankRequest.amount() + bankRequest.currency() + bankRequest.stan();
            String signature = hmacKey.generateHmac(rawData);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-HMAC-SIGNATURE",signature);
            HttpEntity<PaymentUrlRequest> entity = new HttpEntity<>(bankRequest, headers);

            PaymentUrlResponse bankResponse = restTemplate.postForObject(
                BANK_BASE_URL + "/payment-url",
                entity,
                PaymentUrlResponse.class
            );
            
            if (bankResponse == null) {
                throw new PaymentPluginException("Failed to get payment URL from bank");
            }

            UpdateCallbackRequest callbackRequest = new UpdateCallbackRequest(
                request.callbackUrl(),
                request.orderId(),
                request.successUrl(),
                request.failedUrl(),
                request.errorUrl()
            );
            
            restTemplate.put(
                BANK_BASE_URL + "/payment/" + bankResponse.paymentId() + "/callback",
                callbackRequest
            );

            return new PaymentResult(
                true,
                bankResponse.paymentId(),
                bankResponse.paymentUrl(),  // URL na formu za unos kartice
                null,
                    stan
            );
            
        } catch (Exception e) {
            throw new PaymentPluginException("Error processing card payment: " + e.getMessage(), e);
        }
    }

    private String generateSTAN(int webShopId) {
        return webShopId + "-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public RefundResult refund(String externalTransactionId, double amount) {
        return new RefundResult(true, "refund_123", null);
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
