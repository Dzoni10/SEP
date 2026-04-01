package com.bankservice.bank.controller;

import com.bankservice.bank.auth.PSPAuthentication;
import com.bankservice.bank.config.IpsQrUtil;
import com.bankservice.bank.dto.PaymentSubmitRequest;
import com.bankservice.bank.model.*;
import com.bankservice.bank.repositoryInterface.MerchantRepositoryInterface;
import com.bankservice.bank.service.BankPaymentService;
import com.bankservice.bank.service.PspNotifierService;
import com.bankservice.bank.service.TransactionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/bank")
public class BankPaymentController {

    @Value("${psp.password-secret}")
    private String secretKey;

    private final TransactionService transactionService;
    private final BankPaymentService bankPaymentService;
    private final PspNotifierService pspNotifierService;
    private final PSPAuthentication pspAuthentication;
    private final MerchantRepositoryInterface merchantRepositoryInterface;
    private final IpsQrUtil ipsQrUtil;

    public BankPaymentController(BankPaymentService bankPaymentService, TransactionService transactionService, PspNotifierService pspNotifierService, PSPAuthentication pspAuthentication, MerchantRepositoryInterface merchantRepositoryInterface, IpsQrUtil ipsQrUtil){
    this.bankPaymentService = bankPaymentService;
    this.transactionService = transactionService;
    this.pspNotifierService=pspNotifierService;
    this.pspAuthentication=pspAuthentication;
    this.merchantRepositoryInterface = merchantRepositoryInterface;
    this.ipsQrUtil = ipsQrUtil;
    }

    @PostMapping("/payment-url")
    public ResponseEntity<?> createPaymentUrl(@RequestBody PaymentUrlRequest request,@RequestHeader("X-HMAC-Signature") String providedSignature)
    {
        try {
            String rawData = request.getMerchantId()+ request.getAmount() + request.getCurrency() + request.getStan();
            String calculatedSignature = calculateHmac(rawData);

            if(!calculatedSignature.equals(providedSignature)){
                return ResponseEntity.status(403).body("Invalid HMAC signature: " +rawData + "\n sigature: " + calculatedSignature);
            }

            if(request.getMerchantId() == null || request.getMerchantId().trim().isEmpty()){
                return ResponseEntity.badRequest().body("MERCHANT_ID is missing or invalid");
            }
            String paymentId = UUID.randomUUID().toString();
            String paymentUrl = "https://localhost:4400/pay/" + paymentId;
            transactionService.createTransaction(request,paymentId);

            PaymentUrlResponse response = new PaymentUrlResponse(paymentId,paymentUrl);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Error on bank side: "+e.getMessage());
        }
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<?> getPaymentDetails(@PathVariable String paymentId) {
        try {
            BankTransaction transaction = transactionService.getTransaction(paymentId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/payment/{paymentId}/callback")
    public ResponseEntity<?> updateCallbacks(@PathVariable String paymentId, @RequestBody UpdateCallbackRequest request){
        transactionService.updateCallbackUrls(paymentId,request);
        return  ResponseEntity.ok().build();
    }

    @PostMapping("/payment/{paymentId}/submit")
    public ResponseEntity<?> submitPayment(
            @PathVariable String paymentId,
            @RequestBody PaymentSubmitRequest request) {
        try {
            BankTransaction completedTx = bankPaymentService.processPayment(paymentId, request);
            pspNotifierService.notifyPsp(completedTx);

            if(completedTx.getStatus()== PaymentStatus.SUCCESS){
                String baseUrl = completedTx.getSuccessUrl();
                String tokenParam = "token=" + completedTx.getPaymentId();
                String finalRedirectUrl = baseUrl.contains("?") ?
                        baseUrl + "&" + tokenParam:
                        baseUrl + "?" + tokenParam;
                return ResponseEntity.ok(finalRedirectUrl);
            }
            else{
                String baseUrl = completedTx.getErrorUrl();
                String tokenParam = "token=" + completedTx.getPaymentId();
                String finalRedirectUrl = baseUrl.contains("?") ?
                        baseUrl + "&" + tokenParam:
                        baseUrl + "?" + tokenParam;
                return ResponseEntity.ok(finalRedirectUrl);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/payment/{paymentId}/qr")
    public ResponseEntity<?> getQrCodeString(@PathVariable String paymentId) {
        BankTransaction transaction = transactionService.getTransaction(paymentId);
        Merchant merchant = merchantRepositoryInterface.findByMerchantId(transaction.getMerchantId()).orElseThrow();
        String formattedAmount = String.format("RSD%.2f", transaction.getAmount()).replace(".", ",");

        String ipsString = String.format(
                "K:PR|V:01|C:1|R:%s|N:%s|I:%s|SF:123|S:Naknada iznajmljivanja",
                merchant.getAccountNumber().replace("-", ""), // Račun prodavca
                merchant.getName(), // Ime prodavca
                formattedAmount // Iznos
        );
        return ResponseEntity.ok(ipsString);
    }

    @GetMapping("/payment/{paymentId}/qr-data")
    public ResponseEntity<?> getQrData(@PathVariable String paymentId) {
        try {
            BankTransaction transaction = transactionService.getTransaction(paymentId);
            Merchant merchant = merchantRepositoryInterface.findByMerchantId(transaction.getMerchantId())
                    .orElseThrow(() -> new RuntimeException("Merchant doesn't exist"));

            Map<String,String> nbsResponse = ipsQrUtil.generateNbsQr(transaction, merchant);
            return ResponseEntity.ok(nbsResponse); // Frontend dobija string "K:PR|V:01..." i crta QR
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/payment/{paymentId}/qr-submit")
    public ResponseEntity<?> submitQrPayment(
            @PathVariable String paymentId,
            @RequestBody String scannedQrString) {
        try {
            BankTransaction completedTx = bankPaymentService.processQrPayment(paymentId, scannedQrString);
            pspNotifierService.notifyPsp(completedTx);

            String tokenParam = "token=" + completedTx.getPaymentId();
            String redirectUrl;

            if ("SUCCESS".equals(completedTx.getStatus().name())) {
                String baseUrl = completedTx.getSuccessUrl();
                redirectUrl = baseUrl.contains("?") ? baseUrl + "&" + tokenParam : baseUrl + "?" + tokenParam;
            } else {
                String baseUrl = completedTx.getFailedUrl();
                redirectUrl = baseUrl.contains("?") ? baseUrl + "&" + tokenParam : baseUrl + "?" + tokenParam;
            }
            return ResponseEntity.ok(redirectUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
