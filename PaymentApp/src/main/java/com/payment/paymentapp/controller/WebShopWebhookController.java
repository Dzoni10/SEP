package com.payment.paymentapp.controller;

import com.payment.paymentapp.domain.OrderStatus;
import com.payment.paymentapp.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/orders")
public class WebShopWebhookController {

    private final OrderService orderService;

    public WebShopWebhookController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable int orderId, @RequestBody Map<String,String> payload) {

        String status = payload.get("status");

        if ("SUCCESS".equals(status)) {
            // Pronađi narudžbinu u bazi Web Shop-a i stavi joj status PAID / ZAVRŠENO
            orderService.updateOrderStatus(orderId, OrderStatus.PAID);
            System.out.println("Order " + orderId + " successfull payment");
        } else {
            orderService.updateOrderStatus(orderId,OrderStatus.CANCELLED);
            System.out.println("Order " + orderId + " - payment denied.");
        }
        return ResponseEntity.ok().build();
    }
}
