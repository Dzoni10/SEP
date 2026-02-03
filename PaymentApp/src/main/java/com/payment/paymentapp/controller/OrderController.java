package com.payment.paymentapp.controller;

import com.payment.paymentapp.domain.Order;
import com.payment.paymentapp.domain.OrderStatus;
import com.payment.paymentapp.dto.CartItem;
import com.payment.paymentapp.repositoryInterfaces.OrderItemRepository;
import com.payment.paymentapp.repositoryInterfaces.OrderRepository;
import com.payment.paymentapp.service.OrderService;
import com.payment.paymentapp.shared.PaymentMethodType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderItemRepository orderItemRepository;
    private static final int WEB_SHOP_ID=1;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public OrderController(OrderItemRepository orderItemRepository, OrderRepository orderRepository, OrderService orderService) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    /**
     * Kreiraj order i vrati URL za preusmeravanje na PSP (gde korisnik bira način plaćanja)
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @RequestBody CheckoutRequest request) {
        try {
            // 1. Kreiraj order u Web Shop-u
            Order order = orderService.createOrder(request.items(), request.userId());

            // 2. Formiraj URL za PSP front - korisnik tamo bira način plaćanja (CARD, QR, itd.)
            String callbackUrl = "http://localhost:8080/api/v1/orders/" + order.getId() + "/payment-callback";
            String successUrl = "http://localhost:4200/webshop?payment=success";
            String failedUrl = "http://localhost:4200/webshop?payment=failed";
            String errorUrl = "http://localhost:4200/webshop?payment=error";

            String pspRedirectUrl = "http://localhost:4300/payment" +
                    "?orderId=" + order.getId() +
                    "&webShopId=" + WEB_SHOP_ID +
                    "&amount=" + order.getTotalAmount() +
                    "&currency=EUR" +
                    "&callbackUrl=" + java.net.URLEncoder.encode(callbackUrl, java.nio.charset.StandardCharsets.UTF_8) +
                    "&successUrl=" + java.net.URLEncoder.encode(successUrl, java.nio.charset.StandardCharsets.UTF_8) +
                    "&failedUrl=" + java.net.URLEncoder.encode(failedUrl, java.nio.charset.StandardCharsets.UTF_8) +
                    "&errorUrl=" + java.net.URLEncoder.encode(errorUrl, java.nio.charset.StandardCharsets.UTF_8) +
                    "&userId=" + request.userId();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "orderId", order.getId(),
                    "redirectUrl", pspRedirectUrl,
                    "totalAmount", order.getTotalAmount()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Checkout failed",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * PSP vraća rezultat plaćanja na ovaj callback
     */
    @PostMapping("/{orderId}/payment-callback")
    public ResponseEntity<?> paymentCallback(
            @PathVariable int orderId,
            @RequestBody PaymentCallbackRequest callback) {
        try {
            Order order = orderService.getOrderById(orderId);

            if (callback.success()) {
                // Plaćanje je uspešno
                orderService.updateOrderStatus(orderId, OrderStatus.PAID);

                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Order confirmed",
                        "orderId", orderId,
                        "transactionId", callback.transactionId()
                ));
            } else {
                // Plaćanje je neuspešno
                orderService.updateOrderStatus(orderId, OrderStatus.PAYMENT_FAILED);

                return ResponseEntity.badRequest().body(Map.of(
                        "status", "failed",
                        "message", callback.errorMessage(),
                        "orderId", orderId
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Callback processing failed",
                    "message", e.getMessage()
            ));
        }
    }
    /**
     * Preuzmi order po ID-u
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable int orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(Map.of(
                    "id", order.getId(),
                    "totalAmount", order.getTotalAmount(),
                    "status", order.getStatus(),
                    "items", order.getItems()
            ));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Preuzmi sve ordere
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}

record CheckoutRequest(
        List<CartItem> items,
        int userId,
        PaymentMethodType paymentMethod
) {
}
record PaymentCallbackRequest(
        boolean success,
        String transactionId,
        String errorMessage
) {}
