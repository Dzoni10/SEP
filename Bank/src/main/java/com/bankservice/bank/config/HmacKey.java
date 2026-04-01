package com.bankservice.bank.config;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class HmacKey {

    public String generateHmac(String algorithm, String data, String secretKey){
        try{
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return convertToHex(rawHmac);
        } catch(Exception e){
            throw new RuntimeException("Cannot calculate", e);
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
