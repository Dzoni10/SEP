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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Locale;
import java.util.Map;

@Component
public class PayPalPaymentPlugin implements PaymentPlugin {

    private final RestTemplate restTemplate;

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal_api_url}")
    private String PAYPAL_API_BASE;

    public PayPalPaymentPlugin(@Qualifier("publicRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getPluginId() { return "paypal-plugin"; }

    @Override
    public PaymentMethodType getPaymentMethodType() { return PaymentMethodType.PAYPAL; }

    @Override
    public void initialize(Map<String, String> config) throws PaymentPluginException { }

    @Override
    public boolean validateConfiguration(Map<String, String> config) { return true; }


    @Override
    public PaymentResult processPayment(PaymentRequest request) throws PaymentPluginException {
        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            String returnUrl = "https://localhost:8081/api/v1/psp/paypal/success";
            String cancelUrl = "https://localhost:8081/api/v1/psp/paypal/cancel";

            double amountInEur = request.amount();
            if("RSD".equalsIgnoreCase(request.currency())){
                amountInEur= request.amount() /117.2;
            }else if("USD".equalsIgnoreCase(request.currency())){
                amountInEur= request.amount() * 0.8629;
            }else if("EUR".equalsIgnoreCase(request.currency())){
                amountInEur= request.amount();
            }

            String formattedAmount = String.format(Locale.US,"%.2f",amountInEur);

            String payload = String.format("""
                {
                  "intent": "CAPTURE",
                  "purchase_units": [
                    {
                      "amount": {
                        "currency_code": "EUR",
                        "value": "%s"
                      }
                    }
                  ],
                  "application_context": {
                    "return_url": "%s",
                    "cancel_url": "%s",
                    "user_action": "PAY_NOW"
                  }
                }
                """, formattedAmount, returnUrl, cancelUrl);

            HttpEntity<String> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(PAYPAL_API_BASE + "/v2/checkout/orders", entity, Map.class);

            Map<String, Object> body = response.getBody();
            if (body == null) throw new Exception("Empty answer from PayPal-a");

            String orderId = (String) body.get("id"); // PayPal-ov ID transakcije
            String approveUrl = "";

            var links = (java.util.List<Map<String, String>>) body.get("links");
            for (Map<String, String> link : links) {
                if ("approve".equals(link.get("rel"))) {
                    approveUrl = link.get("href");
                    break;
                }
            }
            return new PaymentResult(true, orderId, approveUrl, null,null);

        } catch (Exception e) {
            System.err.println("PayPal error: " + e.getMessage());
            throw new PaymentPluginException("Error during creating pay pal payment: " + e.getMessage(), e);
        }
    }

    private String getAccessToken() {
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(encodedAuth);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(PAYPAL_API_BASE + "/v1/oauth2/token", request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    @Override
    public RefundResult refund(String externalTransactionId, double amount) { return new RefundResult(true, "ref", null); }

    @Override
    public PaymentStatus checkStatus(String externalTransactionId) { return null; }

    @Override
    public boolean isHealthy() { return true; }
}
