package com.payment.paymentapp.service;

import com.payment.paymentapp.domain.Car;
import com.payment.paymentapp.domain.Order;
import com.payment.paymentapp.domain.OrderItem;
import com.payment.paymentapp.domain.OrderStatus;
import com.payment.paymentapp.dto.CartItem;
import com.payment.paymentapp.repositoryInterfaces.CarRepositoryInterface;
import com.payment.paymentapp.repositoryInterfaces.OrderItemRepository;
import com.payment.paymentapp.repositoryInterfaces.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CarRepositoryInterface carRepositoryInterface;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, CarRepositoryInterface carRepositoryInterface) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.carRepositoryInterface = carRepositoryInterface;
    }

    @Transactional
    public Order createOrder(List<CartItem> items, int userId) {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setCheckoutToken(java.util.UUID.randomUUID().toString());

        double totalAmount = 0;

        for (CartItem item : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setUserId(userId);
            orderItem.setCarId(item.carId());

            Car car = carRepositoryInterface.findById(item.carId()).orElseThrow(()-> new RuntimeException("Car not found"));
            double calculatedPrice = car.getRentPrice()*item.rentalDays();
            orderItem.setPrice(calculatedPrice);
            order.getItems().add(orderItem);
            totalAmount+=calculatedPrice;
        }
        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    @Transactional
    public void updateOrderStatus(int orderId, OrderStatus status) {
        orderRepository.findById(orderId)
                .ifPresent(order -> {
                    order.setStatus(status);
                    orderRepository.save(order);
                });
    }

    public Order getOrderById(int orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderByCheckoutToken(String token) {
        return orderRepository.findByCheckoutToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid checkout token!"));
    }
}
