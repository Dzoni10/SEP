package com.payment.paymentapp.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private double totalAmount;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(unique = true)
    private String checkoutToken;


    public Order() {}

    public Order(int id, double totalAmount,OrderStatus status, List<OrderItem> items,String checkoutToken) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items;
        this.checkoutToken=checkoutToken;
    }

    public Order( double totalAmount,OrderStatus status, List<OrderItem> items,String checkoutToken) {
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items;
        this.checkoutToken=checkoutToken;
    }

    public int getId() {
        return id;
    }
    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getCheckoutToken() {
        return checkoutToken;
    }
    public void setCheckoutToken(String checkoutToken) {
        this.checkoutToken = checkoutToken;
    }
}
