package com.payment.paymentserviceprovider.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class HmacKey {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Value("${psp.password-secret}")
    private String secretKey;

    public String generateHmac(String data){
        try{
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKeySpec);

            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return convertToHex(rawHmac);

        } catch(Exception e){
            throw new RuntimeException("Cannot find HMAC", e);
        }
    }
    private String convertToHex(byte[] bytes){
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for(byte b : bytes){
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
