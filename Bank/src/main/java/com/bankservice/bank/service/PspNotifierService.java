package com.bankservice.bank.service;

import com.bankservice.bank.dto.PspWebhookRequest;
import com.bankservice.bank.model.BankTransaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class PspNotifierService {
    private final RestTemplate restTemplate;


    @Value("${psp.password-secret}")
    private String secretKey;

    public PspNotifierService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public void notifyPsp(BankTransaction transaction){
        try{
            PspWebhookRequest request = new PspWebhookRequest(
                    transaction.getStan(),
                    transaction.getGlobalTransactionId(),
                    transaction.getAcquirerTimestamp(),
                    transaction.getStatus()
            );
            if (transaction.getCallbackUrl() != null && !transaction.getCallbackUrl().isEmpty()) {

                String rawData = request.getStan() + (request.getStatus() != null ? request.getStatus().name() : "");
                String signature = calculateHmac(rawData);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("X-HMAC-Signature",signature);

                HttpEntity<PspWebhookRequest> entity = new HttpEntity<>(request,headers);
                restTemplate.postForEntity(transaction.getCallbackUrl(),entity,Void.class);
                System.out.println("Success notify PSP for transaction: " + transaction.getPaymentId());
            } else {
                System.err.println("There are no Callback URL for transaction: " + transaction.getPaymentId());
            }
        }catch (Exception e){
            System.err.println("Error notifying PSP for transaction: " + transaction.getPaymentId());
        }
    }

    private String calculateHmac(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(this.secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hex = new StringBuilder();
        for(byte b : rawHmac){
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
