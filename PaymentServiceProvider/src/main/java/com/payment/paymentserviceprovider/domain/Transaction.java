package com.payment.paymentserviceprovider.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="transaction")
public class Transaction{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true,nullable = false)
    private int webShopId;

    @Column(unique = true,nullable = false)
    private int orderId;

    @Column(nullable = false)
    private PaymentMethodType method;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private String externalTransactionId;

    @Column(nullable = false)
    private LocalDate createdAt;

    @Column(nullable = false)
    private LocalDate completedAt;

    @Column(nullable = false)
    private String errorMessage;

    public Transaction() {}

    public Transaction(Integer id, int webShopId, int orderId, PaymentMethodType method, double amount, String currency, TransactionStatus status, String externalTransactionId, LocalDate createdAt, LocalDate completedAt, String errorMessage) {
        this.id = id;
        this.webShopId = webShopId;
        this.orderId = orderId;
        this.method = method;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.externalTransactionId = externalTransactionId;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.errorMessage = errorMessage;
    }

    public Transaction(int webShopId, int orderId, PaymentMethodType method, double amount, String currency, TransactionStatus status, String externalTransactionId, LocalDate createdAt, LocalDate completedAt, String errorMessage){
        this.webShopId = webShopId;
        this.orderId = orderId;
        this.method = method;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.externalTransactionId = externalTransactionId;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.errorMessage = errorMessage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getWebShopId() {
        return webShopId;
    }

    public void setWebShopId(int webShopId) {
        this.webShopId = webShopId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public PaymentMethodType getMethod() {
        return method;
    }

    public void setMethod(PaymentMethodType method) {
        this.method = method;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getExternalTransactionId() {
        return externalTransactionId;
    }

    public void setExternalTransactionId(String externalTransactionId) {
        this.externalTransactionId = externalTransactionId;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDate completedAt) {
        this.completedAt = completedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
