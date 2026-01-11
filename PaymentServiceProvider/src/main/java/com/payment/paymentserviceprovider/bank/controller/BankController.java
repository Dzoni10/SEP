package com.payment.paymentserviceprovider.bank.controller;

import com.payment.paymentserviceprovider.bank.domain.*;
import com.payment.paymentserviceprovider.bank.service.BankService;
import com.payment.paymentserviceprovider.bank.service.CardValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bank")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    /**
     * Endpoint za dobijanje PAYMENT_URL i PAYMENT_ID
     * PSP poziva ovaj endpoint sa parametrima iz Tabele 2
     */
    @PostMapping("/payment-url")
    public ResponseEntity<PaymentUrlResponse> getPaymentUrl(
            @RequestBody PaymentUrlRequest request) {
        
        // 1. Validacija PSP-a (za sada jednostavna, kasnije HMAC)
        // TODO: Dodati HMAC validaciju
        
        // 2. Validacija MERCHANT_ID (za banku)
        if (request.merchantId() == null || request.merchantId().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // 3. Generisanje PAYMENT_URL i PAYMENT_ID
        PaymentUrlResponse response = bankService.generatePaymentUrl(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint za procesiranje plaćanja
     * Korisnik unosi podatke kartice na formi, forma šalje ovde
     */
    @PostMapping("/process-payment")
    public ResponseEntity<PaymentProcessResponse> processPayment(
            @RequestBody CardPaymentRequest request) {
        
        // 1. Validacija kartice (Lunova formula)
        if (!CardValidator.validateLuhn(request.pan())) {
            PaymentTransaction transaction = bankService.getTransaction(request.paymentId());
            String errorUrl = transaction != null && transaction.getErrorUrl() != null ?
                transaction.getErrorUrl() + "?error=Invalid card number" : null;
            return ResponseEntity.badRequest()
                .body(new PaymentProcessResponse(
                    false, 
                    "Invalid card number", 
                    null, 
                    null,
                    null,
                    errorUrl
                ));
        }
        
        // 2. Validacija datuma (MM/YY)
        if (!CardValidator.validateExpiryDate(request.expiryDate())) {
            PaymentTransaction transaction = bankService.getTransaction(request.paymentId());
            String errorUrl = transaction != null && transaction.getErrorUrl() != null ?
                transaction.getErrorUrl() + "?error=Invalid expiry date" : null;
            return ResponseEntity.badRequest()
                .body(new PaymentProcessResponse(
                    false, 
                    "Invalid expiry date", 
                    null, 
                    null,
                    null,
                    errorUrl
                ));
        }
        
        // 3. Provera stanja računa i rezervacija sredstava
        PaymentProcessResponse response = bankService.processCardPayment(request);
        
        if (!response.success()) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint za ažuriranje transakcije sa callbackUrl i orderId
     * PSP poziva ovaj endpoint nakon što dobije PAYMENT_URL
     */
    @PutMapping("/payment/{paymentId}/callback")
    public ResponseEntity<?> updateTransactionCallback(
            @PathVariable String paymentId,
            @RequestBody UpdateCallbackRequest request) {
        
        bankService.updateTransactionWithCallback(
            paymentId, 
            request.callbackUrl(), 
            request.orderId(),
            request.successUrl(),
            request.failedUrl(),
            request.errorUrl()
        );
        
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint za callback rezultata plaćanja
     * Banka šalje rezultat PSP-u
     */
    @PostMapping("/payment-result")
    public ResponseEntity<?> paymentResult(
            @RequestBody PaymentResultRequest request) {
        
        // Ažuriranje transakcije i poziv Web Shop callback-a
        bankService.handlePaymentResult(request);
        
        return ResponseEntity.ok().build();
    }
}
