package com.payment.paymentserviceprovider.controller;

import com.payment.paymentserviceprovider.domain.*;
import com.payment.paymentserviceprovider.exception.PaymentPluginException;
import com.payment.paymentserviceprovider.service.PaymentProcessingService;
import com.payment.paymentserviceprovider.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/psp")
public class PSPController {

    private final PaymentProcessingService paymentService;
    private final SubscriptionService subscriptionService;


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
            // Ekstraktovanje SUCCESS_URL, FAILED_URL, ERROR_URL iz metadata ili direktno
            // Za sada ćemo koristiti hardkodovane URL-ove, kasnije iz metadata
            String successUrl = request.metadata() != null ? 
                request.metadata().getOrDefault("successUrl", "http://localhost:4200/payment-success") : 
                "http://localhost:4200/payment-success";
            String failedUrl = request.metadata() != null ? 
                request.metadata().getOrDefault("failedUrl", "http://localhost:4200/payment-failed") : 
                "http://localhost:4200/payment-failed";
            String errorUrl = request.metadata() != null ? 
                request.metadata().getOrDefault("errorUrl", "http://localhost:4200/payment-error") : 
                "http://localhost:4200/payment-error";
            
            PaymentRequest paymentReq = new PaymentRequest(
                    webShopId,
                    request.orderId(),
                    request.amount(),
                    request.currency(),
                    request.callbackUrl(),
                    successUrl,
                    failedUrl,
                    errorUrl,
                    request.metadata()
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
}
