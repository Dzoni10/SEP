package com.payment.paymentapp.controller;

import com.payment.paymentapp.domain.Order;
import com.payment.paymentapp.domain.OrderStatus;
import com.payment.paymentapp.dto.CartItem;
import com.payment.paymentapp.repositoryInterfaces.OrderItemRepository;
import com.payment.paymentapp.repositoryInterfaces.OrderRepository;
import com.payment.paymentapp.service.CarService;
import com.payment.paymentapp.service.OrderService;
import com.payment.paymentapp.shared.PaymentMethodType;
import com.payment.paymentapp.shared.PaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final RestTemplate restTemplate;

    @Value("${PSP_BASE_URL}")
    private String PSP_BASE_URL;
    private final OrderItemRepository orderItemRepository;
    private static final int WEB_SHOP_ID=1;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final CarService carService;

    public OrderController(RestTemplate restTemplate, OrderItemRepository orderItemRepository, OrderRepository orderRepository,OrderService orderService,CarService carService) {
        this.restTemplate = restTemplate;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.carService = carService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiateCheckout(@RequestBody InitiateRequest request) {
        try {
            CartItem item = new CartItem(request.carId(), 0, request.rentalDays(),request.hasInsurance());
            Order order = orderService.createOrder(List.of(item), request.userId());

            return ResponseEntity.ok(Map.of("checkoutToken", order.getCheckoutToken()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error during initialization" + e.getMessage());
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @RequestBody SecureCheckoutRequest request) {
        try {
            // 1. Kreiraj order u Web Shop-u
            Order order = orderService.getOrderByCheckoutToken(request.checkoutToken());

            String merchantId = "TEST_MERCHANT";
            String merchantPassword = "test";

            Map<String, Object> pspRequest = new HashMap<>();
            pspRequest.put("merchantId", merchantId);
            pspRequest.put("merchantPassword", merchantPassword);
            pspRequest.put("amount", order.getTotalAmount());
            pspRequest.put("currency", "EUR");
            pspRequest.put("merchantOrderId", String.valueOf(order.getId()));
            pspRequest.put("merchantTimeStamp", Instant.now().toString());
            pspRequest.put("successUrl", "https://localhost:4200/payment-success");
            pspRequest.put("failedUrl", "https://localhost:4200/payment-failed");
            pspRequest.put("errorUrl", "https://localhost:4200/payment-error");
            pspRequest.put("paymentMethod", request.paymentMethod());


            // 3. Pošalji zahtev PSP-u
            ResponseEntity<PaymentResponse> pspResponse = restTemplate.postForEntity(
                    PSP_BASE_URL + "/webshop/"+WEB_SHOP_ID+"/pay",
                    pspRequest,
                    PaymentResponse.class
            );

            if (pspResponse.getStatusCode().is2xxSuccessful()) {
                PaymentResponse response = pspResponse.getBody();
                // Ako PSP vrati redirect URL, preusmeri korisnika tamo
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "orderId",order.getId(),
                        "redirectUrl", response.redirectUrl(),
                        "transactionId", response.transactionId(),
                        "totalAmount", order.getTotalAmount()
                ));
            }

            return ResponseEntity.badRequest().body("Payment initiation failed");

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Checkout failed",
                    "message", e.getMessage()
            ));
        }
    }

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

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable int userId) {
        try {
            List<Order> userOrders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(userOrders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

}

record CheckoutRequest(
        List<CartItem> items,
        int userId,
        PaymentMethodType paymentMethod,
        String checkoutToken
) {
}
record PaymentCallbackRequest(
        boolean success,
        String transactionId,
        String errorMessage
) {}

record RentRequest(
        int carId,
        int userId,
        String paymentMethod
) {}

record InitiateRequest(
        int carId,
        int userId,
        int rentalDays,
        boolean hasInsurance
) {}

record SecureCheckoutRequest(
        String checkoutToken,
        String paymentMethod
) {}

