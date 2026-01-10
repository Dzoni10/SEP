package com.payment.paymentserviceprovider.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="web_shpo_subscriptions")

public class WebShopSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true,nullable = false)
    private Integer webShopId;

    @ElementCollection
    @CollectionTable(name="subscription_methods,",joinColumns=@JoinColumn(name="subscription_id"))
    private List<PaymentMethodType> subscribedMethods;

    @Column(nullable = false)
    private LocalDate createdAt;
    @Column(nullable = false)
    private LocalDate updatedAt;
    @Column(nullable = false)
    private boolean active;

    public WebShopSubscription(){}

    public WebShopSubscription(Integer id, Integer webShopId,
                               List<PaymentMethodType> subscribedMethods,
                               LocalDate createdAt, LocalDate updatedAt,
                               boolean active) {
        this.id = id;
        this.webShopId = webShopId;
        this.subscribedMethods = subscribedMethods;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.active = active;
    }

    public WebShopSubscription(Integer webShopId,
                               List<PaymentMethodType> subscribedMethods,
                               LocalDate createdAt, LocalDate updatedAt,
                               boolean active) {

        this.webShopId = webShopId;
        this.subscribedMethods = subscribedMethods;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWebShopId() {
        return webShopId;
    }

    public void setWebShopId(Integer webShopId) {
        this.webShopId = webShopId;
    }

    public List<PaymentMethodType> getSubscribedMethods() {
        return subscribedMethods;
    }

    public void setSubscribedMethods(List<PaymentMethodType> subscribedMethods) {
        this.subscribedMethods = subscribedMethods;
    }
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
