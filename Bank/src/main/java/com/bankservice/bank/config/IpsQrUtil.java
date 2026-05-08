package com.bankservice.bank.config;

import com.bankservice.bank.model.BankTransaction;
import com.bankservice.bank.model.Merchant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IpsQrUtil {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${nbs_url:https://nbs.rs/QRcode/api/qr/v1/generate?lang=sr_RS_Latn}")
    String nbsApiUrl;

    public Map<String,String> generateNbsQr(BankTransaction transaction, Merchant merchant) throws Exception {

        double finalAmount = transaction.getAmount();

        if ("EUR".equalsIgnoreCase(transaction.getCurrency())) {
            double exchangeRate = 117.20; // Fiksni kurs za simulaciju
            finalAmount = finalAmount * exchangeRate;
        } else if (!"RSD".equalsIgnoreCase(transaction.getCurrency())) {
            throw new Exception("IPS QR support only RSD (or EUR with automatic converting).");
        }

        String formattedAmount = String.format(java.util.Locale.US, "%.2f", finalAmount).replace(".", ",");
        String rawAccount = merchant.getAccountNumber().replace("-", "").trim();
        String accountNumber;

        if (rawAccount.length() < 18) {
            String[] parts = merchant.getAccountNumber().split("-");
            if (parts.length == 3) {
                String middlePart = String.format("%13s", parts[1]).replace(' ', '0');
                accountNumber = parts[0] + middlePart + parts[2];
            } else {
                throw new Exception("NBS validation error: Neispravan format racuna u bazi (" + merchant.getAccountNumber() + ")");
            }
        } else if (rawAccount.length() == 18) {
            accountNumber = rawAccount;
        } else {
            throw new Exception("NBS validation error: Racun je duzi od 18 cifara!");
        }

        String cleanMerchantName = merchant.getName().toUpperCase().replaceAll("[^A-Z0-9 ]", "").trim();
        if (cleanMerchantName.isEmpty()) {
            cleanMerchantName = "TRGOVAC";
        }
        String nbsCurrency = "RSD";

        String ipsString = String.format("K:PR|V:01|C:1|R:%s|N:%s|I:%s%s|SF:289|S:UPLATA",
                accountNumber,
                cleanMerchantName,
                nbsCurrency,
                formattedAmount);

        System.out.println("Saljem na NBS: " + ipsString);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> entity = new HttpEntity<>(ipsString, httpHeaders);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(this.nbsApiUrl, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("s")) {
                Map<String, Object> status = (Map<String, Object>) body.get("s");
                Integer code = (Integer) status.get("code"); // code 0 znači da je OK [cite: 110, 111, 286]

                if (code == 0) {
                    // Sistem vraća tag 'i' sa Base64 slikom i tag 't' sa validiranim tekstom [cite: 142, 146]
                    return Map.of(
                            "qrString", (String) body.get("t"),
                            "base64Image", (String) body.get("i")
                    );
                } else {
                    List<String> errors = (List<String>) body.get("e");
                    System.out.println("NBS error detail (tag 'e'): " + errors);
                    throw new Exception("NBS validation error: " + status.get("desc") + " | Detalji: " + errors);
                }
            }
            throw new Exception("Incorrect response from NBS API");
        } catch (Exception e) {
            throw new Exception("Error during generating NBS QR code: " + e.getMessage());
        }
    }

    public boolean validateScannedIpsString(String scannedString, BankTransaction transaction, Merchant merchant) {
        if (scannedString == null || !scannedString.startsWith("K:PR|V:01")) {
            return false;
        }

        Map<String, String> qrData = new HashMap<>();
        String[] parts = scannedString.split("\\|");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2) {
                qrData.put(keyValue[0], keyValue[1]);
            }
        }

        String rawAccount = merchant.getAccountNumber().replace("-","").trim();
        String expectedAccount = rawAccount;

        if(rawAccount.length() < 18){
            String[] accParts = merchant.getAccountNumber().split("-");
            if(accParts.length == 3){
                expectedAccount = accParts[0] + String.format("%13s",accParts[1]).replace(' ','0') + accParts[2];
            }
        }

        double finalAmount = transaction.getAmount();
        if("EUR".equalsIgnoreCase(transaction.getCurrency())){
            finalAmount = finalAmount * 117.2;
        }

        String expectedAmountStr = String.format(java.util.Locale.US, "%.2f", finalAmount).replace(".", ",");
        String expectedAmountAndCurrency = "RSD" + expectedAmountStr;

        boolean isAccountValid = expectedAccount.equals(qrData.get("R"));
        boolean isAmountValid = expectedAmountAndCurrency.equals(qrData.get("I"));

        return isAccountValid && isAmountValid;
    }
}