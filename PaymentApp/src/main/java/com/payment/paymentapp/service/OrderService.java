package com.payment.paymentapp.service;

import com.payment.paymentapp.domain.Order;
import com.payment.paymentapp.domain.OrderItem;
import com.payment.paymentapp.domain.OrderStatus;
import com.payment.paymentapp.dto.CartItem;
import com.payment.paymentapp.repositoryInterfaces.OrderItemRepository;
import com.payment.paymentapp.repositoryInterfaces.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Kreiraj order sa stavkama
     */
    @Transactional
    public Order createOrder(List<CartItem> items, int userId) {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        double totalAmount = 0;

        for (CartItem item : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setUserId(userId);
            orderItem.setCarId(item.carId());
            orderItem.setPrice(item.price());
            order.getItems().add(orderItem);
            totalAmount += orderItem.getPrice();
        }
        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    /**
     * Ažuriraj status order-a nakon plaćanja
     */
    @Transactional
    public void updateOrderStatus(int orderId, OrderStatus status) {
        orderRepository.findById(orderId)
                .ifPresent(order -> {
                    order.setStatus(status);
                    orderRepository.save(order);
                });
    }

    /**
     * Preuzmi order po ID-u
     */
    public Order getOrderById(int orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    /**
     * Preuzmi sve ordere
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }


}
