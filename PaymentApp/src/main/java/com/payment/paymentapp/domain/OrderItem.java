package com.payment.paymentapp.domain;

import jakarta.persistence.*;

@Entity
@Table(name="order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name="order_id")
    private Order order;

    @Column(name = "user_id")
    private int userId;

    @Column(nullable = false)
    private int carId;

    @Column(nullable = false)
    private double price;


    public OrderItem() {}

    public OrderItem(int id, Order order,int userId, int carId, double price) {
        this.id = id;
        this.order = order;
        this.userId = userId;
        this.carId =carId;
        this.price = price;
    }

    public OrderItem( Order order,int userId, int carId, double price) {
        this.order = order;
        this.userId = userId;
        this.carId = carId;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
