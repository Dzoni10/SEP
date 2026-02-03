package com.bankservice.bank.controller;

import com.bankservice.bank.domain.*;
import com.bankservice.bank.service.BankService;
import com.bankservice.bank.service.CardValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bank")
@CrossOrigin(origins = {"http://localhost:4400", "http://localhost:4200"})
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/payment-url")
    public ResponseEntity<PaymentUrlResponse> getPaymentUrl(@RequestBody PaymentUrlRequest request) {
        if (request.merchantId() == null || request.merchantId().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        PaymentUrlResponse response = bankService.generatePaymentUrl(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process-payment")
    public ResponseEntity<PaymentProcessResponse> processPayment(@RequestBody CardPaymentRequest request) {
        if (!CardValidator.validateLuhn(request.pan())) {
            PaymentTransaction transaction = bankService.getTransaction(request.paymentId());
            String errorUrl = transaction != null && transaction.getErrorUrl() != null ?
                transaction.getErrorUrl() + "?error=Invalid card number" : null;
            return ResponseEntity.badRequest()
                .body(new PaymentProcessResponse(false, "Invalid card number", null, null, null, errorUrl));
        }

        if (!CardValidator.validateExpiryDate(request.expiryDate())) {
            PaymentTransaction transaction = bankService.getTransaction(request.paymentId());
            String errorUrl = transaction != null && transaction.getErrorUrl() != null ?
                transaction.getErrorUrl() + "?error=Invalid expiry date" : null;
            return ResponseEntity.badRequest()
                .body(new PaymentProcessResponse(false, "Invalid expiry date", null, null, null, errorUrl));
        }

        PaymentProcessResponse response = bankService.processCardPayment(request);
        return response.success() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/payment/{paymentId}/callback")
    public ResponseEntity<?> updateTransactionCallback(
            @PathVariable String paymentId,
            @RequestBody UpdateCallbackRequest request) {
        bankService.updateTransactionWithCallback(
            paymentId, request.callbackUrl(), request.orderId(),
            request.successUrl(), request.failedUrl(), request.errorUrl()
        );
        return ResponseEntity.ok().build();
    }
}
