package com.bankservice.bank.auth;

import com.bankservice.bank.config.HmacKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;

@Service
public class PSPAuthentication {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Value("${psp.password-secret}")
    private String sharedSecret;

    private final ObjectMapper objectMapper;

    private final HmacKey hmacKey;

    public PSPAuthentication(HmacKey hmacUtil, ObjectMapper objectMapper) {
        this.hmacKey = hmacUtil;
        this.objectMapper = objectMapper;
    }

    public void checkRequest(Object requestBody, String receivedSignature){
        try{
            String dataToSign = objectMapper.writeValueAsString(requestBody);
            String expectedSignature = hmacKey.generateHmac(HMAC_ALGORITHM, dataToSign, sharedSecret);
            System.out.println("TRUE:" + expectedSignature);
            System.out.println("RECEIVED:" + receivedSignature);
            if(!expectedSignature.equals(receivedSignature)){
                throw new SecurityException("Bad HMAC key");
            }
        } catch(JsonParseException e){
            throw new RuntimeException("Cannot validate body", e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
