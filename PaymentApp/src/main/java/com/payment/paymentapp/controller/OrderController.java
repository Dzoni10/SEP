package com.payment.paymentapp.controller;

import com.payment.paymentapp.domain.Order;
import com.payment.paymentapp.domain.OrderItem;
import com.payment.paymentapp.domain.OrderStatus;
import com.payment.paymentapp.dto.CartItem;
import com.payment.paymentapp.repositoryInterfaces.OrderItemRepository;
import com.payment.paymentapp.repositoryInterfaces.OrderRepository;
import com.payment.paymentapp.service.OrderService;
import com.payment.paymentapp.shared.PaymentInitiationRequest;
import com.payment.paymentapp.shared.PaymentMethodType;
import com.payment.paymentapp.shared.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController("/api/v1/orders")
public class OrderController {

    private final RestTemplate restTemplate;
    private static final String PSP_BASE_URL = "http://localhost:8081/api/v1/psp";
    private final OrderItemRepository orderItemRepository;
    private static final int WEB_SHOP_ID=1;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public OrderController(RestTemplate restTemplate, OrderItemRepository orderItemRepository, OrderRepository orderRepository,OrderService orderService) {
        this.restTemplate = restTemplate;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    /**
     * Kreiraj order i počni plaćanje
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @RequestBody CheckoutRequest request) {
        try {
            // 1. Kreiraj order u Web Shop-u
            Order order = orderService.createOrder(request.items(),request.userId());

            // 2. Preusmerige korisnika na PSP za plaćanje
            PaymentInitiationRequest pspRequest = new PaymentInitiationRequest(
                    order.getId(),
                    order.getTotalAmount(),
                    "EUR",
                    request.paymentMethod(),
                    "http://localhost:8080/api/v1/orders/" + order.getId() + "/payment-callback",
                    Map.of("orderId", String.valueOf(order.getId()),"userId",String.valueOf(request.userId()))
            );

            // 3. Pošalji zahtev PSP-u
            ResponseEntity<PaymentResponse> pspResponse = restTemplate.postForEntity(
                    PSP_BASE_URL + "/webshop/"+WEB_SHOP_ID+"/pay",  // webShopId = 1
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



    private Order createOrder(int orderId, CheckoutRequest request) {
        // Kreiraj order
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);

        // Izračunaj ukupnu cenu
        double totalAmount =0;

        // Kreiraj stavke
        for (CartItem item : request.items()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setCarId(item.carId());
            orderItem.setPrice(item.price());

            orderItemRepository.save(orderItem);

            totalAmount += orderItem.getPrice();
        }

        order.setTotalAmount(totalAmount);

        // Sačuvaj order
        return orderRepository.save(order);
    }
}

record CheckoutRequest(
        List<CartItem> items,
        PaymentMethodType paymentMethod
) {
}
record PaymentCallbackRequest(
        boolean success,
        String transactionId,
        String errorMessage
) {}
