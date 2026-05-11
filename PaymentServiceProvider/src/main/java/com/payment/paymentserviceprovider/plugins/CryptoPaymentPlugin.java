package com.payment.paymentserviceprovider.plugins;

import com.payment.paymentserviceprovider.domain.*;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class CryptoPaymentPlugin implements PaymentPlugin {

    @Value("${coingate.api.url}")
    private String coingateApiUrl;

    @Value("${coingate.api.token}")
    private String apiToken;

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


    @Override
    public void initialize(Map<String, String> config) throws PaymentPluginException {}

    @Override
    public boolean validateConfiguration(Map<String, String> config) {
        return true;
    }

    @Override
    public PaymentResult processPayment(PaymentRequest request)
            throws PaymentPluginException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiToken);

            String uniqueStan = UUID.randomUUID().toString();

            Map<String, Object> body = new HashMap<>();
            body.put("order_id", request.orderId() + "-" + System.currentTimeMillis());
            body.put("price_amount", request.amount());
            body.put("price_currency", request.currency());
            body.put("receive_currency", request.currency());

            body.put("success_url", request.successUrl() + "?token=" + uniqueStan);
            body.put("cancel_url", request.failedUrl());
            body.put("callback_url", "https://shoptalk-cinnamon-bust.ngrok-free.dev/api/v1/psp/webhook/coingate");


            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(coingateApiUrl, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, String> respBody = response.getBody();
                String paymentUrl = (String) respBody.get("payment_url");
                String externalId = String.valueOf(respBody.get("id"));

                return new PaymentResult(true, externalId, paymentUrl, null, uniqueStan);
            }
            return new PaymentResult(false, null, null, "Coin gate refused request", null);
        } catch (Exception e) {
            throw new PaymentPluginException( "Error with communication with CoinGate "+ e.getMessage());
        }
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
