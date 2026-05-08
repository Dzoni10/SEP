package com.payment.paymentapp.controller;

import com.payment.paymentapp.dto.SubscribeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/admin/payment-methods")
public class AdminPaymentController {

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/subscribe")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> subscribeWebShopMethods(@RequestBody SubscribeRequest request)
    {
        String pspUrl = "https://localhost:8081/api/v1/psp/webshop/1/subscribe";
        try{
            ResponseEntity<String> response = restTemplate.postForEntity(pspUrl,request, String.class);
            return ResponseEntity.ok(response.getBody());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error sending subscription request to PSP");
        }
    }

    @GetMapping("/current")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCurrentWebShopMethods() {
        String pspUrl = "https://localhost:8081/api/v1/psp/webshop/1/available-methods";
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(pspUrl, List.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
             return ResponseEntity.ok(List.of());
        }
    }
}
