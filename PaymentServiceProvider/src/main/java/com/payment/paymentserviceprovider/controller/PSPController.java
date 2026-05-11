package com.payment.paymentserviceprovider.controller;

import com.payment.paymentserviceprovider.domain.*;
import com.payment.paymentserviceprovider.dto.BankWebhookRequest;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;
import com.payment.paymentserviceprovider.service.PaymentProcessingService;
import com.payment.paymentserviceprovider.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/psp")
public class PSPController {

    private final PaymentProcessingService paymentService;
    private final SubscriptionService subscriptionService;

    @Value("${psp.password-secret:password}")
    private String secretKey;

    public PSPController(PaymentProcessingService paymentService,
                         SubscriptionService subscriptionService) {
        this.paymentService = paymentService;
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/webshop/{webShopId}/subscribe")
    public ResponseEntity<?> subscribeToMethods(
            @PathVariable int webShopId,
            @RequestBody SubscribeRequest request) {
        try {
            subscriptionService.subscribeToPaymentMethods(webShopId, request.methods());
            return ResponseEntity.ok("Subscription successful");
        } catch (PaymentPluginException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/webshop/{webShopId}/pay")
    public ResponseEntity<?> initiatePayment(
            @PathVariable int webShopId,
            @RequestBody PaymentInitiationRequest request) {
        try {

            String successUrl = (request.successUrl() != null) ? request.successUrl() : "https://localhost:4200/payment-success";
            String failedUrl = (request.failedUrl() != null) ? request.failedUrl() : "https://localhost:4200/payment-failed";
            String errorUrl = (request.errorUrl() != null) ? request.errorUrl() : "https://localhost:4200/payment-error";
            String pspCallbackUrl = "https://localhost:8081/api/v1/psp/webhook/bank";

            int orderId = Integer.parseInt(request.merchantOrderId());

            PaymentRequest paymentReq = new PaymentRequest(
                    webShopId,
                    orderId,
                    request.amount(),
                    request.currency(),
                    pspCallbackUrl,
                    successUrl,
                    failedUrl,
                    errorUrl,
                    new HashMap<>()
            );

            PaymentResponse response = paymentService.initiatePayment(
                    webShopId,
                    paymentReq,
                    request.paymentMethod()
            );

            return ResponseEntity.ok(response);
        } catch (PaymentPluginException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/webshop/{webShopId}/available-methods")
    public ResponseEntity<?> getAvailableMethods(@PathVariable int webShopId) {
        try {
            List<PaymentMethodType> methods =
                    subscriptionService.getAvailableMethodsForWebShop(webShopId);
            return ResponseEntity.ok(methods);
        } catch (PaymentPluginException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/webhook/bank")
    public ResponseEntity<?> handleBankWebhook(@RequestBody BankWebhookRequest request, @RequestHeader(value = "X-HMAC-Signature",required = false) String providedSignature) {
        try {
            if(providedSignature == null || providedSignature.trim().isEmpty()) {
                System.err.println("Rejected webhook: Need HMAC signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("There are no HMAC signature");
            }

            String statusStr = request.status() != null ? request.status().toString() : "";
            String rawData = request.stan() + statusStr;
            String calculateSignature = calculateHmac(rawData);

            if(!calculateSignature.equals(providedSignature)) {
               System.err.println("Rejected webhook: Signature is invalid");
               return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Signature is invalid");
            }
            paymentService.handleBankCallback(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error during processing bank webhook: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/paypal/success")
    public ResponseEntity<?> capturePayPalPayment(
            @RequestParam("token") String token,
            @RequestParam("PayerID") String payerId) {
        try {
            boolean isCaptured = paymentService.capturePayPalOrder(token);
            Transaction transaction = paymentService.getTransactionByExternalId(token);

            HttpHeaders headers = new HttpHeaders();
            if (isCaptured) {
                String redirectUrl = transaction.getSuccessUrl() + "?txId=" +transaction.getExternalTransactionId();
                headers.setLocation(URI.create(redirectUrl));
            } else {
                String redirectUrl = transaction.getErrorUrl() + "?txId=" + transaction.getExternalTransactionId();
                headers.setLocation(URI.create(redirectUrl));
            }
            return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 Redirect

        } catch (Exception e) {
            System.err.println("PayPal error: " + e.getMessage());
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("https://localhost:4200/payment-failed"));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    @GetMapping("/paypal/cancel")
    public ResponseEntity<?> cancelPayPalPayment(@RequestParam("token") String token) {
        Transaction transaction = paymentService.cancelPayPalOrder(token);

        HttpHeaders headers = new HttpHeaders();
        if(transaction!=null){
            headers.setLocation(URI.create(transaction.getFailedUrl()));
        }
        else{
            headers.setLocation(URI.create("https://localhost:4200/payment-failed"));
        }
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @PostMapping(value = "/webhook/coingate",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleCoingateWebhook(@RequestBody Map<String,Object> payload){
        try{
            String externalId = String.valueOf(payload.get("id"));
            String status = String.valueOf(payload.get("status"));

            System.out.println("CoingGate webhook ID: "+ externalId + " Status: " + status);
            paymentService.handleCoinGateCallback(externalId, status);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            System.err.println("Error during processing Webook: " + e.getMessage());
            return ResponseEntity.badRequest().build();
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
