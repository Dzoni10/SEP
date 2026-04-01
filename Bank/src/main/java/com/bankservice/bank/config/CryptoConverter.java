package com.bankservice.bank.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
@Converter
public class CryptoConverter implements AttributeConverter<String, String> {
    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "MySuperSecretKey".getBytes();

    @Override
    public String convertToDatabaseColumn(String sensitiveData) {
        if(sensitiveData == null) return null;
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY, ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE,keySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(sensitiveData.getBytes()));
        }catch (Exception e) {
            throw new RuntimeException("Error during data encryption: " + e.getMessage());
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if(dbData == null) return null;
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY, ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE,keySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        }catch (Exception e) {
            throw new RuntimeException("Error during data decryption: " + e.getMessage());
        }
    }
}
